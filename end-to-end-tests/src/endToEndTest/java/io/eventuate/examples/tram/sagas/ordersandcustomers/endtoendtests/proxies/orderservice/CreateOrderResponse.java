package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests.proxies.orderservice;


public record CreateOrderResponse(long orderId) {

  public Long getOrderId() {
    return orderId();
  }

}
