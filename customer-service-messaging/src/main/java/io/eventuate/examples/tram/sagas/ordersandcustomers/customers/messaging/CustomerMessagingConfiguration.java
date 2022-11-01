package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.CustomerService;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OptimisticLockingDecoratorConfiguration.class)
public class CustomerMessagingConfiguration {

  @Bean
  public CustomerCommandHandler customerCommandHandler(CustomerService customerService) {
    return new CustomerCommandHandler(customerService);
  }

  // TODO Exception handler for CustomerCreditLimitExceededException

  @Bean
  public CommandDispatcher consumerCommandDispatcher(CustomerCommandHandler target,
                                                     SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {

    return sagaCommandDispatcherFactory.make("customerCommandDispatcher", target.commandHandlerDefinitions());
  }

}
