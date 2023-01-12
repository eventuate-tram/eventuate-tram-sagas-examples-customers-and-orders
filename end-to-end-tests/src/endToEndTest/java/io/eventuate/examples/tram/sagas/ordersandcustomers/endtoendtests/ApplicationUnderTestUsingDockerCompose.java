package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

public class ApplicationUnderTestUsingDockerCompose extends ApplicationUnderTest {
  @Override
  public void start() {
    // Do nothing
  }

  @Override
  int getCustomerServicePort() {
    return 8081;
  }

  @Override
  int getApigatewayPort() {
    return 8083;
  }

  @Override
  int getOrderServicePort() {
    return 8081;
  }
}
