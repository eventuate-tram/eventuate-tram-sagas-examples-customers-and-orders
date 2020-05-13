package io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.customers;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.proxies.CustomerNotFoundException;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.orders.OrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.proxies.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.proxies.OrderServiceProxy;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

public class OrderHistoryHandlers {

  private OrderServiceProxy orderService;
  private CustomerServiceProxy customerService;

  public OrderHistoryHandlers(OrderServiceProxy orderService, CustomerServiceProxy customerService) {
    this.orderService = orderService;
    this.customerService = customerService;
  }

  public Mono<ServerResponse> getOrderDetails(ServerRequest serverRequest) {
    String customerId = serverRequest.pathVariable("customerId");

    Mono<CustomerResponse> customer = customerService.findCustomerById(customerId);

    Mono<List<OrderResponse>> orders = orderService.findOrdersByCustomerId(customerId);

    return Mono
            .zip(customer, orders)
            .map(objects -> {
              CustomerResponse c = objects.getT1();
              c.setOrders(objects.getT2());
              return c;
            })
            .flatMap(c ->
              ServerResponse.ok()
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(fromObject(c))
            ).onErrorResume(CustomerNotFoundException.class, e -> ServerResponse.notFound().build());
  }
}
