package io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies.orderservice;

public record GetOrderResponse(Long orderId, OrderState orderState, RejectionReason rejectionReason) {

}
