package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

import org.springframework.http.HttpStatusCode;

public class UnknownProxyException extends RuntimeException{
  public UnknownProxyException(String message) {
    super(message);
  }

  static UnknownProxyException make(String path, HttpStatusCode statusCode, String param) {
    return new UnknownProxyException("Unknown: " + path + param + "=" + statusCode);
  }
}
