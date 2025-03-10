package io.eventuate.examples.tram.sagas.customersandorders.orders.web;


public record CreateOrderResponse(long orderId) {

  public Long getOrderId() {
    return orderId();
  }

}
