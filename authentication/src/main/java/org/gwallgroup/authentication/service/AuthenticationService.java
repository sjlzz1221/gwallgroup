package org.gwallgroup.authentication.service;

import org.gwallgroup.authentication.entity.dto.TokenLoginDto;
import org.gwallgroup.common.entity.LoginCheck;
import org.gwallgroup.common.utils.ResponseBase;

public interface AuthenticationService {

  /**
   * 验证登录
   *
   * @param tokenKey token key
   * @param token token
   * @return check result
   */
  LoginCheck check(String tokenKey, String token);

  /**
   * 使用token 登录
   *
   * @param tokenLoginDto token dto
   * @return res
   */
  ResponseBase login(TokenLoginDto tokenLoginDto);
}
