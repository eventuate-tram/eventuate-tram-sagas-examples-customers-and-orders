package io.eventuate.examples.tram.sagas.ordersandcustomers.orders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
public class OrderServiceMain {

  private Logger _logger = LoggerFactory.getLogger(getClass());


  @Bean
  public OncePerRequestFilter logFilter() {
    return new OncePerRequestFilter() {
      @Override
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        _logger.info("Path: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
        _logger.info("Path: {} {}", request.getRequestURI(), response.getStatus());
      }
    };
  }


  public static void main(String[] args) {
    SpringApplication.run(OrderServiceMain.class, args);
  }

}
