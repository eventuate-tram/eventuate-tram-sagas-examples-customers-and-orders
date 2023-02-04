package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.messaging.replies.CustomerCreditReserved;
import io.eventuate.tram.commands.common.CommandNameMapping;
import io.eventuate.tram.commands.common.DefaultCommandNameMapping;
import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import io.eventuate.tram.sagas.spring.inmemory.TramSagaInMemoryConfiguration;
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.spring.cloudcontractsupport.EventuateContractVerifierConfiguration;
import io.eventuate.tram.spring.cloudcontractsupport.EventuateTramRoutesConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.BatchStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= CustomerServiceProxyTest.TestConfiguration.class,
        webEnvironment= SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = {"eventuate-tram-sagas-examples-customers-and-orders.customer-service:customer-service-messaging"})
@DirtiesContext
public class CustomerServiceProxyTest {


  @Configuration
  @AutoConfigureDataJdbc
  @Import({TramSagaInMemoryConfiguration.class, EventuateContractVerifierConfiguration.class, SagaOrchestratorConfiguration.class})
  public static class TestConfiguration {

    @Bean
    public ChannelMapping channelMapping() {
      return DefaultChannelMapping.builder().build();
    }

    @Bean
    public CommandNameMapping commandNameMapping() {
      return new DefaultCommandNameMapping();
    }

    @Bean
    public EventuateTramRoutesConfigurer eventuateTramRoutesConfigurer(BatchStubRunner batchStubRunner) {
      return new EventuateTramRoutesConfigurer(batchStubRunner);
    }

    @Bean
    public CustomerServiceProxy customerServiceProxy() {
      return new CustomerServiceProxy();
    }

    @Bean
    public SagaMessagingTestHelper sagaMessagingTestHelper(ContractVerifierMessaging contractVerifierMessaging, SagaCommandProducer sagaCommandProducer, IdGenerator idGenerator) {
      return new SagaMessagingTestHelper(contractVerifierMessaging, sagaCommandProducer, idGenerator);
    }
  }

  @Autowired
  private SagaMessagingTestHelper sagaMessagingTestHelper;

  @Autowired
  private CustomerServiceProxy customerServiceProxy;

  @Test
  public void shouldSuccessfullyCreateTicket() {
    String sagaType = CreateOrderSaga.class.getName();

    long orderId = 1L;
    Long customerId = 1511300065921L;
    Money orderTotal = new Money("61.70");

    CustomerCreditReserved reply = sagaMessagingTestHelper.sendAndReceiveCommand(customerServiceProxy.reserveCredit(orderId, customerId, orderTotal), CustomerCreditReserved.class, sagaType);

    assertNotNull(reply);

  }

}