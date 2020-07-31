package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.RejectionReason;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Order createOrder(OrderDetails orderDetails) {
    Order order = Order.createOrder(orderDetails);
    orderRepository.save(order);
    return order;
  }

  public void approveOrder(Long orderId) {
    orderRepository.findById(orderId).get().approve();
  }

  public void rejectOrder(Long orderId, RejectionReason rejectionReason) {
    orderRepository.findById(orderId).get().reject(rejectionReason);
  }
}
