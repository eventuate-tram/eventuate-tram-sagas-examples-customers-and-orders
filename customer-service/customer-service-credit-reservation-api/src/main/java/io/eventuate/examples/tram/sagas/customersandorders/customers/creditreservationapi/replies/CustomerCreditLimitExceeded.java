package io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies;

import io.eventuate.tram.commands.consumer.annotations.FailureReply;

@FailureReply
public class CustomerCreditLimitExceeded implements ReserveCreditResult {
}
