package io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.orders;

public class OrderResponse {
  private Long orderId;
  private OrderState orderState;
  private RejectionReason rejectionReason;

  public OrderResponse() {
  }

  public OrderResponse(Long orderId, OrderState orderState, RejectionReason rejectionReason) {
    this.orderId = orderId;
    this.orderState = orderState;
    this.rejectionReason = rejectionReason;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public OrderState getOrderState() {
    return orderState;
  }

  public void setOrderState(OrderState orderState) {
    this.orderState = orderState;
  }

  public RejectionReason getRejectionReason() {
    return rejectionReason;
  }

  public void setRejectionReason(RejectionReason rejectionReason) {
    this.rejectionReason = rejectionReason;
  }
}
