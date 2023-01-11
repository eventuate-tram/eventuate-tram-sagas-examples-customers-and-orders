package io.eventuate.examples.tram.sagas.ordersandcustomers.customers;


import io.eventuate.common.testcontainers.DatabaseContainerFactory;
import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.EventuateZookeeperContainer;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaCluster;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaContainer;
import io.eventuate.testcontainers.service.ServiceContainer;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomerServiceComponentTest {

    public static EventuateKafkaCluster eventuateKafkaCluster = new EventuateKafkaCluster();

    public static EventuateZookeeperContainer zookeeper = eventuateKafkaCluster.zookeeper;

    public static EventuateKafkaContainer kafka = eventuateKafkaCluster.kafka;

    public static EventuateDatabaseContainer<?> database =
            DatabaseContainerFactory.makeVanillaDatabaseContainer()
                    .withNetwork(eventuateKafkaCluster.network)
                    .withNetworkAliases("customer-service-mysql")
                    .withReuse(true);


    public static ServiceContainer service =
            new ServiceContainer()
                    .withNetwork(eventuateKafkaCluster.network)
                    .withDatabase(database)
                    .withZookeeper(zookeeper)
                    .withKafka(kafka)
                    .withReuse(false) // should rebuild
            ;

    @BeforeClass
    public static void startContainers() {
        zookeeper.start();
        kafka.start();
        database.start();
        service.start();
    }

    @Test
    public void shouldStart() {
        // HTTP
        // Messaging
    }
}