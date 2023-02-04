package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrderDestinations.class)
public class OrderConfiguration {
  @Bean
  public RouteLocator orderProxyRouting(RouteLocatorBuilder builder, OrderDestinations orderDestinations) {
    return builder.routes()
            .route(r -> r.path("/orders/customer/**").and().method("GET").uri(orderDestinations.getOrderServiceUrl()))
            .build();
  }
}
