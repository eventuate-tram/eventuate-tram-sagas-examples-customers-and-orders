package io.eventuate.examples.tram.sagas.customersandorders.customers.messaging;

import io.eventuate.examples.tram.sagas.customersandorders.customers.messaging.replies.CustomerCreditReserved;
import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = AbstractMessagingContractTest.TestConfig.class)
public abstract class AbstractMessagingContractTest {

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfig {

    }

    @Autowired
    private MessageProducer messageProducer;

    protected void creditReserved() {
        messageProducer.send("reserveCreditReply",
                withSuccess(new CustomerCreditReserved()));
    }

    protected void reserveCredit() {
        // This is referenced by a disabled test
        throw new UnsupportedOperationException();
    }
}
