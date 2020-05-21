package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.common.CommonConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers.CustomerConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers.CustomerDestinations;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderDestinations;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@Import({CommonConfiguration.class, OrderConfiguration.class, CustomerConfiguration.class})
public class ProxyConfiguration {

  @Bean
  public OrderServiceProxy orderServiceProxy(OrderDestinations orderDestinations, WebClient client) {
    return new OrderServiceProxy(orderDestinations, client);
  }

  @Bean
  public CustomerServiceProxy customerServiceProxy(CustomerDestinations customerDestinations, WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
    return new CustomerServiceProxy(client, circuitBreakerRegistry, customerDestinations.getCustomerServiceUrl(), timeLimiterRegistry);
  }

  @Bean
  public TimeLimiterRegistry timeLimiterRegistry() {
    return TimeLimiterRegistry.of(TimeLimiterConfig.custom().timeoutDuration(Duration.of(1000, ChronoUnit.MILLIS)).build());
  }

}
