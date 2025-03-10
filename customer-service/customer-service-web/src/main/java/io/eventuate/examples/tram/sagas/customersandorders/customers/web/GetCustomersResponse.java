package io.eventuate.examples.tram.sagas.customersandorders.customers.web;

import java.util.List;

public record GetCustomersResponse(List<GetCustomerResponse> customers) {
}
