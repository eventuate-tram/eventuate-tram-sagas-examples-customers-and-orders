package io.eventuate.examples.tram.sagas.customersandorders.apigateway.customers;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "customer.destinations")
public class CustomerDestinations {

  @NotNull
  private String customerServiceUrl;

  public String getCustomerServiceUrl() {
    return customerServiceUrl;
  }

  public void setCustomerServiceUrl(String customerServiceUrl) {
    this.customerServiceUrl = customerServiceUrl;
  }
}
