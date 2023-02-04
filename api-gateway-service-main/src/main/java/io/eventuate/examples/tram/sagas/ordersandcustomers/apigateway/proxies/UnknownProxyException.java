package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import org.springframework.http.HttpStatus;

public class UnknownProxyException extends RuntimeException{
  public UnknownProxyException(String message) {
    super(message);
  }

  static UnknownProxyException make(String path, HttpStatus statusCode, String param) {
    return new UnknownProxyException("Unknown: " + path + param + "=" + statusCode);
  }
}
