package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies;

public class UnknownProxyException extends RuntimeException{
  public UnknownProxyException(String message) {
    super(message);
  }
}
