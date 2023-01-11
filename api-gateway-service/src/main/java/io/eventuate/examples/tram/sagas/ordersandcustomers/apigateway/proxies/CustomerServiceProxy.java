package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.GetCustomerResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class CustomerServiceProxy {
  private final CircuitBreaker cb;

  private WebClient client;
  private String customerServiceUrl;
  private TimeLimiter timeLimiter;

  public CustomerServiceProxy(WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, String customerServiceUrl, TimeLimiterRegistry timeLimiterRegistry) {
    this.client = client;
    this.cb = circuitBreakerRegistry.circuitBreaker("CUSTOMER_SERVICE_CIRCUIT_BREAKER");
    this.timeLimiter = timeLimiterRegistry.timeLimiter("CUSTOMER_SERVICE_TIME_LIMITER");
    this.customerServiceUrl = customerServiceUrl;
  }

  public Mono<Optional<GetCustomerResponse>> findCustomerById(String customerId) {
    Mono<ClientResponse> response = client
            .get()
            .uri(customerServiceUrl + "/customers/{customerId}", customerId)
            .exchange();
    return response.flatMap(resp -> {
      switch (resp.statusCode()) {
        case OK:
          return resp.bodyToMono(GetCustomerResponse.class).map(Optional::of);
        case NOT_FOUND:
          Mono<Optional<GetCustomerResponse>> notFound = Mono.just(Optional.empty());
          return notFound;
        default:
          return Mono.error(UnknownProxyException.make("/customers/", resp.statusCode(), customerId));
      }
    })
    .transformDeferred(TimeLimiterOperator.of(timeLimiter))
    .transformDeferred(CircuitBreakerOperator.of(cb))
    //.onErrorResume(CallNotPermittedException.class, e -> Mono.just(null))
    ;
  }


}
