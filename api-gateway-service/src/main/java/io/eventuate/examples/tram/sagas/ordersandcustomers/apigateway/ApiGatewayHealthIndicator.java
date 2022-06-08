package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers.CustomerDestinations;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderDestinations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.OK;

public class ApiGatewayHealthIndicator implements ReactiveHealthIndicator {

    private final OrderDestinations orderDestinations;
    private final CustomerDestinations customerDestinations;

    private final WebClient client;

    public ApiGatewayHealthIndicator(OrderDestinations orderDestinations, CustomerDestinations customerDestinations, WebClient client) {
        this.orderDestinations = orderDestinations;
        this.customerDestinations = customerDestinations;
        this.client = client;
    }

    @Override
    public Mono<Health> health() {

        Mono<ClientResponse> orderHealth = client
                .get()
                .uri(orderDestinations.getOrderServiceUrl() + "/actuator/health")
                .exchange();
        Mono<ClientResponse> customerHealth = client
                .get()
                .uri(customerDestinations.getCustomerServiceUrl() + "/actuator/health")
                .exchange();
        return customerHealth.zipWith(orderHealth, (or, cr) -> or.statusCode() == OK && cr.statusCode() == OK ? Health.up().build() :
                        Health.down().withDetail("or", or.statusCode().toString()).withDetail("cr", cr.statusCode().toString()).build());
    }
}
