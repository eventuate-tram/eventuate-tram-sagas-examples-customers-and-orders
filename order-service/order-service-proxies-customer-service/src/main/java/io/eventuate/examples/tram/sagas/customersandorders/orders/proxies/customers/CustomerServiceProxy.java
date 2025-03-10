package io.eventuate.examples.tram.sagas.customersandorders.orders.proxies.customers;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.customersandorders.customers.messaging.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.customersandorders.customers.messaging.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.customersandorders.customers.messaging.replies.CustomerNotFound;
import io.eventuate.examples.tram.sagas.customersandorders.customers.messaging.replies.ReserveCreditResult;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantOperation;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantProxy;

@SagaParticipantProxy(channel=CustomerServiceProxy.CHANNEL)
public class CustomerServiceProxy {

  public static final String CHANNEL = "customerService";

  public static final Class<CustomerCreditLimitExceeded> creditLimitExceededReply = CustomerCreditLimitExceeded.class;
  public static final Class<CustomerNotFound> customerNotFoundReply = CustomerNotFound.class;

  @SagaParticipantOperation(commandClass=ReserveCreditCommand.class, replyClasses=ReserveCreditResult.class)
  public CommandWithDestination reserveCredit(long orderId, Long customerId, Money orderTotal) {
    return CommandWithDestinationBuilder.send(new ReserveCreditCommand(customerId, orderId, orderTotal))
            .to(CHANNEL)
            .build();
  }
}
