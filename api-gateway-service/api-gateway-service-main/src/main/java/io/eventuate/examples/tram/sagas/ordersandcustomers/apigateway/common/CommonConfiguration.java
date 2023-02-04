package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CommonConfiguration {
  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }

}
