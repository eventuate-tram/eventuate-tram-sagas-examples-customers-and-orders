package io.eventuate.examples.tram.sagas.customersandorders.endtoendtests.proxies.customerservice;

import io.eventuate.examples.common.money.Money;

public record CreateCustomerRequest(String name, Money creditLimit) {

}
