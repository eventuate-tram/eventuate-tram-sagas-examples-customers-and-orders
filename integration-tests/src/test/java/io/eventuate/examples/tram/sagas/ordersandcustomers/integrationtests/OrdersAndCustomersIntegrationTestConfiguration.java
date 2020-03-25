package io.eventuate.examples.tram.sagas.ordersandcustomers.integrationtests;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({OrdersAndCustomersIntegrationCommonIntegrationTestConfiguration.class})
public class OrdersAndCustomersIntegrationTestConfiguration {
}
