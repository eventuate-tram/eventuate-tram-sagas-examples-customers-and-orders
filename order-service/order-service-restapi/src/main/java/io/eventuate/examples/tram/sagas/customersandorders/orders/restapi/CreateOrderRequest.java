package io.eventuate.examples.tram.sagas.customersandorders.orders.restapi;


import io.eventuate.examples.common.money.Money;

public record CreateOrderRequest(Long customerId, Money orderTotal) {

}
