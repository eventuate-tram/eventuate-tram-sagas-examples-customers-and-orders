package io.eventuate.examples.tram.sagas.customersandorders.orders.sagas;

import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.OrderService;
import io.eventuate.examples.tram.sagas.customersandorders.orders.proxies.customers.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.customersandorders.orders.proxies.customers.CustomerServiceProxyConfiguration;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.eventuate.tram.spring.flyway.EventuateTramFlywayMigrationConfiguration;
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
@Import({OptimisticLockingDecoratorConfiguration.class, CustomerServiceProxyConfiguration.class, EventuateTramFlywayMigrationConfiguration.class})
public class OrderSagasConfiguration {

  @Bean
  public OrderSagaService orderSagaService(OrderRepository orderRepository, SagaInstanceFactory sagaInstanceFactory, CreateOrderSaga createOrderSaga) {
    return new OrderSagaService(orderRepository, sagaInstanceFactory, createOrderSaga);
  }

  @Bean
  public CreateOrderSaga createOrderSaga(OrderService orderService, CustomerServiceProxy customerService) {
    return new CreateOrderSaga(orderService, customerService);
  }


}
