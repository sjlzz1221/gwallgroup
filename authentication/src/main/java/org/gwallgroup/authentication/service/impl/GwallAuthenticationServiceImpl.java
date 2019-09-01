package org.gwallgroup.authentication.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.Md5Utils;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gwallgroup.authentication.entity.dto.TokenLoginDto;
import org.gwallgroup.authentication.entity.po.GwallAccount;
import org.gwallgroup.authentication.repository.GwallAccountRepository;
import org.gwallgroup.authentication.service.GwallAuthenticationService;
import org.gwallgroup.authentication.utils.SessionUtil;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.gwallgroup.common.web.constants.Xheader;
import org.gwallgroup.common.web.utils.help.AttributeHelp;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class GwallAuthenticationServiceImpl implements GwallAuthenticationService {

  @Resource
  @Qualifier("redisSessionTemplate")
  private RedisTemplate<String, JSONObject> sessionOperations;

  @Resource
  @Qualifier("redisLoginTemplate")
  private RedisTemplate<Long, String> loginOperations;

  @Resource private GwallAccountRepository gwallAccountRepository;

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
//        JSONObject domain = new JSONObject();
//        domain.put(Xheader.X_P, "");
//        domain.put(Xheader.X_MAN, JSON.toJSON(exist));
//        loginOperations.boundValueOps(exist.getId()).set(newSession, 6, TimeUnit.HOURS);
//        sessionOperations.boundValueOps(newSession).set(domain, 6, TimeUnit.HOURS);
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String tokenKey = AttributeHelp.getHeader(Xheader.X_TK, request, Xheader.AUTHORIZATION);

        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        if (response != null) {
          Cookie cookie = new Cookie(tokenKey, newSession);
          cookie.setPath("/");
          response.addCookie(cookie);
          response.setHeader(tokenKey, newSession);
          response.setHeader(Xheader.X_P, "");
          response.setHeader(Xheader.X_MAN, JSON.toJSONString(exist));
        }
        return ResponseHelp.simpleSucceed()
            .add(tokenKey, newSession)
            .add("currentAuthority", "admin");
      } else {
        return ResponseHelp.prefabSimpleFailed("password authentication failed");
      }
    } else {
      return ResponseHelp.prefabSimpleFailed("account authentication failed");
    }
  }
}
