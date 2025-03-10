package io.eventuate.examples.tram.sagas.ordersandcustomers.orders;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.commands.ReserveCreditCommand;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies.CustomerCreditLimitExceeded;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies.CustomerCreditReserved;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.replies.CustomerNotFound;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderDomainConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.persistence.OrderPersistenceConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.proxies.customers.CustomerServiceProxyConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.CreateOrderSaga;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.OrderSagasConfiguration;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.web.OrderWebConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.testing.AsyncApiDocument;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceInProcessComponentTest {

  protected static Logger logger = LoggerFactory.getLogger(OrderServiceInProcessComponentTest.class);

  @Configuration
  @EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
  @Import({OrderWebConfiguration.class,
      OrderPersistenceConfiguration.class,
      OrderDomainConfiguration.class,
      CustomerServiceProxyConfiguration.class,
      OrderSagasConfiguration.class,
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
  void shouldExposeSwaggerUI() {
    RestAssured.given()
        .get("/swagger-ui/index.html")
        .then()
        .statusCode(200);
  }

  @Test
  public void shouldExposeSpringWolf() {
    AsyncApiDocument doc = AsyncApiDocument.getSpringWolfDoc();

    String createOrderSaga = CreateOrderSaga.class.getName();

    doc.assertSendsMessage(createOrderSaga + "-customerService-reserveCredit",
        "customerService",
        ReserveCreditCommand.class.getName());

    for (var replyClass : List.of(CustomerCreditReserved.class, CustomerNotFound.class, CustomerCreditLimitExceeded.class)) {
      doc.assertReceivesMessage("receive-" + createOrderSaga + "-reply",
          createOrderSaga + "-reply",
          replyClass.getName());
    }

  }

  @Test
  public void shouldExposeSpringWolfUi() {

    RestAssured.given()
        .get("/springwolf/asyncapi-ui.html")
        .then()
        .statusCode(200);

  }
}
