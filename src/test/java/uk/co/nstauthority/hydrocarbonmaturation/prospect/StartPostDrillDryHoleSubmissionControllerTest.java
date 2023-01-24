package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.StartPostDrillDryHoleSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = StartPostDrillDryHoleSubmissionController.class)
class StartPostDrillDryHoleSubmissionControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private PermissionService permissionService;

  @SecurityTest
  void getStartPostDrillDryHolePage_assertStatusOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
        get(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage()))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void startPostDrillDryHole_assertRedirection() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
        post(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).startPostDrillDryHole()))
            .with(user(USER))
            .with(csrf()))
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl(ReverseRouter.route(on(PostDrillDryHoleController.class).getPostDrillDryHolePage(null))));
  }

  @Test
  void getStartPostDrillDryHolePage_assertModelProperties() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage()))
            .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();
    assertEquals(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).startPostDrillDryHole()), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()), model.get("backLinkUrl"));
  }

  @SecurityTest
  void getStartPostDrillDryHolePage_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startPostDrillDryHole_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).startPostDrillDryHole()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}