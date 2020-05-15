package io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.customers;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.proxies.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apiagateway.proxies.OrderServiceProxy;
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
  public RouteLocator customerProxyRouting(RouteLocatorBuilder builder, CustomerDestinations customerDestinations) {
    return builder.routes()
            .route(r -> r.path("/customers").and().method("POST").uri(customerDestinations.getCustomerServiceUrl()))
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> orderHistoryHandlerRouting(OrderHistoryHandlers orderHistoryHandlers) {
    return RouterFunctions.route(GET("/customers/orderhistory/{customerId}"), orderHistoryHandlers::getOrderHistory);
  }

  @Bean
  public OrderHistoryHandlers orderHistoryHandlers(OrderServiceProxy orderService, CustomerServiceProxy customerService) {
    return new OrderHistoryHandlers(orderService, customerService);
  }
}
