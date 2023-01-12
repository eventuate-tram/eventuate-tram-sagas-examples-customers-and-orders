package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ApplicationUnderTest {

  protected Logger logger = LoggerFactory.getLogger(getClass());



  public static ApplicationUnderTest make() {
    return System.getProperty("endToEndTest.use.dockerCompose") != null ? new ApplicationUnderTestUsingDockerCompose() : new ApplicationUnderTestUsingTestContainers();
  }

  public abstract void start();


  public String apiGatewayBaseUrl(String hostName, String path, String... pathElements) {
    return BaseUrlUtils.baseUrl(hostName, path, getApigatewayPort(), pathElements);
  }

  abstract int getCustomerServicePort();

  abstract int getApigatewayPort();

  abstract int getOrderServicePort();

}
