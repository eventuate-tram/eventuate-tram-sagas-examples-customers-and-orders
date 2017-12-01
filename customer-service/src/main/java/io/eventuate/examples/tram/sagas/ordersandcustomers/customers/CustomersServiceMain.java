package io.eventuate.examples.tram.sagas.ordersandcustomers.customers;

import io.eventuate.examples.tram.sagas.ordersandcustomers.TramCommandsAndEventsIntegrationData;
import io.eventuate.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.sagas.orchestration.SagaOrchestratorConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Configuration
@Import({CustomerConfiguration.class,
        TramEventsPublisherConfiguration.class,
        TramCommandProducerConfiguration.class,
        SagaOrchestratorConfiguration.class,
        TramJdbcKafkaConfiguration.class})
@ComponentScan
public class CustomersServiceMain {
  public static void main(String[] args) {
    SpringApplication.run(CustomersServiceMain.class, args);
  }

  @Bean
  public ChannelMapping channelMapping(TramCommandsAndEventsIntegrationData data) {
    return DefaultChannelMapping.builder()
            .with("CustomerAggregate", data.getAggregateDestination())
            .with("customerService", data.getCommandChannel())
            .build();
  }


  @Bean
  public TramCommandsAndEventsIntegrationData tramCommandsAndEventsIntegrationData() {
    return new TramCommandsAndEventsIntegrationData();
  }

}
