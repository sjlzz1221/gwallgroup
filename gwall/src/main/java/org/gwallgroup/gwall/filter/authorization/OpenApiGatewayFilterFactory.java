package org.gwallgroup.gwall.filter.authorization;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * 开发api，验证
 */
@Component
@Deprecated
public class OpenApiGatewayFilterFactory extends AbstractGatewayFilterFactory<OpenApiGatewayFilterFactory.Config> {

  public OpenApiGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      System.out.println("OpenApiGatewayFilterFactory");
      return chain.filter(exchange);
    };
  }

  public static class Config {

  }

}
