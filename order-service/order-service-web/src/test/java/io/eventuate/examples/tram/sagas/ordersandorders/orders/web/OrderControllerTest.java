package io.eventuate.examples.tram.sagas.ordersandorders.orders.web;


import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.web.OrderController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

  @Mock
  private OrderService orderService;
  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private OrderController orderController;

  @Test
  public void shouldGetOrders() {
    when(orderRepository.findAll()).thenReturn(Collections.emptyList());

    given()
            .standaloneSetup(orderController)
      .when()
            .get("/orders")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .contentType(JSON)
            .and().body("orders", empty());
  }
}