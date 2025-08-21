package uk.co.nstauthority.hydrocarbonmaturation.configuration;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.configuration.WebSecurityConfiguration.SERVICE_ACCESS_PRIVILEDGE;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.EnergyPortalConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.error.DefaultErrorController;

@AutoConfigureMockMvc
@IntegrationTest
@EnableConfigurationProperties(value = {EnergyPortalConfiguration.class})
class WebSecurityConfigurationTest {

  private static final String CONTEXT_PATH = "/test";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EnergyPortalConfiguration energyPortalConfiguration;

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("server.servlet.context-path", () -> CONTEXT_PATH);
  }

  @Test
  void security_whenUserHasAccessAuthority_thenOk() throws Exception {

    var idpAccessGrantedAuthority = new SimpleGrantedAuthority(SERVICE_ACCESS_PRIVILEDGE);

    var user = UserTestUtil.Builder().build();

    mockMvc.perform(get(ReverseRouter.route(on(WebSecurityConfigurationTestController.class).testEndpoint()))
            .with(user(user, Set.of(idpAccessGrantedAuthority)))
        )
        .andExpect(status().isOk());
  }

  @Test
  void security_whenUserDoesntHaveAccessAuthority_thenForbidden() throws Exception {

    Set<GrantedAuthority> emptyGrantedAuthorityCollection = Set.of();

    var user = UserTestUtil.Builder().build();

    // check for a single path endpoint, e.g. /endpoint
    mockMvc.perform(get(ReverseRouter.route(on(WebSecurityConfigurationTestController.class).testEndpoint()))
            .with(user(user, emptyGrantedAuthorityCollection)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            CONTEXT_PATH + ReverseRouter.route(on(DefaultErrorController.class).getUnauthorisedErrorPage())
        ));

    // check for a multi path endpoint e.g. /first/second
    mockMvc.perform(get(ReverseRouter.route(on(WebSecurityConfigurationTestController.class).testEndpointWithMultiUrlPath()))
            .with(user(user, emptyGrantedAuthorityCollection))
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            CONTEXT_PATH + ReverseRouter.route(on(DefaultErrorController.class).getUnauthorisedErrorPage())
        ));
  }

  @Test
  void security_whenLogout_thenEnergyPortalLogoutRedirectionFollowed() throws Exception {

    var idpAccessGrantedAuthority = new SimpleGrantedAuthority(SERVICE_ACCESS_PRIVILEDGE);

    var user = UserTestUtil.Builder().build();

    mockMvc.perform(post("/logout")
            .with(user(user, Set.of(idpAccessGrantedAuthority)))
            .with(csrf())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(energyPortalConfiguration.logoutUrl()))
        .andExpect(unauthenticated());
  }
}
