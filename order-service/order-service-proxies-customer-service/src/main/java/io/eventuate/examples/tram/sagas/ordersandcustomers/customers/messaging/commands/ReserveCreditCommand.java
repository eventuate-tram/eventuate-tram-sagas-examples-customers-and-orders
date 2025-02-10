package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.commands;

import io.eventuate.examples.common.money.Money;
import io.eventuate.tram.commands.common.Command;

public record ReserveCreditCommand(Long customerId, Long orderId, Money orderTotal) implements Command {

}
