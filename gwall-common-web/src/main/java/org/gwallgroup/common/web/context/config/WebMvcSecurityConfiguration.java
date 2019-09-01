package org.gwallgroup.common.web.context.config;

import java.util.List;
import org.gwallgroup.common.web.context.GwallContextFilter;
import org.gwallgroup.common.web.context.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcSecurityConfiguration implements WebMvcConfigurer {

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    AuthenticationPrincipalArgumentResolver authenticationPrincipalResolver =
        new AuthenticationPrincipalArgumentResolver();
    argumentResolvers.add(authenticationPrincipalResolver);
  }

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean bean = new FilterRegistrationBean();
    bean.setFilter(new GwallContextFilter());
    bean.addUrlPatterns("/*");
    return bean;
  }
}
