package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.web;

import java.util.List;

public record GetCustomersResponse(List<GetCustomerResponse> customers) {
}
