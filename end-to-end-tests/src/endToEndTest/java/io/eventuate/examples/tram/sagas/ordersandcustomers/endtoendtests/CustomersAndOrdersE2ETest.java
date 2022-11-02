package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.api.web.GetCustomerHistoryResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.CreateCustomerRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.CreateCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.OrderState;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.RejectionReason;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.CreateOrderRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.CreateOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.GetOrderResponse;
import io.eventuate.util.test.async.Eventually;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomersAndOrdersE2ETestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomersAndOrdersE2ETest {

  private static final String CUSTOMER_NAME = "John";

  @Value("${host.name}")
  private String hostName;

  private String baseUrl(String path, String... pathElements) {
    assertNotNull("host", hostName);

    StringBuilder sb = new StringBuilder("http://");
    sb.append(hostName);
    sb.append(":");
    sb.append(8083);
    sb.append("/");
    sb.append(path);

    for (String pe : pathElements) {
      sb.append("/");
      sb.append(pe);
    }
    String s = sb.toString();
    return s;
  }

  @Autowired
  RestTemplate restTemplate;

  @Test
  public void shouldApprove() {
    CreateCustomerResponse createCustomerResponse = restTemplate.postForObject(baseUrl("customers"),
            new CreateCustomerRequest(CUSTOMER_NAME, new Money("15.00")), CreateCustomerResponse.class);

    CreateOrderResponse createOrderResponse = restTemplate.postForObject(baseUrl("orders"),
            new CreateOrderRequest(createCustomerResponse.getCustomerId(), new Money("12.34")), CreateOrderResponse.class);

    assertOrderState(createOrderResponse.getOrderId(), OrderState.APPROVED, null);
  }

  @Test
  public void shouldRejectBecauseOfInsufficientCredit() {
    CreateCustomerResponse createCustomerResponse = restTemplate.postForObject(baseUrl("customers"),
            new CreateCustomerRequest(CUSTOMER_NAME, new Money("15.00")), CreateCustomerResponse.class);

    CreateOrderResponse createOrderResponse = restTemplate.postForObject(baseUrl("orders"),
            new CreateOrderRequest(createCustomerResponse.getCustomerId(), new Money("123.40")), CreateOrderResponse.class);

    assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED, RejectionReason.INSUFFICIENT_CREDIT);
  }

  @Test
  public void shouldRejectBecauseOfUnknownCustomer() {

    CreateOrderResponse createOrderResponse = restTemplate.postForObject(baseUrl("orders"),
            new CreateOrderRequest(Long.MAX_VALUE, new Money("123.40")), CreateOrderResponse.class);

    assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED, RejectionReason.UNKNOWN_CUSTOMER);
  }

  @Test
  public void shouldSupportOrderHistory() {
    CreateCustomerResponse createCustomerResponse = restTemplate.postForObject(baseUrl("customers"),
            new CreateCustomerRequest(CUSTOMER_NAME, new Money("1000.00")), CreateCustomerResponse.class);

    CreateOrderResponse createOrderResponse = restTemplate.postForObject(baseUrl("orders"),
            new CreateOrderRequest(createCustomerResponse.getCustomerId(), new Money("100.00")),
            CreateOrderResponse.class);

    Eventually.eventually(() -> {
      ResponseEntity<GetCustomerHistoryResponse> customerResponseEntity =
              restTemplate.getForEntity(baseUrl("customers", Long.toString(createCustomerResponse.getCustomerId()), "orderhistory"),
                      GetCustomerHistoryResponse.class);

      assertEquals(HttpStatus.OK, customerResponseEntity.getStatusCode());

      GetCustomerHistoryResponse customerResponse = customerResponseEntity.getBody();

      assertEquals(new Money("1000.00").getAmount().setScale(2), customerResponse.getCreditLimit().getAmount().setScale(2));
      assertEquals(createCustomerResponse.getCustomerId(), customerResponse.getCustomerId());
      assertEquals(CUSTOMER_NAME, customerResponse.getName());
      assertEquals(1, customerResponse.getOrders().size());
      assertEquals((Long) createOrderResponse.getOrderId(), customerResponse.getOrders().get(0).getOrderId());
      assertEquals(OrderState.APPROVED, customerResponse.getOrders().get(0).getOrderState());
    });
  }

  private void assertOrderState(Long id, OrderState expectedState, RejectionReason expectedRejectionReason) {
    Eventually.eventually(() -> {
      ResponseEntity<GetOrderResponse> getOrderResponseEntity = restTemplate.getForEntity(baseUrl("orders/" + id), GetOrderResponse.class);
      assertEquals(HttpStatus.OK, getOrderResponseEntity.getStatusCode());
      GetOrderResponse order = getOrderResponseEntity.getBody();
      assertEquals(expectedState, order.getOrderState());
      assertEquals(expectedRejectionReason, order.getRejectionReason());
    });
  }

  @Test(expected = HttpClientErrorException.NotFound.class)
  public void shouldHandleOrderHistoryQueryForUnknownCustomer() {
    restTemplate.getForEntity(baseUrl("customers", Long.toString(System.currentTimeMillis()), "orderhistory"),
            GetCustomerHistoryResponse.class);
  }

  @Test
  public void testSwaggerUiUrls() throws IOException {
    testSwaggerUiUrl(8081);
    testSwaggerUiUrl(8082);
    testSwaggerUiUrl(8083);
  }

  private void testSwaggerUiUrl(int port) throws IOException {
    assertUrlStatusIsOk(String.format("http://%s:%s/swagger-ui/index.html", hostName, port));
  }

  private void assertUrlStatusIsOk(String url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    if (connection.getResponseCode() != 200)
      Assert.fail(String.format("Expected 200 for %s, got %s", url, connection.getResponseCode()));
 }
}