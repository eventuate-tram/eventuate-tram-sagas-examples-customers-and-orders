package io.eventuate.examples.tram.sagas.customersandorders.orders.restapi;

import java.util.List;

public record GetOrdersResponse(List<GetOrderResponse> orders) {

}
