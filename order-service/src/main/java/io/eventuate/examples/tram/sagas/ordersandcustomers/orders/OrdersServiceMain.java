package io.eventuate.examples.tram.sagas.ordersandcustomers.orders;

import io.eventuate.examples.tram.sagas.ordersandcustomers.TramCommandsAndEventsIntegrationData;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.web.OrderWebConfiguration;
import io.eventuate.javaclient.spring.jdbc.EventuateSchema;
import io.eventuate.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.sagas.orchestration.AggregateInstanceSubscriptionsDAO;
import io.eventuate.tram.sagas.orchestration.SagaInstanceRepository;
import io.eventuate.tram.sagas.orchestration.SagaInstanceRepositoryJdbc;
import io.eventuate.tram.sagas.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.sagas.participant.SagaLockManagerImpl;
import io.eventuate.tram.sagas.participant.SagaParticipantConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;

@SpringBootApplication
@Configuration
@Import({OrderWebConfiguration.class,
        OrderConfiguration.class,
        TramEventsPublisherConfiguration.class,
        TramCommandProducerConfiguration.class,
        SagaOrchestratorConfiguration.class,
        TramJdbcKafkaConfiguration.class,
        SagaParticipantConfiguration.class})
@ComponentScan
public class OrdersServiceMain {

  @Value("${eventuate.database.schema:#{null}}")
  private String eventuateDatabaseSchema;

  public static void main(String[] args) {
    SpringApplication.run(OrdersServiceMain.class, args);
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

  @Bean
  @Primary
  public AggregateInstanceSubscriptionsDAO aggregateInstanceSubscriptionsDAO() {
    return new AggregateInstanceSubscriptionsDAO(new EventuateSchema(eventuateDatabaseSchema));
  }

  @Bean
  @Primary
  public SagaInstanceRepository sagaInstanceRepository() {
    return new SagaInstanceRepositoryJdbc(new EventuateSchema(eventuateDatabaseSchema));
  }

  @Bean
  @Primary
  public SagaLockManagerImpl sagaLockManager() {
    return new SagaLockManagerImpl(new EventuateSchema(eventuateDatabaseSchema));
  }
}
