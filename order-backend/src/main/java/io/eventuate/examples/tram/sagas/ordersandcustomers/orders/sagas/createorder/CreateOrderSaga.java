package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.createorder;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.replies.CustomerNotFound;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common.RejectionReason;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {

  private OrderRepository orderRepository;

  public CreateOrderSaga(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  private SagaDefinition<CreateOrderSagaData> sagaDefinition =
          step()
            .invokeLocal(this::create)
            .withCompensation(this::reject)
          .step()
            .invokeParticipant(this::reserveCredit)
            .onReply(CustomerNotFound.class, this::handleCustomerNotFound)
            .onReply(CustomerCreditLimitExceeded.class, this::handleCustomerCreditLimitExceeded)
          .step()
            .invokeLocal(this::approve)
          .build();

  private void handleCustomerNotFound(CreateOrderSagaData data, CustomerNotFound reply) {
    data.setRejectionReason(RejectionReason.UNKNOWN_CUSTOMER);
  }

  private void handleCustomerCreditLimitExceeded(CreateOrderSagaData data, CustomerCreditLimitExceeded reply) {
    data.setRejectionReason(RejectionReason.INSUFFICIENT_CREDIT);
  }


  @Override
  public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
    return this.sagaDefinition;
  }

  private void create(CreateOrderSagaData data) {
    Order order = Order.createOrder(data.getOrderDetails());
    orderRepository.save(order);
    data.setOrderId(order.getId());
  }

  private CommandWithDestination reserveCredit(CreateOrderSagaData data) {
    long orderId = data.getOrderId();
    Long customerId = data.getOrderDetails().getCustomerId();
    Money orderTotal = data.getOrderDetails().getOrderTotal();
    return send(new ReserveCreditCommand(customerId, orderId, orderTotal))
            .to("customerService")
            .build();
  }

  private void approve(CreateOrderSagaData data) {
    orderRepository.findById(data.getOrderId()).get().approve();
  }

  public void reject(CreateOrderSagaData data) {
    orderRepository.findById(data.getOrderId()).get().reject(data.getRejectionReason());
  }



}
