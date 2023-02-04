package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

import io.eventuate.cdc.testcontainers.EventuateCdcContainer;
import io.eventuate.common.testcontainers.DatabaseContainerFactory;
import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.EventuateGenericContainer;
import io.eventuate.common.testcontainers.EventuateZookeeperContainer;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaCluster;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaContainer;
import io.eventuate.testcontainers.service.ServiceContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.lifecycle.Startables;

public class ApplicationUnderTestUsingTestContainers extends ApplicationUnderTest {
  private final EventuateZookeeperContainer zookeeper;
  private final EventuateKafkaContainer kafka;
  private final EventuateDatabaseContainer<?> customerServiceDatabase;
  private final EventuateDatabaseContainer<?> orderServiceDatabase; // This results in only one DB!
  private final ServiceContainer customerService
          // should rebuild
          ;
  private final ServiceContainer orderService
          // should rebuild
          ;
  private final ServiceContainer apiGatewayService;
  private final EventuateCdcContainer cdc
          // State for deleted databases is persisted in Kafka
          ;

  public ApplicationUnderTestUsingTestContainers() {
    EventuateKafkaCluster eventuateKafkaCluster = new EventuateKafkaCluster("CustomersAndOrdersE2ETest");

    zookeeper = eventuateKafkaCluster.zookeeper;
    kafka = eventuateKafkaCluster.kafka.dependsOn(zookeeper);

    customerServiceDatabase = DatabaseContainerFactory.makeVanillaDatabaseContainer()
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("customer-service-mysql")
            .withReuse(false);
    orderServiceDatabase = DatabaseContainerFactory.makeVanillaDatabaseContainer()
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("order-service-mysql")
            .withReuse(false);
    customerService = new ServiceContainer("../customer-service/customer-service-main/Dockerfile", "../gradle.properties")
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("customer-service")
            .withDatabase(customerServiceDatabase)
            .withZookeeper(zookeeper)
            .withKafka(kafka)
            .dependsOn(customerServiceDatabase, kafka)
            .withReuse(false);
    orderService = new ServiceContainer("../order-service/order-service-main/Dockerfile", "../gradle.properties")
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("order-service")
            .withDatabase(orderServiceDatabase)
            .withZookeeper(zookeeper)
            .withKafka(kafka)
            .dependsOn(orderServiceDatabase, kafka)
            .withReuse(false);
    apiGatewayService = new ServiceContainer("../api-gateway-service/api-gateway-service-main/Dockerfile", "../gradle.properties")
            .withNetwork(eventuateKafkaCluster.network)
            .withReuse(false) // should rebuild
            .withExposedPorts(8080)
            .withEnv("ORDER_DESTINATIONS_ORDERSERVICEURL", "http://order-service:8080")
            .withEnv("CUSTOMER_DESTINATIONS_CUSTOMERSERVICEURL", "http://customer-service:8080")
            .withEnv("SPRING_SLEUTH_ENABLED", "true")
            .withEnv("SPRING_SLEUTH_SAMPLER_PROBABILITY", "1")
            .withEnv("SPRING_ZIPKIN_BASE_URL", "http://zipkin:9411/")
            .withEnv("JAVA_OPTS", "-Ddebug")
            .withEnv("APIGATEWAY_TIMEOUT_MILLIS", "1000");
    cdc = new EventuateCdcContainer()
            .withKafkaCluster(eventuateKafkaCluster)
            .withTramPipeline(customerServiceDatabase)
            .withTramPipeline(orderServiceDatabase)
            .dependsOn(customerService, orderService)
            .withReuse(false);
  }

  @Override
  public void start() {
    Startables.deepStart(cdc, apiGatewayService).join();
  }

  private void startContainer(EventuateGenericContainer<?> container) {
    String name = container.getFirstNetworkAlias();

    Slf4jLogConsumer logConsumer2 = new Slf4jLogConsumer(logger).withPrefix("SVC " + name + ":");
    System.out.println("============ Starting " + container.getClass().getSimpleName() + "," + container);
    container.start();
    System.out.println("============ Started " + container.getClass().getSimpleName() + "," + container);
    container.followOutput(logConsumer2);

  }

  @Override
  int getCustomerServicePort() {
      return customerService.getFirstMappedPort();
  }

  @Override
  int getApigatewayPort() {
      return apiGatewayService.getFirstMappedPort();
  }

  @Override
  int getOrderServicePort() {
      return orderService.getFirstMappedPort();
  }

  @Override
  boolean exposesSwaggerUiForBackendServices() {
    return true;
  }


}
