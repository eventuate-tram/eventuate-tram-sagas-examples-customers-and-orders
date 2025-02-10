package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.orderservice.GetOrderResponse;

import java.util.List;

public record GetCustomerHistoryResponse(Long customerId, String name, Money creditLimit, List<GetOrderResponse> orders) {
  public Money getCreditLimit() {
    return creditLimit();
  }

  public Long getCustomerId() {
    return customerId();
  }

  public String getName() {
    return name();
  }

  public List<GetOrderResponse> getOrders() {
    return orders();
  }
}
