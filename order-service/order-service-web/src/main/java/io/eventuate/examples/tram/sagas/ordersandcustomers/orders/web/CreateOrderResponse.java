package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.web;


public record CreateOrderResponse(long orderId) {

  public Long getOrderId() {
    return orderId();
  }

}
