package io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.proxies;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.orders.OrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.orders.OrderDestinations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
public class OrderServiceProxy {
  private OrderDestinations orderDestinations;

  private WebClient client;

  public OrderServiceProxy(OrderDestinations orderDestinations, WebClient client) {
    this.orderDestinations = orderDestinations;
    this.client = client;
  }

  public Mono<List<OrderResponse>> findOrdersByCustomerId(String customerId) {
    Mono<ClientResponse> response = client
            .get()
            .uri(orderDestinations.getOrderServiceUrl() + "/orders/customer/{customerId}", customerId)
            .exchange();

    return response.flatMap(resp -> {
      switch (resp.statusCode()) {
        case OK:
          return resp.bodyToMono(OrderResponse[].class).map(Arrays::asList);
        default:
          return Mono.error(new RuntimeException("Unknown" + resp.statusCode()));
      }
    });
  }
}
