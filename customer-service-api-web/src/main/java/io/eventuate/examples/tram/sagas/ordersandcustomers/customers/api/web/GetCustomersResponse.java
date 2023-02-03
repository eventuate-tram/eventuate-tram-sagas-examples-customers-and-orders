package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.web;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.GetCustomerResponse;

import java.util.List;

public class GetCustomersResponse {

  private List<GetCustomerResponse> customers;


  public GetCustomersResponse(List<GetCustomerResponse> customers) {
    this.customers = customers;
  }

  public List<GetCustomerResponse> getCustomers() {
    return customers;
  }

  public void setCustomers(List<GetCustomerResponse> customers) {
    this.customers = customers;
  }
}
