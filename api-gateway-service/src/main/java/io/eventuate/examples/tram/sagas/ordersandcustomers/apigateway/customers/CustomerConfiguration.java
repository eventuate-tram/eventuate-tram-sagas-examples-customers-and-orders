package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.OrderServiceProxy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@EnableConfigurationProperties(CustomerDestinations.class)
public class CustomerConfiguration {

  @Bean
  public RouterFunction<ServerResponse> orderHistoryHandlerRouting(OrderHistoryHandlers orderHistoryHandlers) {
    return RouterFunctions.route(GET("/customers/{customerId}/orderhistory"), orderHistoryHandlers::getOrderHistory);
  }

  @Bean
  public OrderHistoryHandlers orderHistoryHandlers(OrderServiceProxy orderService, CustomerServiceProxy customerService) {
    return new OrderHistoryHandlers(orderService, customerService);
  }

  @Bean
  public RouteLocator customerProxyRouting(RouteLocatorBuilder builder, CustomerDestinations customerDestinations) {
    return builder.routes()
            .route(r -> r.path("/customers/**").and().method("GET").uri(customerDestinations.getCustomerServiceUrl()))
            .build();
  }
}
