package io.eventuate.examples.tram.sagas.customersandorders.customers.web;

import io.eventuate.examples.common.money.Money;

public record CreateCustomerRequest(String name, Money creditLimit) {

}
