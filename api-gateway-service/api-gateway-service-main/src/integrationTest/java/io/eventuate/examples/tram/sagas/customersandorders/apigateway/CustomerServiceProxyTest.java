package io.eventuate.examples.tram.sagas.customersandorders.apigateway;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies.customerservice.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies.customerservice.GetCustomerResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = {"io.eventuate.examples.tram.sagas.customersandorders:customer-service-web:+"},
    stubsMode = StubRunnerProperties.StubsMode.REMOTE)
@DirtiesContext
public class CustomerServiceProxyTest {

  @Value("${stubrunner.runningstubs.customer-service-web.port}")
  private int port;

  @Configuration
  static public class Config {
  }

  private CustomerServiceProxy customerServiceProxy;

  @BeforeEach
  public void setUp() {
    customerServiceProxy = new CustomerServiceProxy(WebClient.builder().build(),
            CircuitBreakerRegistry.custom().build(), "http://localhost:%s".formatted(port), TimeLimiterRegistry.ofDefaults());
  }
  @Test
  public void shouldCallCustomerService() {
    GetCustomerResponse customer = customerServiceProxy.findCustomerById("101").block().get();

    assertEquals(Long.valueOf(101L), customer.customerId());
    assertEquals("Chris", customer.name());
    assertEquals(new Money("123.45"), customer.creditLimit());

  }

}