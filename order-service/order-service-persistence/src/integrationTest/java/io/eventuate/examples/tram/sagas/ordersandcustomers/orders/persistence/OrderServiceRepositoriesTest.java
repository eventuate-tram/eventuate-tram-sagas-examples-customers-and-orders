package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.persistence;

import io.eventuate.common.testcontainers.ContainerTestUtil;
import io.eventuate.common.testcontainers.DatabaseContainerFactory;
import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.PropertyProvidingContainer;
import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ContextConfiguration(classes= OrderServiceRepositoriesTest.Config.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NEVER)
public class OrderServiceRepositoriesTest {

  @Configuration
  @Import(OrderPersistenceConfiguration.class)
  static public class Config {
  }

  public static EventuateDatabaseContainer<?> database =
      DatabaseContainerFactory.makeVanillaDatabaseContainer()
          .withReuse(ContainerTestUtil.shouldReuse());

  @DynamicPropertySource
  static void registerMySqlProperties(DynamicPropertyRegistry registry) {
    PropertyProvidingContainer.startAndProvideProperties(registry, database);
  }

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;


  @Test
  public void shouldSaveAndLoadOrder() {

    long customerId = 123L;
    Money orderTotal = new Money("12.34");
    OrderDetails orderDetails = new OrderDetails(customerId, orderTotal);

    Order o = new Order(orderDetails);

    transactionTemplate.executeWithoutResult( ts -> orderRepository.save(o) );

    long orderId = o.getId();

    transactionTemplate.executeWithoutResult(ts -> {
      Order o2 = orderRepository.findById(orderId).get();
      assertEquals(orderDetails, o2.getOrderDetails());
      assertEquals(OrderState.PENDING, o2.getState());

      o2.approve();
    });

    transactionTemplate.executeWithoutResult(ts -> {
      Order o2 = orderRepository.findById(orderId).get();
      assertEquals(OrderState.APPROVED, o2.getState());
    });


  }

}
