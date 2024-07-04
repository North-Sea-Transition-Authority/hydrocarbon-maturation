package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;

@ExtendWith(MockitoExtension.class)
class PostAuthenticationRequestMdcFilterTest {

  @Mock
  private UserService userService;

  @Mock
  private MockFilterChain filterChain;

  @InjectMocks
  private PostAuthenticationRequestMdcFilter postAuthenticationRequestMdcFilter;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    MDC.remove(RequestLogFilter.MDC_WUA_ID);
    MDC.remove(RequestLogFilter.MDC_PROXY_WUA_ID);
  }

  @Test
  void doFilterInternal_noUser() throws ServletException, IOException {
    when(userService.getOptionalUserDetail())
        .thenReturn(Optional.empty());

    postAuthenticationRequestMdcFilter.doFilterInternal(request, response, filterChain);

    assertThat(MDC.get(RequestLogFilter.MDC_WUA_ID)).isNull();
    assertThat(MDC.get(RequestLogFilter.MDC_PROXY_WUA_ID)).isNull();

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_userExistsNoProxy() throws ServletException, IOException {
    var userWithoutProxyWuaId = UserTestUtil.Builder()
        .withProxyWuaId(null)
        .build();

    when(userService.getOptionalUserDetail())
        .thenReturn(Optional.of(userWithoutProxyWuaId));

    postAuthenticationRequestMdcFilter.doFilterInternal(request, response, filterChain);

    assertThat(MDC.get(RequestLogFilter.MDC_WUA_ID))
        .isEqualTo(userWithoutProxyWuaId.wuaId().toString());
    assertThat(MDC.get(RequestLogFilter.MDC_PROXY_WUA_ID))
        .isNull();

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_userExistsWithProxy() throws ServletException, IOException {
    var userWithProxyWuaId = UserTestUtil.Builder().build();

    when(userService.getOptionalUserDetail())
        .thenReturn(Optional.of(userWithProxyWuaId));

    postAuthenticationRequestMdcFilter.doFilterInternal(request, response, filterChain);

    assertThat(MDC.get(RequestLogFilter.MDC_WUA_ID))
        .isEqualTo(userWithProxyWuaId.wuaId().toString());
    assertThat(MDC.get(RequestLogFilter.MDC_PROXY_WUA_ID))
        .isEqualTo(userWithProxyWuaId.proxyWuaId().toString());

    verify(filterChain).doFilter(request, response);
  }
}