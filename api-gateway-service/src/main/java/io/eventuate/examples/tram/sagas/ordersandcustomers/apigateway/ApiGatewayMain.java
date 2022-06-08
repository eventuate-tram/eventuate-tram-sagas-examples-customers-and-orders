package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway;


import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers.CustomerDestinations;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderDestinations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ApiGatewayMain {

  @Bean
  public ReactiveHealthIndicator apiGatewayHealthIndicator(OrderDestinations orderDestinations, CustomerDestinations customerDestinations, WebClient client) {
    return new ApiGatewayHealthIndicator(orderDestinations, customerDestinations, client);
  }

  public static void main(String[] args) {
    ReactorDebugAgent.init();
    SpringApplication.run(ApiGatewayMain.class, args);
  }
}

