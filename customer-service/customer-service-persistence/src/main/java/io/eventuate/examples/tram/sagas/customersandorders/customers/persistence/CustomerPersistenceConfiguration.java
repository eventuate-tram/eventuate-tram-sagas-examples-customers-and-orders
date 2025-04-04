package io.eventuate.examples.tram.sagas.customersandorders.customers.persistence;

import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.Customer;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {CustomerRepository.class})
@EntityScan(basePackageClasses = {Customer.class})
public class CustomerPersistenceConfiguration {



}
