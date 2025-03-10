package io.eventuate.examples.tram.sagas.customersandorders.orders.proxies.customers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerServiceProxyConfiguration {

  @Bean
  public CustomerServiceProxy customerServiceProxy() {
    return new CustomerServiceProxy();
  }

}
