package io.eventuate.examples.tram.sagas.customersandorders.orders.web;

import java.util.List;

public record GetOrdersResponse(List<GetOrderResponse> orders) {

}
