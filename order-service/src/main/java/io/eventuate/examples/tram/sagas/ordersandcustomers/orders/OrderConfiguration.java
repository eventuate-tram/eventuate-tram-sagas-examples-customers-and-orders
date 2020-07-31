package io.eventuate.examples.tram.sagas.ordersandcustomers.orders;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service.CreateOrderSaga;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service.OrderSagaService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service.OrderService;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
@Import(OptimisticLockingDecoratorConfiguration.class)
public class OrderConfiguration {

  @Bean
  public OrderSagaService orderSagaService(OrderRepository orderRepository, SagaInstanceFactory sagaInstanceFactory, CreateOrderSaga createOrderSaga) {
    return new OrderSagaService(orderRepository, sagaInstanceFactory, createOrderSaga);
  }

  @Bean
  public OrderService orderService(OrderRepository orderRepository) {
    return new OrderService(orderRepository);
  }

  @Bean
  public CreateOrderSaga createOrderSaga(OrderService orderService) {
    return new CreateOrderSaga(orderService);
  }
}
