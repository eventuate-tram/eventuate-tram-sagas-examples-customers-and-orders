package io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.proxies;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.customers.CustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.customers.CustomerDestinations;
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

  public Mono<CustomerResponse> findCustomerById(String customerId) {
    Mono<ClientResponse> response = client
            .get()
            .uri(customerDestinations.getCustomerServiceUrl() + "/customers/{customerId}", customerId)
            .exchange();
    return response.flatMap(resp -> {
      switch (resp.statusCode()) {
        case OK:
          return resp.bodyToMono(CustomerResponse.class);
        case NOT_FOUND:
          return Mono.error(new CustomerNotFoundException());
        default:
          return Mono.error(new RuntimeException("Unknown" + resp.statusCode()));
      }
    });
  }
}
