package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers.CustomerDestinations;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.webapi.GetCustomerResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceProxy {
  private CustomerDestinations customerDestinations;

  private WebClient client;

  public CustomerServiceProxy(CustomerDestinations customerDestinations, WebClient client) {
    this.customerDestinations = customerDestinations;
    this.client = client;
  }

  public Mono<GetCustomerResponse> findCustomerById(String customerId) {
    Mono<ClientResponse> response = client
            .get()
            .uri(customerDestinations.getCustomerServiceUrl() + "/customers/{customerId}", customerId)
            .exchange();
    return response.flatMap(resp -> {
      switch (resp.statusCode()) {
        case OK:
          return resp.bodyToMono(GetCustomerResponse.class);
        case NOT_FOUND:
          return Mono.error(new CustomerNotFoundException());
        default:
          return Mono.error(new RuntimeException("Unknown" + resp.statusCode()));
      }
    });
  }
}
