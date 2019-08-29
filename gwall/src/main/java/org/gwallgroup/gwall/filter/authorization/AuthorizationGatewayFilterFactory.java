package org.gwallgroup.gwall.filter.authorization;

import com.alibaba.nacos.common.util.Md5Utils;
import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.AccessService;
import org.gwallgroup.gwall.constants.Xheader;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;

/**
 * 验证用户Api权限，调用授权模块
 */
@Component
public class AuthorizationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {

  @Reference(check = false, lazy = true)
  private AccessService accessService;

  public AuthorizationGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      System.out.println("AuthorizationGatewayFilterFactory");
      ServerHttpRequest req = exchange.getRequest();
      String ac = req.getHeaders().getFirst(Xheader.X_X);
      if (!Xheader.AC.equals(ac)) {
        setResponseStatus(exchange, HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
      }
      String serviceType = getAttr(Xheader.X_ST, req, null);
      String version = getAttr(Xheader.X_V, req, null);
      String permissions = getAttr(Xheader.X_P, req, null);
      if (accessService.isAccess(serviceType, version, req.getPath().value(), permissions)) {
        setResponseStatus(exchange, HttpStatus.OK);
        return chain.filter(exchange);
      } else {
        setResponseStatus(exchange, HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
      }
    };
  }

  public static class Config {

  }

  private String getAttr(String key, ServerHttpRequest request, String defaultValue) {
    if (key == null) {
      return defaultValue;
    }
    String result = request.getHeaders().getFirst(key);
    if (result != null) {
      return result;
    }
    result = request.getQueryParams().getFirst(Md5Utils.getMD5(key.getBytes()));
    if (result != null) {
      return result;
    }
    HttpCookie cookie = request.getCookies().getFirst(key);
    if (cookie != null) {
      result = cookie.getValue();
    }
    if (result != null) {
      return result;
    }
    return defaultValue;
  }

}
