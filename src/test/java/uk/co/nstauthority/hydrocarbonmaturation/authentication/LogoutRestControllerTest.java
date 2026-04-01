package uk.co.nstauthority.hydrocarbonmaturation.authentication;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.EnergyPortalConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = LogoutRestController.class)
class LogoutRestControllerTest extends RealSecurityAbstractControllerTest {

  private static final Class<LogoutRestController> CONTROLLER = LogoutRestController.class;
  private static final String UNAUTHORIZED_KEY = "Bearer INVALID_KEY";
  private static final Long WUA_ID = 1L;

  @Autowired
  private EnergyPortalConfiguration energyPortalConfiguration;

  @MockitoBean
  private LogoutService logoutService;

  @SecurityTest
  void logoutService() throws Exception {
    mockMvc
        .perform(post(ReverseRouter.route(on(CONTROLLER).logoutOfService(null, WUA_ID)))
            .header("Authorization", "Bearer " + energyPortalConfiguration.portalLogoutPreSharedKey()))
        .andExpect(status().isOk());
    verify(logoutService).logoutUser(WUA_ID);
  }

  @SecurityTest
  void logoutService_unauthorized() throws Exception {
    mockMvc
        .perform(post(ReverseRouter.route(on(CONTROLLER).logoutOfService(null, WUA_ID)))
            .header("Authorization", UNAUTHORIZED_KEY))
        .andExpect(status().isForbidden());
    verify(logoutService, never()).logoutUser(any());
  }

  @ParameterizedTest
  @ValueSource(strings = {"INVALID_KEY", "test"})
  void logoutService_invalidKey(String preSharedKey) throws Exception {
    mockMvc
        .perform(post(ReverseRouter.route(on(CONTROLLER).logoutOfService(null, WUA_ID)))
            .header("Authorization", preSharedKey))
        .andExpect(status().isForbidden());
    verify(logoutService, never()).logoutUser(any());
  }
}