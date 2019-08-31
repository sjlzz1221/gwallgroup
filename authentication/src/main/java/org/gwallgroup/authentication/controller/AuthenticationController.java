package org.gwallgroup.authentication.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.gwallgroup.authentication.entity.dto.TokenLoginDto;
import org.gwallgroup.authentication.service.AuthenticationService;
import org.gwallgroup.authentication.service.AuthenticationServiceManager;
import org.gwallgroup.common.constants.Xheader;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.web.utils.help.AttributeHelp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 * 通用口令登录
 * 验证登录
 * @author jsen
 */
@RestController
@RequestMapping("authentication")
@Slf4j
public class AuthenticationController {


  @PostMapping("token")
  public ResponseBase tokenLogin(HttpServletRequest request, TokenLoginDto tokenLoginDto) {
    String serviceType = AttributeHelp.getHeader(Xheader.X_ST, request, Xheader.DEFAULT);
    String loginType = AttributeHelp.getHeader(Xheader.X_LT, request, Xheader.DEFAULT);
    String version = AttributeHelp.getHeader(Xheader.X_V, request, Xheader.DEFAULT_VERSION);
    log.debug("{} {} {}", serviceType, loginType, version);
    AuthenticationService authenticationService = AuthenticationServiceManager.get(serviceType);
    return authenticationService.login(tokenLoginDto);
  }

}
