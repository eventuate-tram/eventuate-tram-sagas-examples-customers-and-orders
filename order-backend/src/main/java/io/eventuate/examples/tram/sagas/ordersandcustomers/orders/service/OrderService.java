package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.createorder.CreateOrderSaga;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.createorder.CreateOrderSagaData;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private SagaInstanceFactory sagaInstanceFactory;

  @Autowired
  private CreateOrderSaga createOrderSaga;

  public OrderService(OrderRepository orderRepository, SagaInstanceFactory sagaInstanceFactory, CreateOrderSaga createOrderSaga) {
    this.orderRepository = orderRepository;
    this.sagaInstanceFactory = sagaInstanceFactory;
    this.createOrderSaga = createOrderSaga;
  }

  @Transactional
  public Order createOrder(OrderDetails orderDetails) {
    CreateOrderSagaData data = new CreateOrderSagaData(orderDetails);
    sagaInstanceFactory.create(createOrderSaga, data);
    return orderRepository.findById(data.getOrderId()).get();
  }

}
