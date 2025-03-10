package io.eventuate.examples.tram.sagas.customersandorders.orders.web;

import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.Order;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.OrderDetails;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.customersandorders.orders.sagas.OrderSagaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class OrderController {

  private final OrderSagaService orderSagaService;
  private final OrderRepository orderRepository;

  public OrderController(OrderSagaService orderSagaService, OrderRepository orderRepository) {
    this.orderSagaService = orderSagaService;
    this.orderRepository = orderRepository;
  }

  @PostMapping("/orders")
  public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
    Order order = orderSagaService.createOrder(new OrderDetails(createOrderRequest.customerId(), createOrderRequest.orderTotal()));
    return new CreateOrderResponse(order.getId());
  }

  @GetMapping("/orders")
  public ResponseEntity<GetOrdersResponse> getAll() {
    return ResponseEntity.ok(new GetOrdersResponse(StreamSupport.stream(orderRepository.findAll().spliterator(), false)
            .map(o -> new GetOrderResponse(o.getId(), o.getState(), o.getRejectionReason())).collect(Collectors.toList())));
  }

  @GetMapping("/orders/{orderId}")
  public ResponseEntity<GetOrderResponse> getOrder(@PathVariable Long orderId) {
    return orderRepository
            .findById(orderId)
            .map(o -> new ResponseEntity<>(new GetOrderResponse(o.getId(), o.getState(), o.getRejectionReason()), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/orders/customer/{customerId}")
  public ResponseEntity<List<GetOrderResponse>> getOrdersByCustomerId(@PathVariable Long customerId) {
    return new ResponseEntity<>(orderRepository
            .findAllByOrderDetailsCustomerId(customerId)
            .stream()
            .map(o -> new GetOrderResponse(o.getId(), o.getState(), o.getRejectionReason()))
            .collect(Collectors.toList()), HttpStatus.OK);
  }
}
