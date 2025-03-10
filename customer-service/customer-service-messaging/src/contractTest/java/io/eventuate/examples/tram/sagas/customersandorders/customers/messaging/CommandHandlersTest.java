package io.eventuate.examples.tram.sagas.customersandorders.customers.messaging;

import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerService;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.eventuate.util.test.async.Eventually.eventually;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "io.eventuate.examples.tram.sagas.customersandorders:customer-service-messaging:+",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@DirtiesContext
public class CommandHandlersTest {

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    @Import({CustomerCommandHandlerConfiguration.class})
    public static class TestConfiguration {


    }

    @Autowired
    private StubFinder stubFinder;

    @MockitoBean
    private CustomerService customerService;

    @Test
    public void shouldReserveCredit() {
        stubFinder.trigger("reserveCredit");
        eventually(() -> {
            verify(customerService).reserveCredit(101L, 102L, new Money(103));
        });
    }

}
