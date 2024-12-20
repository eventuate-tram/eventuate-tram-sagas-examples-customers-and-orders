package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CommonConfiguration {
  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }

  @Bean
  public RouterFunction<ServerResponse> swaggerRouter1() {
    return RouterFunctions.resources("/swagger/**", new ClassPathResource("static/swagger/"));
  }

  @Bean
  public RouterFunction<ServerResponse> swaggerRouter2() {
    return RouterFunctions.resources("/swagger-ui/**", new ClassPathResource("META-INF/static-content/swagger-ui/"));
  }

}
