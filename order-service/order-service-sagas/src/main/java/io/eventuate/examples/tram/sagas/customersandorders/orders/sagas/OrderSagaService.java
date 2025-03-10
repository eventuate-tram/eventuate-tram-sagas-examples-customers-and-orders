package io.eventuate.examples.tram.sagas.customersandorders.orders.sagas;

import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.Order;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.OrderDetails;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.OrderRepository;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import org.springframework.transaction.annotation.Transactional;

public class OrderSagaService {

  private final OrderRepository orderRepository;

  private final SagaInstanceFactory sagaInstanceFactory;

  private final CreateOrderSaga createOrderSaga;

  public OrderSagaService(OrderRepository orderRepository, SagaInstanceFactory sagaInstanceFactory, CreateOrderSaga createOrderSaga) {
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
