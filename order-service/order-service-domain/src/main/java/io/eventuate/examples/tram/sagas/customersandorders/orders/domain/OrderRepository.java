package io.eventuate.examples.tram.sagas.customersandorders.orders.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
  List<Order> findAllByOrderDetailsCustomerId(Long customerId);
}
