package org.gwallgroup.common.web.context;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gwallgroup.common.web.constants.Xheader;
import org.gwallgroup.common.web.utils.help.AttributeHelp;
import org.springframework.web.filter.OncePerRequestFilter;

public class GwallContextFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String user = AttributeHelp.getHeader(Xheader.X_MAN, request, null);
    if (user != null) {
      JSONObject cu = JSONObject.parseObject(user);
      GwallContextHolder.getContext()
          .setAuthentication(new ContextUser().setUser(cu).setId(cu.getLong("id")));
    }
    filterChain.doFilter(request, response);
  }
}
