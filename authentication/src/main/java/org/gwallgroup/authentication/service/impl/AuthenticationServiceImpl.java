package org.gwallgroup.authentication.service.impl;

import com.alibaba.nacos.common.util.Md5Utils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.gwallgroup.authentication.dto.TokenLoginDto;
import org.gwallgroup.authentication.service.AuthenticationService;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  @Resource(name = "redisTemplate")
  private ValueOperations<String, Object> valueOperations;

  @Override
  public ResponseBase tokenLogin(TokenLoginDto tokenLoginDto) {
    if ("jsen".equals(tokenLoginDto.getPrincipal()) && "1234".equals(tokenLoginDto.getToken())) {
      String token = Md5Utils.getMD5("jsen".getBytes());
      valueOperations.set(token, true, 60, TimeUnit.MINUTES);
      return ResponseHelp.simpleSucceed().add("token", token);
    }
    return ResponseHelp.prefabSimpleFailed("authentication failed");
  }
}
