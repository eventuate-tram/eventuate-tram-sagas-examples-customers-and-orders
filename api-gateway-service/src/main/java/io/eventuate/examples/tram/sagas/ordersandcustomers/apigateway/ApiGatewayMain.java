package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway;


import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers.CustomerDestinations;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.orders.OrderDestinations;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.NoopDnsCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ApiGatewayMain {

  @Bean
  public ReactiveHealthIndicator apiGatewayHealthIndicator(OrderDestinations orderDestinations, CustomerDestinations customerDestinations, WebClient client) {
    return new ApiGatewayHealthIndicator(orderDestinations, customerDestinations, client);
  }

  @Bean
  public HttpClientCustomizer myHttpClientCustomizer() {

    // https://github.com/spring-cloud/spring-cloud-gateway/issues/561

    return originalHttpClient -> {
      DnsNameResolverBuilder dnsResolverBuilder = new DnsNameResolverBuilder()
              .channelFactory(NioDatagramChannel::new)
              .resolveCache(NoopDnsCache.INSTANCE);
      return originalHttpClient.resolver(new DnsAddressResolverGroup(dnsResolverBuilder));
    };
  }

  public static void main(String[] args) {
    ReactorDebugAgent.init();
    SpringApplication.run(ApiGatewayMain.class, args);
  }
}

