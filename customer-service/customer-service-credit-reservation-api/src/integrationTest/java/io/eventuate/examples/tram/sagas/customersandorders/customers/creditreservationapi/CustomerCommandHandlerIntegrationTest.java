package io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi;

import io.eventuate.common.testcontainers.DatabaseContainerFactory;
import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerService;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.commands.ReserveCreditCommand;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaCluster;
import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.spring.flyway.EventuateTramFlywayMigrationConfiguration;
import io.eventuate.tram.spring.testing.kafka.producer.EventuateKafkaTestCommandProducerConfiguration;
import io.eventuate.tram.spring.testing.outbox.commands.CommandOutboxTestSupport;
import io.eventuate.tram.spring.testing.outbox.commands.CommandOutboxTestSupportConfiguration;
import io.eventuate.util.test.async.Eventually;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.lifecycle.Startables;

import java.util.Collections;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomerCommandHandlerIntegrationTest {

  private static final EventuateKafkaCluster eventuateKafkaCluster = new EventuateKafkaCluster();

  private static final EventuateDatabaseContainer database = DatabaseContainerFactory.makeVanillaDatabaseContainer();

  @DynamicPropertySource
  static void registerMySqlProperties(DynamicPropertyRegistry registry) {
    eventuateKafkaCluster.kafka.dependsOn(eventuateKafkaCluster.zookeeper);
    Startables.deepStart(eventuateKafkaCluster.kafka, database).join();

    Stream.of(database, eventuateKafkaCluster.zookeeper, eventuateKafkaCluster.kafka).forEach(container ->
      container.registerProperties(registry::add));
  }

  // TODO - autoconfigure?? EventuateTramFlywayMigrationConfiguration

  @Configuration
  @EnableAutoConfiguration
  @Import({CustomerMessagingConfiguration.class,
      EventuateKafkaTestCommandProducerConfiguration.class,
      EventuateTramFlywayMigrationConfiguration.class,
      CommandOutboxTestSupportConfiguration.class})
  static public class Config {

  }

  @MockitoBean
  private CustomerService customerService;


  @Autowired
  private CommandProducer commandProducer;

  @Autowired
  private CommandOutboxTestSupport commandOutboxTestSupport;

  @Test
  public void shouldHandleReserveCreditCommand() {

    String replyTo = "my-reply-to-channel-" + System.currentTimeMillis();

    long customerId = System.currentTimeMillis();
    long orderId = 102L;
    Money orderTotal = new Money("12.34");

    sendCommand(customerId, orderId, orderTotal, replyTo);

    Eventually.eventually(() -> {

      verify(customerService).reserveCredit(customerId, orderId, orderTotal);

      commandOutboxTestSupport.assertCommandReplyMessageSent(replyTo);
    });
  }

  private void sendCommand(long customerId, long orderId, Money orderTotal, String replyTo) {
    commandProducer.send("customerService", new ReserveCreditCommand(customerId, orderId, orderTotal), replyTo, Collections.emptyMap());
  }


}
