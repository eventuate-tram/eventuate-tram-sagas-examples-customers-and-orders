package io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.customers;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.orders.OrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.common.Money;

import java.util.List;

public class CustomerResponse {
  private Long customerId;
  private String name;
  private Money creditLimit;
  private List<OrderResponse> orders;

  public CustomerResponse() {
  }

  public CustomerResponse(Long customerId, String name, Money creditLimit, List<OrderResponse> orders) {
    this.customerId = customerId;
    this.name = name;
    this.creditLimit = creditLimit;
    this.orders = orders;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Money getCreditLimit() {
    return creditLimit;
  }

  public void setCreditLimit(Money creditLimit) {
    this.creditLimit = creditLimit;
  }

  public List<OrderResponse> getOrders() {
    return orders;
  }

  public void setOrders(List<OrderResponse> orders) {
    this.orders = orders;
  }
}
