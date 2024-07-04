package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;

@Component
public class PostAuthenticationRequestMdcFilter extends OncePerRequestFilter {
  private final UserService userService;

  PostAuthenticationRequestMdcFilter(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    userService.getOptionalUserDetail()
        .ifPresent(this::setWuaIdAndProxyWuaId);

    filterChain.doFilter(request, response);
  }

  private void setWuaIdAndProxyWuaId(User user) {
    MDC.put(RequestLogFilter.MDC_WUA_ID, user.wuaId().toString());

    var proxyWuaId = user.proxyWuaId();
    if (proxyWuaId != null) {
      MDC.put(RequestLogFilter.MDC_PROXY_WUA_ID, proxyWuaId.toString());
    }
  }
}
