package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi;


public class CreateOrderResponse {
  private long orderId;

  public CreateOrderResponse() {
  }

  public CreateOrderResponse(long orderId) {
    this.orderId = orderId;
  }

  public long getOrderId() {
    return orderId;
  }
}
