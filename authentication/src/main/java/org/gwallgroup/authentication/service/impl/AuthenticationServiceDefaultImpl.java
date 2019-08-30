package org.gwallgroup.authentication.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.Md5Utils;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.gwallgroup.authentication.entity.dto.TokenLoginDto;
import org.gwallgroup.authentication.entity.po.GwallAccount;
import org.gwallgroup.authentication.repository.GwallAccountRepository;
import org.gwallgroup.authentication.service.AuthenticationService;
import org.gwallgroup.authentication.service.AuthenticationServiceManager;
import org.gwallgroup.common.constants.Xheader;
import org.gwallgroup.common.entity.LoginCheck;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author jsen
 */
@Service("AuthenticationServiceDefault")
public class AuthenticationServiceDefaultImpl implements AuthenticationService {

  @Resource
  private RedisTemplate<String, JSONObject> sessionOperations;
  @Resource
  private RedisTemplate<Long, String> loginOperations;
  @Resource
  private GwallAccountRepository gwallAccountRepository;

  @PostConstruct
  public void init() {
    AuthenticationServiceManager.register(Xheader.DEFAULT, this);
  }

  @Override
  public LoginCheck check(String tokenKey, String token) {
    JSONObject exist = sessionOperations.boundValueOps(token).get();
    if (exist != null) {
      return new LoginCheck().setCode(200).setPermissions(exist.getString(Xheader.X_P));
    } else {
      return new LoginCheck().setCode(401);
    }
  }

  @Override
  public ResponseBase login(TokenLoginDto tokenLoginDto) {
    GwallAccount exist = gwallAccountRepository.findFirstByName(tokenLoginDto.getPrincipal());
    if (exist != null) {
      String token = Md5Utils.getMD5("jsen".getBytes(Charset.defaultCharset()));
      if (token.equals(exist.getPassword())) {
        String oldToken = loginOperations.boundValueOps(exist.getId()).get();
        if (oldToken != null) {
          sessionOperations.delete(oldToken);
        }
        JSONObject domain = new JSONObject();
        domain.put("x-p", "");
        loginOperations.boundValueOps(exist.getId()).set(token, 6, TimeUnit.HOURS);
        sessionOperations.boundValueOps(token).set(domain, 6, TimeUnit.HOURS);
        return ResponseHelp.simpleSucceed().add("token", token);
      } else {
        return ResponseHelp.prefabSimpleFailed("password authentication failed");
      }
    } else {
      return ResponseHelp.prefabSimpleFailed("account authentication failed");
    }
  }
}
