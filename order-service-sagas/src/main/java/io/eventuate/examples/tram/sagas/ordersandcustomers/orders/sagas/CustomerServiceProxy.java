package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.messaging.commands.ReserveCreditCommand;
import io.eventuate.tram.commands.consumer.CommandWithDestination;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class CustomerServiceProxy {
  CommandWithDestination reserveCredit(long orderId, Long customerId, Money orderTotal) {
    return send(new ReserveCreditCommand(customerId, orderId, orderTotal))
            .to("customerService")
            .build();
  }
}
