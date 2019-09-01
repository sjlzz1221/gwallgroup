package org.gwallgroup.authentication.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.Md5Utils;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gwallgroup.authentication.entity.dto.TokenLoginDto;
import org.gwallgroup.authentication.entity.po.GwallAccount;
import org.gwallgroup.authentication.repository.GwallAccountRepository;
import org.gwallgroup.authentication.service.AuthenticationService;
import org.gwallgroup.authentication.service.AuthenticationServiceManager;
import org.gwallgroup.authentication.utils.SessionUtil;
import org.gwallgroup.common.entity.LoginCheck;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.gwallgroup.common.web.constants.Xheader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** @author jsen */
@Service("AuthenticationServiceDefault")
public class AuthenticationServiceDefaultImpl implements AuthenticationService {

  @Resource
  @Qualifier("redisSessionTemplate")
  private RedisTemplate<String, JSONObject> sessionOperations;

  @Resource
  @Qualifier("redisLoginTemplate")
  private RedisTemplate<Long, String> loginOperations;

  @Resource private GwallAccountRepository gwallAccountRepository;

  @PostConstruct
  public void init() {
    AuthenticationServiceManager.register(Xheader.DEFAULT, this);
  }

  @Override
  public LoginCheck check(String tokenKey, String token) {
    JSONObject exist = sessionOperations.boundValueOps(token).get();
    if (exist != null) {
      return new LoginCheck()
          .setCode(200)
          .setPermissions(exist.getString(Xheader.X_P))
          .setXMan(exist.getString(Xheader.X_MAN));
    } else {
      return new LoginCheck().setCode(401);
    }
  }

  @Override
  public ResponseBase login(TokenLoginDto tokenLoginDto) {
    GwallAccount exist = gwallAccountRepository.findFirstByName(tokenLoginDto.getPrincipal());
    if (exist != null) {
      String token = Md5Utils.getMD5(tokenLoginDto.getToken().getBytes(Charset.defaultCharset()));
      if (token.equals(exist.getPassword())) {
        String oldSession = loginOperations.boundValueOps(exist.getId()).get();
        if (oldSession != null) {
          sessionOperations.delete(oldSession);
        }
        String newSession = SessionUtil.getSessionId();
        JSONObject domain = new JSONObject();
        domain.put(Xheader.X_P, "");
        domain.put(Xheader.X_MAN, JSON.toJSON(exist));
        loginOperations.boundValueOps(exist.getId()).set(newSession, 6, TimeUnit.HOURS);
        sessionOperations.boundValueOps(newSession).set(domain, 6, TimeUnit.HOURS);
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        if (response != null) {
          Cookie cookie = new Cookie(Xheader.AUTHORIZATION, newSession);
          cookie.setPath("/");
          response.addCookie(cookie);
        }
        return ResponseHelp.simpleSucceed()
            .add(Xheader.AUTHORIZATION, newSession)
            .add("currentAuthority", "admin");
      } else {
        return ResponseHelp.prefabSimpleFailed("password authentication failed");
      }
    } else {
      return ResponseHelp.prefabSimpleFailed("account authentication failed");
    }
  }
}
