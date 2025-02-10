package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.customerservice;


import io.eventuate.examples.common.money.Money;

public record GetCustomerResponse(Long customerId, String name, Money creditLimit) {

}
