package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.orderservice;

public record GetOrderResponse(Long orderId, OrderState orderState, RejectionReason rejectionReason) {

}
