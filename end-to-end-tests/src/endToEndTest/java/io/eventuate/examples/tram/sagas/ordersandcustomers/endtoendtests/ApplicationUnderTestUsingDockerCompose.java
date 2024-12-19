package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

public class ApplicationUnderTestUsingDockerCompose extends ApplicationUnderTest {
  @Override
  public void start() {
    // Do nothing
  }

  @Override
  public int getCustomerServicePort() {
    return 8081;
  }

  @Override
  public int getApigatewayPort() {
    return 8083;
  }

  @Override
  public int getOrderServicePort() {
    return 8081;
  }

  @Override
  boolean exposesSwaggerUiForBackendServices() {
    return true;
  }
}
