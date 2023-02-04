package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerDomainConfiguration {

  @Bean
  public CustomerService customerService(CustomerRepository customerRepository) {
    return new CustomerService(customerRepository);
  }

}
