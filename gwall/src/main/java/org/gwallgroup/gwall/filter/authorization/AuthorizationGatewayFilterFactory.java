package org.gwallgroup.gwall.filter.authorization;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * 验证用户Api权限，调用授权模块
 */
@Component
public class AuthorizationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {

  public AuthorizationGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      System.out.println("AuthorizationGatewayFilterFactory");
      return chain.filter(exchange);
    };
  }

  public static class Config {

  }

}
