package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies;

import io.eventuate.tram.commands.consumer.annotations.SuccessReply;

@SuccessReply
public class CustomerCreditReserved implements ReserveCreditResult {
}
