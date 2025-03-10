package io.eventuate.examples.tram.sagas.customersandorders.apigateway.proxies.common;

import org.springframework.http.HttpStatusCode;

public class UnknownProxyException extends RuntimeException{
  public UnknownProxyException(String message) {
    super(message);
  }

  public static UnknownProxyException make(String path, HttpStatusCode statusCode, String param) {
    return new UnknownProxyException("Unknown: " + path + param + "=" + statusCode);
  }
}
