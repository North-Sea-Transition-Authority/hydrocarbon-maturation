package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
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

@ContextConfiguration(classes = StartRelinquishmentSubmissionController.class)
class StartRelinquishmentSubmissionControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private PermissionService permissionService;

  @SecurityTest
  void getStartRelinquishmentPage_hasPermission_assertIsOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/relinquishment/startRelinquishment"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView.getModel())
        .extracting(
            "actionUrl",
            "backLinkUrl")
        .containsExactly(
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).startRelinquishment()),
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @SecurityTest
  void startRelinquishment_hasPermission_assertRedirect() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .startRelinquishment()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(NewRelinquishmentController.class).getNewRelinquishmentPage())));
  }

  @SecurityTest
  void getStartRelinquishmentPage_hasNoExplorationPermission_assertIsForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());

  }

  @SecurityTest
  void startRelinquishment_hasNoExplorationPermission_assertRedirect() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .startRelinquishment()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}