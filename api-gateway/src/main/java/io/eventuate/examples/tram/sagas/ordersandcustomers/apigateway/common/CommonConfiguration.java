package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CommonConfiguration {
  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }

  @Bean
  RouterFunction<ServerResponse> routerFunction() {
    return  route(GET("/swagger-ui/index.html"), req ->
            ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html"))
                    .build());
  }
}
