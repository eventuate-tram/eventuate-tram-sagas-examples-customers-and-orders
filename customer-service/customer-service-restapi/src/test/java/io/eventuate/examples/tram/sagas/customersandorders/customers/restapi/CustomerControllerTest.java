package io.eventuate.examples.tram.sagas.customersandorders.customers.restapi;


import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerRepository;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

  @Mock
  private CustomerService customerService;
  @Mock
  private CustomerRepository customerRepository;

  @InjectMocks
  private CustomerController customerController;

  @Test
  public void shouldGetCustomers() {
    when(customerRepository.findAll()).thenReturn(Collections.emptyList());

    given()
            .standaloneSetup(customerController)
      .when()
            .get("/customers")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .contentType(JSON)
            .and().body("customers", empty());
  }
}