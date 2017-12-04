package io.eventuate.examples.tram.sagas.ordersandcustomers.integrationtests;

import io.eventuate.javaclient.spring.jdbc.EventuateSchema;
import io.eventuate.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.sagas.orchestration.AggregateInstanceSubscriptionsDAO;
import io.eventuate.tram.sagas.orchestration.SagaInstanceRepository;
import io.eventuate.tram.sagas.orchestration.SagaInstanceRepositoryJdbc;
import io.eventuate.tram.sagas.participant.SagaLockManagerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import({OrdersAndCustomersIntegrationCommonIntegrationTestConfiguration.class, TramJdbcKafkaConfiguration.class})
public class OrdersAndCustomersIntegrationTestConfiguration {

  @Value("${eventuate.database.schema:#{null}}")
  private String eventuateDatabaseSchema;

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
