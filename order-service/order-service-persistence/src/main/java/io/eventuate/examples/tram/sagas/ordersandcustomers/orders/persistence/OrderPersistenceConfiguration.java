package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.persistence;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.tram.spring.flyway.EventuateTramFlywayMigrationConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {OrderRepository.class})
@EntityScan(basePackageClasses = {Order.class})
@Import(EventuateTramFlywayMigrationConfiguration.class)
public class OrderPersistenceConfiguration {
}
