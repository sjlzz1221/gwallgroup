package org.gwallgroup.authentication.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.Md5Utils;
import org.gwallgroup.authentication.entity.dto.TokenLoginDto;
import org.gwallgroup.authentication.entity.po.GwallAccount;
import org.gwallgroup.authentication.repository.GwallAccountRepository;
import org.gwallgroup.authentication.service.AuthenticationService;
import org.gwallgroup.authentication.service.AuthenticationServiceManager;
import org.gwallgroup.authentication.utils.SessionUtil;
import org.gwallgroup.common.constants.Xheader;
import org.gwallgroup.common.entity.LoginCheck;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/** @author jsen */
@Service("AuthenticationServiceDefault")
public class AuthenticationServiceDefaultImpl implements AuthenticationService {

  @Resource private RedisTemplate<String, JSONObject> sessionOperations;
  @Resource private RedisTemplate<Long, String> loginOperations;
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
        return ResponseHelp.simpleSucceed().add(Xheader.TOKEN, token);
      } else {
        return ResponseHelp.prefabSimpleFailed("password authentication failed");
      }
    } else {
      return ResponseHelp.prefabSimpleFailed("account authentication failed");
    }
  }
}
