package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.createorder;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common.OrderState;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common.RejectionReason;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static io.eventuate.tram.sagas.testing.SagaUnitTestSupport.given;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateOrderSagaTest {

  private OrderRepository orderRepository;
  private Long customerId = 102L;
  private Money orderTotal = new Money("12.34");
  private OrderDetails orderDetails = new OrderDetails(customerId, orderTotal);
  private Long orderId = 103L;

  private CreateOrderSaga makeCreateOrderSaga() {
    return new CreateOrderSaga(orderRepository);
  }


  @Before
  public void setUp() {
    orderRepository = mock(OrderRepository.class);
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