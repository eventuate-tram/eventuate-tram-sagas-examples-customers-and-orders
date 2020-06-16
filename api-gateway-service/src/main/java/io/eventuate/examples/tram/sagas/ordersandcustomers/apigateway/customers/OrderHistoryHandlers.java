package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.OrderServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.api.web.GetCustomerHistoryResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.GetCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.web.GetOrderResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class OrderHistoryHandlers {

  private OrderServiceProxy orderService;
  private CustomerServiceProxy customerService;

  public OrderHistoryHandlers(OrderServiceProxy orderService, CustomerServiceProxy customerService) {
    this.orderService = orderService;
    this.customerService = customerService;
  }

  public Mono<ServerResponse> getOrderHistory(ServerRequest serverRequest) {
    String customerId = serverRequest.pathVariable("customerId");

    Mono<Optional<GetCustomerResponse>> customer = customerService.findCustomerById(customerId);

    Mono<List<GetOrderResponse>> orders = orderService.findOrdersByCustomerId(customerId);

    Mono<Optional<GetCustomerHistoryResponse>> map = Mono
            .zip(customer, orders)
            .map(possibleCustomerAndOrders ->
                    possibleCustomerAndOrders.getT1().map(c -> {
                      List<GetOrderResponse> os = possibleCustomerAndOrders.getT2();
                      return new GetCustomerHistoryResponse(c.getCustomerId(), c.getName(), c.getCreditLimit(), os);
                    }));
    return map.flatMap(maybe ->
            maybe.map(c ->
                    ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fromValue(c)))
                    .orElseGet(() -> ServerResponse.notFound().build()));
  }
}
