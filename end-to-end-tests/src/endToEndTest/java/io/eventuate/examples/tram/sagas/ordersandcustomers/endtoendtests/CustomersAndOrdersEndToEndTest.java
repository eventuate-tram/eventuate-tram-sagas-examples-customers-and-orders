package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.api.web.GetCustomerHistoryResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.CreateCustomerRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.CreateCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.GetCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.GetCustomersResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.OrderState;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.RejectionReason;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.CreateOrderRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.CreateOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.GetOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.GetOrdersResponse;
import io.eventuate.util.test.async.Eventually;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@SpringBootTest(classes = CustomersAndOrdersEndToEndTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomersAndOrdersEndToEndTest {

    // Service
    /// Outbox/producer properties:
    ////  eventuate.tram.outbox.partitioning.outbox.tables
    ////  eventuate.tram.outbox.partitioning.message.partitions
    /// Flyway configuration (same as above)

    // CDC Polling configuration

    private static Logger logger = LoggerFactory.getLogger(CustomersAndOrdersEndToEndTest.class);

    private static ApplicationUnderTest applicationUnderTest = ApplicationUnderTest.make();
    private final Money orderTotalUnderCreditLimit = new Money("12.34");
    private final Money orderTotalOverCreditLimit = new Money("123.40");
    private Money creditLimit = new Money("15.00");


    @BeforeClass
    public static void startContainers() {
        applicationUnderTest.start();
    }



    private static final String CUSTOMER_NAME = "John";

    @Value("${host.name}")
    private String hostName;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void shouldGetCustomers() {
        GetCustomersResponse customers = restTemplate.getForObject(applicationUnderTest.apiGatewayBaseUrl(hostName, "customers"), GetCustomersResponse.class);
        assertNotNull(customers);
    }
    @Test
    public void shouldGetOrder() {
        GetOrdersResponse orders = restTemplate.getForObject(applicationUnderTest.apiGatewayBaseUrl(hostName, "orders"), GetOrdersResponse.class);
        assertNotNull(orders);
    }
    @Test
    public void shouldApprove() {
        CreateCustomerResponse createCustomerResponse = createCustomer();

        assertCustomerHasCreditLimit(createCustomerResponse.getCustomerId());

        CreateOrderResponse createOrderResponse = createOrder(createCustomerResponse.getCustomerId(), orderTotalUnderCreditLimit);

        assertOrderState(createOrderResponse.getOrderId(), OrderState.APPROVED, null);
    }

    @Nullable
    private CreateOrderResponse createOrder(Long customerId, Money orderTotal) {
        return restTemplate.postForObject(applicationUnderTest.apiGatewayBaseUrl(hostName, "orders"),
                new CreateOrderRequest(customerId, orderTotal), CreateOrderResponse.class);
    }

    @Nullable
    private CreateCustomerResponse createCustomer() {
        return restTemplate.postForObject(applicationUnderTest.apiGatewayBaseUrl(hostName, "customers"),
                new CreateCustomerRequest(CUSTOMER_NAME, creditLimit), CreateCustomerResponse.class);
    }

    private void assertCustomerHasCreditLimit(long id) {
        GetCustomerResponse customer = restTemplate.getForObject(applicationUnderTest.apiGatewayBaseUrl(hostName, "customers/" + id), GetCustomerResponse.class);
        assertEquals(creditLimit, customer.getCreditLimit());

    }

    @Test
    public void shouldRejectBecauseOfInsufficientCredit() {
        CreateCustomerResponse createCustomerResponse = createCustomer();

        CreateOrderResponse createOrderResponse = createOrder(createCustomerResponse.getCustomerId(), orderTotalOverCreditLimit);

        assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED, RejectionReason.INSUFFICIENT_CREDIT);
    }

    @Test
    public void shouldRejectBecauseOfUnknownCustomer() {

        CreateOrderResponse createOrderResponse = createOrder(Long.MAX_VALUE, new Money("123.40"));

        assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED, RejectionReason.UNKNOWN_CUSTOMER);
    }

    @Test
    public void shouldSupportOrderHistory() {

        CreateCustomerResponse createCustomerResponse = createCustomer();

        CreateOrderResponse createOrderResponse = createOrder(createCustomerResponse.getCustomerId(), orderTotalUnderCreditLimit);

        Eventually.eventually(() -> {
            GetCustomerHistoryResponse customerResponse = getOrderHistory(createCustomerResponse.getCustomerId());

            assertEquals(creditLimit.getAmount().setScale(2), customerResponse.getCreditLimit().getAmount().setScale(2));
            assertEquals(createCustomerResponse.getCustomerId(), customerResponse.getCustomerId());
            assertEquals(CUSTOMER_NAME, customerResponse.getName());
            assertEquals(1, customerResponse.getOrders().size());

            assertEquals((Long) createOrderResponse.getOrderId(), customerResponse.getOrders().get(0).getOrderId());
            assertEquals(OrderState.APPROVED, customerResponse.getOrders().get(0).getOrderState());
        });
    }

    @Nullable
    private GetCustomerHistoryResponse getOrderHistory(Long customerId) {
        ResponseEntity<GetCustomerHistoryResponse> customerResponseEntity =
                restTemplate.getForEntity(applicationUnderTest.apiGatewayBaseUrl(hostName, "customers", Long.toString(customerId), "orderhistory"),
                        GetCustomerHistoryResponse.class);

        assertEquals(HttpStatus.OK, customerResponseEntity.getStatusCode());

        return customerResponseEntity.getBody();
    }

    private void assertOrderState(Long id, OrderState expectedState, RejectionReason expectedRejectionReason) {
        Eventually.eventually(() -> {
            ResponseEntity<GetOrderResponse> getOrderResponseEntity = restTemplate.getForEntity(applicationUnderTest.apiGatewayBaseUrl(hostName, "orders/" + id), GetOrderResponse.class);
            assertEquals(HttpStatus.OK, getOrderResponseEntity.getStatusCode());
            GetOrderResponse order = getOrderResponseEntity.getBody();
            assertEquals(expectedState, order.getOrderState());
            assertEquals(expectedRejectionReason, order.getRejectionReason());
        });
    }

    @Test(expected = HttpClientErrorException.NotFound.class)
    public void shouldHandleOrderHistoryQueryForUnknownCustomer() {
        getOrderHistory(System.currentTimeMillis());
    }

    @Test
    public void testSwaggerUiUrls() throws IOException {
        testSwaggerUiUrl(applicationUnderTest.getApigatewayPort());

        if (applicationUnderTest.exposesSwaggerUiForBackendServices()) {
            testSwaggerUiUrl(applicationUnderTest.getCustomerServicePort());
            testSwaggerUiUrl(applicationUnderTest.getOrderServicePort());
        }
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