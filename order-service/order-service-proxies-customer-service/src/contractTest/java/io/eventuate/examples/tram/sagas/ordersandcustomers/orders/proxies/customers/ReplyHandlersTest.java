package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.proxies.customers;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies.CustomerCreditReserved;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "io.eventuate.examples.tram.sagas.ordersandcustomers:customer-service-messaging:+",
        stubsMode = StubRunnerProperties.StubsMode.REMOTE
        )
@DirtiesContext
public class ReplyHandlersTest {

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfiguration {

        @Bean
        public TestReplyConsumer testMessageConsumer(MessageConsumer messageConsumer) {
            return new TestReplyConsumer(ReplyHandlersTest.class.getName(), "reserveCreditReply", messageConsumer);
        }

    }

    @Autowired
    private StubFinder stubFinder;

    @Autowired
    private TestReplyConsumer testReplyConsumer;


    @Test
    public void shouldHandleCreditReservedReply() {
        stubFinder.trigger("creditReserved");

        testReplyConsumer.assertReplyOfTypeReceived(CustomerCreditReserved.class);
    }

}
