package io.eventuate.examples.tram.sagas.customersandorders.customers.messaging.replies;

import io.eventuate.tram.commands.consumer.annotations.FailureReply;

@FailureReply
public class CustomerNotFound implements ReserveCreditResult {
}
