package io.eventuate.examples.tram.sagas.customersandorders.customers.restapi;

import java.util.List;

public record GetCustomersResponse(List<GetCustomerResponse> customers) {
}
