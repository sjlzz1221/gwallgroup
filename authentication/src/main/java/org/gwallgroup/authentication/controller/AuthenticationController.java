package org.gwallgroup.authentication.controller;

import javax.annotation.Resource;
import org.gwallgroup.authentication.dto.TokenLoginDto;
import org.gwallgroup.authentication.service.AuthenticationService;
import org.gwallgroup.common.utils.ResponseBase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 * 通用口令登录
 * 验证登录
 */
@RestController
@RequestMapping("authentication")
public class AuthenticationController {

  @Resource
  private AuthenticationService authenticationService;

  @PostMapping("token")
  public ResponseBase tokenLogin(TokenLoginDto tokenLoginDto) {
    return authenticationService.tokenLogin(tokenLoginDto);
  }

}
