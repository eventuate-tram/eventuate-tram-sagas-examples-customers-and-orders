package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.GetCustomerResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = {"eventuate-tram-sagas-examples-customers-and-orders.customer-service:customer-service-web"})
@DirtiesContext
public class CustomerServiceProxyTest {

  @Value("${stubrunner.runningstubs.customer-service-web.port}")
  private int port;

  @Configuration
  static public class Config {
  }

  private CustomerServiceProxy customerServiceProxy;

  @Before
  public void setUp() {
    customerServiceProxy = new CustomerServiceProxy(WebClient.builder().build(),
            CircuitBreakerRegistry.custom().build(), String.format("http://localhost:%s", port), TimeLimiterRegistry.ofDefaults());
  }
  @Test
  public void shouldCallCustomerService() {
    GetCustomerResponse customer = customerServiceProxy.findCustomerById("101").block().get();

    assertEquals(Long.valueOf(101L), customer.getCustomerId());
    assertEquals("Chris", customer.getName());
    assertEquals(new Money("123.45"), customer.getCreditLimit());

  }

}