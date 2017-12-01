package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.web;


public class CreateOrderResponse {
  private Long orderId;

  public CreateOrderResponse() {
  }

  public CreateOrderResponse(Long orderId) {
    this.orderId = orderId;
  }

  public Long getOrderId() {
    return orderId;
  }
}
