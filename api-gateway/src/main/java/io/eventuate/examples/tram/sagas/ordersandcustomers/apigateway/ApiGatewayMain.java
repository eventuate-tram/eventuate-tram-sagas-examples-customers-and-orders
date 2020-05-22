package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ApiGatewayMain {

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayMain.class, args);
  }
}

