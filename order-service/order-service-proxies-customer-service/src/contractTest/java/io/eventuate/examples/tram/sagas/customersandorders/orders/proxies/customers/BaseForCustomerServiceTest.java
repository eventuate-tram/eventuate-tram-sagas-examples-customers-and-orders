package io.eventuate.examples.tram.sagas.customersandorders.orders.proxies.customers;

import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.examples.common.money.Money;
import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {BaseForCustomerServiceTest.TestConfig.class})
public abstract class BaseForCustomerServiceTest {

    static {
        // JSonMapper.objectMapper.findAndRegisterModules() - seems useful
        JSonMapper.objectMapper.registerModule(new MoneyModule());
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    @Import({CustomerServiceProxyConfiguration.class})
    public static class TestConfig {

    }

    @Autowired
    private CustomerServiceProxy customerServiceProxy;

    @Autowired
    private CommandProducer commandProducer;

    protected void reserveCredit() {
        var command = customerServiceProxy.reserveCredit(102, 101L, new Money(103));
        String replyTo = "reserveCreditReply";
        commandProducer.send(command.getDestinationChannel(), command.getCommand(), replyTo, Collections.emptyMap());
    }

}