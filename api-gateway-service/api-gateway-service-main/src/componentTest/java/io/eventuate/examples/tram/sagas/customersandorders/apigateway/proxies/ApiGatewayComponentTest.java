package io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"customer.destinations.customerServiceUrl=http://localhost:${wiremock.server.port}", "order.destinations.orderServiceUrl=http://localhost:${wiremock.server.port}"})
@AutoConfigureWireMock(port = 0)
public class ApiGatewayComponentTest {

  @Configuration
  @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
  @Import(ProxyConfiguration.class)
  static public class Config {
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
  public void shouldGetOrderHistory() throws JSONException, JsonProcessingException {

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