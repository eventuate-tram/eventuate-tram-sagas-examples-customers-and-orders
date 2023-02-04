package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderDestinations;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.GetOrderResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class OrderServiceProxy {
  private final CircuitBreaker circuitBreaker;
  private final TimeLimiter timeLimiter;
  private final OrderDestinations orderDestinations;

  private final WebClient client;

  public OrderServiceProxy(OrderDestinations orderDestinations, WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
    this.orderDestinations = orderDestinations;
    this.client = client;
    this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("ORDER_SERVICE_CIRCUIT_BREAKER");
    this.timeLimiter = timeLimiterRegistry.timeLimiter("ORDER_SERVICE_CIRCUIT_BREAKER");
  }

  public Mono<List<GetOrderResponse>> findOrdersByCustomerId(String customerId) {
    return client
            .get()
            .uri(orderDestinations.getOrderServiceUrl() + "/orders/customer/{customerId}", customerId)
            .retrieve()
            .onStatus(status -> status != HttpStatus.OK, response -> Mono.error(UnknownProxyException.make("/orders/customer/", response.statusCode(), customerId)))
            .bodyToMono(GetOrderResponse[].class).map(Arrays::asList)
            .transformDeferred(TimeLimiterOperator.of(timeLimiter))
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
    ;
  }
}
