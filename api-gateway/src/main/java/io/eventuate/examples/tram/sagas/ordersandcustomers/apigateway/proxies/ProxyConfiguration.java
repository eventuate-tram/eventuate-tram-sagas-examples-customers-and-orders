package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.common.CommonConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers.CustomerConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers.CustomerDestinations;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderDestinations;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
@Import({CommonConfiguration.class, OrderConfiguration.class, CustomerConfiguration.class})
public class ProxyConfiguration {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apigateway.timeout.millis}")
  private long apiGatewayTimeoutMillis;

  @Bean
  public OrderServiceProxy orderServiceProxy(OrderDestinations orderDestinations, WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
    return new OrderServiceProxy(orderDestinations, client, circuitBreakerRegistry, timeLimiterRegistry);
  }

  @Bean
  public CustomerServiceProxy customerServiceProxy(CustomerDestinations customerDestinations, WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
    return new CustomerServiceProxy(client, circuitBreakerRegistry, customerDestinations.getCustomerServiceUrl(), timeLimiterRegistry);
  }

  @Bean
  public TimeLimiterRegistry timeLimiterRegistry() {
    logger.info("apiGatewayTimeoutMillis={}", apiGatewayTimeoutMillis);
    return TimeLimiterRegistry.of(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(apiGatewayTimeoutMillis)).build());
  }

  @Bean
  public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
            .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(apiGatewayTimeoutMillis)).build()).build());
  }
}
