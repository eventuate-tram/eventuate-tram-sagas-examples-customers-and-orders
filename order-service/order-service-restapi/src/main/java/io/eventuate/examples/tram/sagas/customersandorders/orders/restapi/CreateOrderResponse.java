package io.eventuate.examples.tram.sagas.customersandorders.orders.restapi;


public record CreateOrderResponse(long orderId) {

  public Long getOrderId() {
    return orderId();
  }

}
