package io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies;

import io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies.common.UnknownProxyException;
import io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies.customerservice.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies.customerservice.GetCustomerResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = CustomerServiceProxyTest.Config.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerServiceProxyTest {

  @Configuration
  @EnableAutoConfiguration
  @Import(ProxyConfiguration.class)
  static public class Config {
  }

  @Autowired
  private CustomerServiceProxy customerServiceProxy;

  private static final WireMockServer wireMockServer = WireMockUtil.mockOnDynamicPort();

  @DynamicPropertySource
  static void registerWireMockProperties(DynamicPropertyRegistry registry) {
    wireMockServer.start();
    registry.add("customer.destinations.customerServiceUrl", WireMockUtil.makeServiceUrlSupplier(wireMockServer));
    registry.add("order.destinations.orderServiceUrl", WireMockUtil.makeServiceUrlSupplier(wireMockServer));
  }

  @BeforeEach
  public void configureWireMock() {
    WireMock.configureFor(wireMockServer.port());
  }

  @AfterEach
  public void resetWireMock() {
    wireMockServer.resetAll();
  }

  @LocalServerPort
  private long port;

  @Autowired
  private WebClient webClient;

  @Test
  public void shouldCallCustomerService() throws JSONException {

    JSONObject json = new JSONObject();
    json.put("customerId", 101);
    json.put("name", "Fred");
    json.put("creditLimit", "12.34");

    String expectedResponse = json.toString();

    stubFor(get(urlEqualTo("/customers/101"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(expectedResponse)));

    GetCustomerResponse customer = customerServiceProxy.findCustomerById("101").block().get();

    assertEquals(Long.valueOf(101L), customer.customerId());

    verify(getRequestedFor(urlMatching("/customers/101")));
  }

  @Test
  public void shouldTimeoutAndTripCircuitBreaker() {
    assertThrows(CallNotPermittedException.class, () -> {

      String expectedResponse = "{}";

      stubFor(get(urlEqualTo("/customers/99"))
              .willReturn(aResponse()
                      .withStatus(500)
                      .withHeader("Content-Type", "application/json")
                      .withBody(expectedResponse)));


      IntStream.range(0, 100).forEach(i -> {
                try {
                  customerServiceProxy.findCustomerById("99").block();
                } catch (CallNotPermittedException e) {
                  throw e;
                } catch (UnknownProxyException e) {
                  //
                }
              }
      );

      verify(getRequestedFor(urlMatching("/customers/99")));
    });
  }
}