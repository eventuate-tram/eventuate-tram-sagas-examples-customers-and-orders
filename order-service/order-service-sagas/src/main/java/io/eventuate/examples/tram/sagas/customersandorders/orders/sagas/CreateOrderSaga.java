package io.eventuate.examples.tram.sagas.customersandorders.orders.sagas;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.CustomerNotFound;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.Order;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.OrderService;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.RejectionReason;
import io.eventuate.examples.tram.sagas.customersandorders.orders.proxies.customers.CustomerServiceProxy;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;

import java.util.List;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {

  private final OrderService orderService;
  private final CustomerServiceProxy customerService;

  public CreateOrderSaga(OrderService orderService, CustomerServiceProxy customerService) {
    this.orderService = orderService;
    this.customerService = customerService;
  }

  @Override
  public List<Object> getParticipantProxies() {
    return List.of(customerService);
  }

  private SagaDefinition<CreateOrderSagaData> sagaDefinition =
          step()
            .invokeLocal(this::create)
            .withCompensation(this::reject)
          .step()
            .invokeParticipant(this::reserveCredit)
            .onReply(CustomerServiceProxy.customerNotFoundReply, this::handleCustomerNotFound)
            .onReply(CustomerServiceProxy.creditLimitExceededReply, this::handleCustomerCreditLimitExceeded)
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
    Order order = orderService.createOrder(data.getOrderDetails());
    data.setOrderId(order.getId());
  }

  private CommandWithDestination reserveCredit(CreateOrderSagaData data) {
    long orderId = data.getOrderId();
    Long customerId = data.getOrderDetails().customerId();
    Money orderTotal = data.getOrderDetails().orderTotal();
    return customerService.reserveCredit(orderId, customerId, orderTotal);
  }

  private void approve(CreateOrderSagaData data) {
    orderService.approveOrder(data.getOrderId());
  }

  private void reject(CreateOrderSagaData data) {
    orderService.rejectOrder(data.getOrderId(), data.getRejectionReason());
  }
}
