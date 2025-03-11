package io.eventuate.examples.tram.sagas.customersandorders.customers;


import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerDomainConfiguration;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.CustomerCommandHandler;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.CustomerCommandHandlerConfiguration;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.CustomerCreditReserved;
import io.eventuate.examples.tram.sagas.customersandorders.customers.creditreservationapi.replies.CustomerNotFound;
import io.eventuate.examples.tram.sagas.customersandorders.customers.persistence.CustomerPersistenceConfiguration;
import io.eventuate.examples.tram.sagas.customersandorders.customers.restapi.CustomerRestApiConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.testing.AsyncApiDocument;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Set;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerServiceInProcessComponentTest {

    protected static Logger logger = LoggerFactory.getLogger(CustomerServiceInProcessComponentTest.class);

    @Configuration
    @EnableAutoConfiguration
    @Import({CustomerRestApiConfiguration.class, CustomerPersistenceConfiguration.class,
        CustomerDomainConfiguration.class,
        CustomerCommandHandlerConfiguration.class,
        TramInMemoryConfiguration.class
    })
    static public class Config {

    }

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void shouldStart() {
    }

    @Test
    void shouldExposeSwaggerUI() {
        RestAssured.given()
            .get("/swagger-ui/index.html")
            .then()
            .statusCode(200);
    }

    @Test
    public void shouldExposeSpringWolf() {
        AsyncApiDocument doc = AsyncApiDocument.getSpringWolfDoc();

        doc.assertReceivesMessageAndReplies(CustomerCommandHandler.class.getName() + ".reserveCredit",
            "customerService",
            ReserveCreditCommand.class.getName(),
            Set.of(CustomerNotFound.class.getName(), CustomerCreditLimitExceeded.class.getName(), CustomerCreditReserved.class.getName()));
    }

}
