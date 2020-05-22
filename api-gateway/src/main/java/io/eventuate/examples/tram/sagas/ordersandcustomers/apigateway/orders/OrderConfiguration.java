package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrderDestinations.class)
public class OrderConfiguration {
}
