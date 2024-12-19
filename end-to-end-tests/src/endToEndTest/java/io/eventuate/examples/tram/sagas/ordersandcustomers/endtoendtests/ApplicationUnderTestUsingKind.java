package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

public class ApplicationUnderTestUsingKind extends ApplicationUnderTest {
  @Override
  public void start() {

  }

  @Override
  public int getApigatewayPort() {
    return 80;
  }

  @Override
  public int getCustomerServicePort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getOrderServicePort() {
    throw new UnsupportedOperationException();
  }

  @Override
  boolean exposesSwaggerUiForBackendServices() {
    return false;
  }
}
