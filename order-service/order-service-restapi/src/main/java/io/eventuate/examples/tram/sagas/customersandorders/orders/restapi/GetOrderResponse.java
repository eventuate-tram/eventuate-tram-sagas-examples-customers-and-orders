package io.eventuate.examples.tram.sagas.customersandorders.orders.restapi;

import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.OrderState;
import io.eventuate.examples.tram.sagas.customersandorders.orders.domain.RejectionReason;

public record GetOrderResponse(Long orderId, OrderState orderState, RejectionReason rejectionReason) {

}
