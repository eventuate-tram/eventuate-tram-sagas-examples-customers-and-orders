package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderDestinations;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.GetOrderResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class OrderServiceProxy {
  private final CircuitBreaker circuitBreaker;
  private final TimeLimiter timeLimiter;
  private final OrderDestinations orderDestinations;

  private WebClient client;

  public OrderServiceProxy(OrderDestinations orderDestinations, WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
    this.orderDestinations = orderDestinations;
    this.client = client;
    this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("MY_CIRCUIT_BREAKER");
    this.timeLimiter = timeLimiterRegistry.timeLimiter("MY_TIME_LIMITER");
  }

  public Mono<List<GetOrderResponse>> findOrdersByCustomerId(String customerId) {
    Mono<ClientResponse> response = client
            .get()
            .uri(orderDestinations.getOrderServiceUrl() + "/orders/customer/{customerId}", customerId)
            .exchange();

    return response.flatMap(resp -> {
      switch (resp.statusCode()) {
        case OK:
          return resp.bodyToMono(GetOrderResponse[].class).map(Arrays::asList);
        default:
          return Mono.error(new UnknownProxyException("Unknown: " + resp.statusCode()));
      }
    })
    .transformDeferred(TimeLimiterOperator.of(timeLimiter))
    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
    ;
  }
}
