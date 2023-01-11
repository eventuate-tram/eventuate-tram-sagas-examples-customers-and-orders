package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.CustomerService;
import io.eventuate.tram.commands.common.CommandNameMapping;
import io.eventuate.tram.commands.common.DefaultCommandNameMapping;
import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.eventuate.tram.sagas.spring.inmemory.TramSagaInMemoryConfiguration;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import io.eventuate.tram.spring.cloudcontractsupport.EventuateContractVerifierConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.junit.Before;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = OrderserviceBase.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
@AutoConfigureDataJdbc
public abstract class OrderserviceBase {

  @Configuration
  //@EnableAutoConfiguration // (exclude = {DataSourceAutoConfiguration.class, EventuateTramKafkaMessageConsumerAutoConfiguration.class})
  @Import({CustomerCommandHandlerConfiguration.class,
          EventuateContractVerifierConfiguration.class,
          TramSagaInMemoryConfiguration.class,
          SagaParticipantConfiguration.class,
          TramMessageProducerJdbcConfiguration.class})
  public static class TestConfiguration {

    @Bean
    public ChannelMapping channelMapping() {
      return DefaultChannelMapping.builder().build();
    }

    @Bean
    public CommandNameMapping commandNameMapping() {
      return new DefaultCommandNameMapping();
    }


  }

  @MockBean
  private CustomerService customerService;

  @Before
  public void setUp() {

  }

}
