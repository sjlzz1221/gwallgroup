package org.gwallgroup.authentication.service;

import org.gwallgroup.authentication.dto.TokenLoginDto;
import org.gwallgroup.common.utils.ResponseBase;

public interface AuthenticationService {

  /**
   * 使用token 登录
   * @param tokenLoginDto token dto
   * @return res
   */
  ResponseBase tokenLogin(TokenLoginDto tokenLoginDto);

}
