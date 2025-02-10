package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.proxies.customers;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies.CustomerNotFound;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder;

public class CustomerServiceProxy {

  public static final Class<CustomerCreditLimitExceeded> creditLimitExceededReply = CustomerCreditLimitExceeded.class;
  public static final Class<CustomerNotFound> customerNotFoundReply = CustomerNotFound.class;

  public CommandWithDestination reserveCredit(long orderId, Long customerId, Money orderTotal) {
    return CommandWithDestinationBuilder.send(new ReserveCreditCommand(customerId, orderId, orderTotal))
            .to("customerService")
            .build();
  }
}
