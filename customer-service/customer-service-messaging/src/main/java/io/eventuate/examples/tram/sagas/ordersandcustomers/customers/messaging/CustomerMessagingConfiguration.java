package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging;

import io.eventuate.tram.spring.flyway.EventuateTramFlywayMigrationConfiguration;
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({OptimisticLockingDecoratorConfiguration.class, EventuateTramFlywayMigrationConfiguration.class,
        CustomerCommandHandlerConfiguration.class})
public class CustomerMessagingConfiguration {



}
