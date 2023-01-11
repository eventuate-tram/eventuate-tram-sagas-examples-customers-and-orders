package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.messaging.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.messaging.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.OrderState;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.common.RejectionReason;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.api.messaging.sagas.createorder.CreateOrderSagaData;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.CreateOrderSaga;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.CustomerServiceProxy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static io.eventuate.tram.sagas.testing.SagaUnitTestSupport.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateOrderSagaTest {

  private OrderRepository orderRepository;
  private OrderService orderService;
  private Long customerId = 102L;
  private Money orderTotal = new Money("12.34");
  private OrderDetails orderDetails = new OrderDetails(customerId, orderTotal);
  private Long orderId = 103L;
  private CustomerServiceProxy customerService = new CustomerServiceProxy();

  private CreateOrderSaga makeCreateOrderSaga() {
    return new CreateOrderSaga(orderService, customerService);
  }


  @Before
  public void setUp() {
    orderRepository = mock(OrderRepository.class);
    orderService = new OrderService(orderRepository);
  }

  private Order order;

  @Test
  public void shouldCreateOrder() {
    when(orderRepository.save(any(Order.class))).then((Answer<Order>) invocation -> {
      order = invocation.getArgument(0);
      order.setId(orderId);
      return order;
    });

    when(orderRepository.findById(orderId)).then(invocation -> Optional.of(order));

    given()
            .saga(makeCreateOrderSaga(),
                    new CreateOrderSagaData(orderDetails)).
            expect().
            command(new ReserveCreditCommand(customerId, orderId, orderTotal))
            .to("customerService")
            .andGiven()
            .successReply()
            .expectCompletedSuccessfully();

    assertEquals(OrderState.APPROVED, order.getState());
  }

  @Test
  public void shouldRejectCreateOrder() {
    when(orderRepository.save(any(Order.class))).then((Answer<Order>) invocation -> {
      order = invocation.getArgument(0);
      order.setId(orderId);
      return order;
    });

    when(orderRepository.findById(orderId)).then(invocation -> Optional.of(order));

    CreateOrderSagaData data = new CreateOrderSagaData(orderDetails);

    given()
            .saga(makeCreateOrderSaga(),
                    data).
            expect().
            command(new ReserveCreditCommand(customerId, orderId, orderTotal))
            .to("customerService")
            .andGiven()
            .failureReply(new CustomerCreditLimitExceeded())
            .expectRolledBack()
            .assertSagaData(sd -> {
              assertEquals(RejectionReason.INSUFFICIENT_CREDIT, sd.getRejectionReason());
            });

    assertEquals(OrderState.REJECTED, order.getState());
    assertEquals(RejectionReason.INSUFFICIENT_CREDIT, order.getRejectionReason());
  }
}