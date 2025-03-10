package io.eventuate.examples.tram.sagas.customersandorders.endtoendtests.proxies.orderservice;


public record CreateOrderResponse(long orderId) {

  public Long getOrderId() {
    return orderId();
  }

}
