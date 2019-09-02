package org.gwallgroup.authentication.service;

import org.gwallgroup.authentication.entity.dto.TokenLoginDto;
import org.gwallgroup.common.utils.ResponseBase;

public interface GwallAuthenticationService {

  /**
   * 使用token 登录
   *
   * @param tokenLoginDto token dto
   * @return res
   */
  ResponseBase login(TokenLoginDto tokenLoginDto);

}
