package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Set;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = StartPostDrillDiscoveryController.class)
class StartPostDrillDiscoveryControllerTest extends AbstractControllerTest {
  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private PermissionService permissionService;

  @SecurityTest
  void getStartPostDrillDiscoveryPage() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage()))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "actionUrl",
            "backLinkUrl")
        .containsExactly(
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).startPostDrillDiscovery()),
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @SecurityTest
  void startPostDrillDiscovery() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(StartPostDrillDiscoveryController.class).startPostDrillDiscovery()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(PostDrillDiscoveryController.class).getStartDiscoveryPage(null))));
  }

  @SecurityTest
  void getStartPostDrillDiscoveryPage_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startPostDrillDiscovery_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(StartPostDrillDiscoveryController.class).startPostDrillDiscovery()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}