package org.gwallgroup.gwall.filter.authentication;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;

import com.alibaba.nacos.common.util.Md5Utils;
import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.LoginStatusService;
import org.gwallgroup.common.vo.LoginCheck;
import org.gwallgroup.gwall.constants.Xheader;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * 验证用户登入，调用认证模块
 */
@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

  @Reference(check = false, lazy = true)
  private LoginStatusService loginStatusService;

  public AuthenticationGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      System.out.println("AuthenticationGatewayFilterFactory");
      ServerHttpRequest req = exchange.getRequest();
      String serviceType = getAttr(Xheader.X_ST, req, null);
      String loginType = getAttr(Xheader.X_LT, req, null);
      String version = getAttr(Xheader.X_V, req, null);
      String tokenKey = getAttr(Xheader.X_TK, req, "Authorization");
      String token = getAttr(tokenKey, req, null);
      LoginCheck loginCheck = loginStatusService.isLogin(serviceType, loginType, version, tokenKey, token);
      setResponseStatus(exchange, HttpStatus.resolve(loginCheck.getCode()));
      req.getHeaders().add(Xheader.X_P, loginCheck.getPermissions());
      req.getHeaders().add(Xheader.X_X, Xheader.AC);
      if (loginCheck.getCode() == HttpStatus.OK.value()) {
        return chain.filter(exchange);
      } else {
        // 401 or else complete
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
