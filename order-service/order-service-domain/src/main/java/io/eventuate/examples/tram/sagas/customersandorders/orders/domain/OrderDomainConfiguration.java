package io.eventuate.examples.tram.sagas.customersandorders.orders.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderDomainConfiguration {

  @Bean
  public OrderService orderService(OrderRepository orderRepository) {
    return new OrderService(orderRepository);
  }
}
