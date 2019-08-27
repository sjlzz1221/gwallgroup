package org.gwallgroup.authentication.service.impl;

import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.Service;
import org.gwallgroup.common.dubbo.LoginStatusService;
import org.springframework.data.redis.core.ValueOperations;

@Service
public class LoginStatusServiceImpl implements LoginStatusService {

  @Resource(name = "redisTemplate")
  private ValueOperations<String, Object> valueOperations;

  /**
   * 检查是否登入
   * @param serviceType 服务类型（登录的服务，一般指不同的用户数据库）
   * @param loginType 登入类型（手机登录，账号登录等）
   * @param version token 版本 （1，2，3 etc.）
   * @param tokenKey 获取token的token key
   * @param token token 的值
   * @return http code 200 succeed
   */
  @Override
  public int isLogin(String serviceType, String loginType, String version, String tokenKey,
      String token) {
    if (token == null) {
      return 401;
    }
    // 验证用户是否登录
    Object exist = valueOperations.get(token);
    if (exist != null) {
      return 200;
    } else {
      return 401;
    }
  }
}
