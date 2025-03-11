package io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi;

import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerCommandHandlerConfiguration {

  @Bean
  public CustomerCommandHandler customerCommandHandler(CustomerService customerService) {
    return new CustomerCommandHandler(customerService);
  }

  // TODO Exception handler for CustomerCreditLimitExceededException
}
