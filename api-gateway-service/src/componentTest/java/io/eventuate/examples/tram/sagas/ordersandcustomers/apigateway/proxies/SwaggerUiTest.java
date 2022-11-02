package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SwaggerUiTest.Config.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SwaggerUiTest {

  @LocalServerPort
  int port;

  private String hostName = "localhost";

  @Configuration
  @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
  @Import(ProxyConfiguration.class)
  static public class Config {
  }

  @Test
  public void testSwaggerUiUrls() throws IOException {
    testSwaggerUiUrl(port);
  }

  private void testSwaggerUiUrl(int port) throws IOException {
    assertUrlStatusIsOk(String.format("http://%s:%s/swagger-ui/index.html", hostName, port));
  }

  private void assertUrlStatusIsOk(String url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    if (connection.getResponseCode() != 200)
      Assert.fail(String.format("Expected 200 for %s, got %s", url, connection.getResponseCode()));
  }


}