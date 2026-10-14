package io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.json.JSONArray;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiGatewayComponentTest {

  @Configuration
  @EnableAutoConfiguration
  @Import(ProxyConfiguration.class)
  static public class Config {
  }

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
  public void shouldGetOrder() {

    var expectedResponse = "hello";

    StubMapping stubMapping = stubFor(get(urlEqualTo("/orders/101"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(expectedResponse)));

    String response = webClient.get().uri("http://localhost:" + port + "/orders/101").retrieve().bodyToMono(String.class).block();

    assertEquals(expectedResponse, response);

    verify(getRequestedFor(urlMatching("/orders/101")));
  }

  @Test
  public void shouldGetOrders() {

    var expectedResponse = "hello-every-order";

    StubMapping stubMapping = stubFor(get(urlEqualTo("/orders"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(expectedResponse)));

    String response = webClient.get().uri("http://localhost:" + port + "/orders").retrieve().bodyToMono(String.class).block();

    assertEquals(expectedResponse, response);

    verify(getRequestedFor(urlMatching("/orders")));
  }

  @Test
  public void shouldGetOrderHistory() throws JSONException, JacksonException {

    var customerJSon = new JSONObject();
    customerJSon.put("customerId", 101);
    customerJSon.put("name", "Fred");
    customerJSon.put("creditLimit", "12.34");

    var ordersJSon = new JSONArray();
    ordersJSon.put(new JSONObject().put("orderId", 1).put("orderState", "APPROVED").put("rejectionReason", JSONObject.NULL));


    stubFor(get(urlEqualTo("/orders/customer/5"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(ordersJSon.toString())));

    stubFor(get(urlEqualTo("/customers/5"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(customerJSon.toString())));

    var mapper = new ObjectMapper();

    var response = webClient.get().uri("http://localhost:" + port + "/customers/5/orderhistory").retrieve().bodyToMono(String.class).block();

    customerJSon.put("orders", ordersJSon);
    customerJSon.put("creditLimit", new JSONObject().put("amount", 12.34));
    assertEquals(mapper.readTree(customerJSon.toString()), mapper.readTree(response));

    verify(getRequestedFor(urlMatching("/customers/5")));
    verify(getRequestedFor(urlMatching("/orders/customer/5")));
  }

}