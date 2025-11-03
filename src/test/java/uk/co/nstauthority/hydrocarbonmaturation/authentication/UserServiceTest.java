package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import uk.co.nstauthority.hydrocarbonmaturation.audit.AuditRevisionUtil;

class UserServiceTest {

  private static UserService userService;

  @BeforeAll
  static void setUp() {
    userService = new UserService();
  }

  @BeforeEach
  void setUpBeforeEach() {
    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
  }

  @Test
  void getUser_whenUserInContext_thenExpectedUserReturned() {

    var user = UserTestUtil.Builder()
        .withWuaId(100L)
        .build();

    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    assertThat(userService.getUser())
        .extracting(User::wuaId)
        .isEqualTo(user.wuaId());
  }

  @Test
  void getUser_whenNoPrincipal_thenException() {

    SecurityContextHolder.setContext(new SecurityContextImpl(new ServiceSaml2Authentication(null, Set.of())));

    assertThrowsExactly(InvalidAuthenticationException.class, () -> userService.getUser(),
        "User not found in ServiceSaml2Authentication principal");
  }

  @Test
  void getUser_whenNoAuthenticationInContext_thenException() {
    assertThrowsExactly(InvalidAuthenticationException.class, () -> userService.getUser(),
        "ServiceSaml2Authentication not found in authentication context");
  }

  @Test
  void newRevision_whenAuthenticatedUserPresent() {
    var user = UserTestUtil.Builder().build();
    SecurityContextHolder.setContext(new SecurityContextImpl((new ServiceSaml2Authentication(user, Set.of()))));

    assertThat(userService.getOptionalUserDetail()).containsSame(user);
  }

  @Test
  void getOptionalUserDetail_whenNoAuthenticatedUser_thenUseFallbackUser() {
    SecurityContextHolder.setContext(new SecurityContextImpl(null));

    var user = UserTestUtil.Builder().build();

    try (MockedStatic<AuditRevisionUtil> mockedUtil = Mockito.mockStatic(AuditRevisionUtil.class)) {
      mockedUtil.when(AuditRevisionUtil::getFallbackAuditUser)
          .thenReturn(user);

      assertThat(userService.getOptionalUserDetail()).containsSame(user);
    }
  }

  @Test
  void getUserWuaId_whenUserInContext_thenExpectedIdReturned() {
    var user = UserTestUtil.Builder()
        .withWuaId(50L)
        .build();

    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    assertThat(userService.getUserWuaId())
        .isEqualTo(user.wuaId().intValue());
  }
}
