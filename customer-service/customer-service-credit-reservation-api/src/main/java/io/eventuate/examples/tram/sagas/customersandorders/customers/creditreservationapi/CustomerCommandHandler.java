package io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi;

import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerCreditLimitExceededException;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerNotFoundException;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerService;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.CustomerCreditReserved;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.CustomerNotFound;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.ReserveCreditResult;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.commands.consumer.annotations.EventuateCommandHandler;

public class CustomerCommandHandler {

  private final CustomerService customerService;

  public CustomerCommandHandler(CustomerService customerService) {
    this.customerService = customerService;
  }

  @EventuateCommandHandler(subscriberId="customerCommandDispatcher", channel="customerService")
  public ReserveCreditResult reserveCredit(CommandMessage<ReserveCreditCommand> cm) {
    ReserveCreditCommand cmd = cm.getCommand();
    try {
      customerService.reserveCredit(cmd.customerId(), cmd.orderId(), cmd.orderTotal());
      return new CustomerCreditReserved();
    } catch (CustomerNotFoundException e) {
      return new CustomerNotFound();
    } catch (CustomerCreditLimitExceededException e) {
      return new CustomerCreditLimitExceeded();
    }
  }

}
