package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerController {
  @GetMapping("/swagger-ui.html")
  public Resource getFile() {
    return new ClassPathResource("META-INF/swagger-ui/index.html");
  }
}
