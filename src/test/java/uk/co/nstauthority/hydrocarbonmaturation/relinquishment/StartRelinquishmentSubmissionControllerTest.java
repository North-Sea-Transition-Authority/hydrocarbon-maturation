package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectPurposeController;

@ContextConfiguration(classes = StartRelinquishmentSubmissionController.class)
class StartRelinquishmentSubmissionControllerTest extends AbstractSubmissionControllerTest {

  @SecurityTest
  void getStartProspectRelinquishmentPage_hasPermission_assertIsOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartProspectRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/relinquishment/startRelinquishment"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView.getModel())
        .extracting(
            "actionUrl",
            "backLinkUrl",
            "resourceType")
        .containsExactly(
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).startProspectRelinquishment()),
            ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm()),
            ResourceType.PROSPECT);
  }

  @SecurityTest
  void getStartDiscoveryRelinquishmentPage_hasPermission_assertIsOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartDiscoveryRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/relinquishment/startRelinquishment"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView.getModel())
        .extracting(
            "actionUrl",
            "backLinkUrl",
            "resourceType")
        .containsExactly(
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).startDiscoveryRelinquishment()),
            ReverseRouter.route(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm()),
            ResourceType.DISCOVERY);
  }

  @SecurityTest
  void startProspectRelinquishment_hasPermission_assertRedirect() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .startProspectRelinquishment()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(NewProspectRelinquishmentController.class).getNewProspectRelinquishmentPage())));
  }

  @SecurityTest
  void startDiscoveryRelinquishment_hasPermission_assertRedirect() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .startDiscoveryRelinquishment()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class).getNewDiscoveryRelinquishmentPage())));
  }

  @SecurityTest
  void getStartProspectRelinquishmentPage_hasNoPermissions_assertIsForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartProspectRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getStartDiscoveryRelinquishmentPage_hasNoPermissions_assertIsForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartDiscoveryRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startProspectRelinquishment_hasNoPermission_assertRedirect() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .startProspectRelinquishment()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startDiscoveryRelinquishment_hasNoPermission_assertRedirect() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .startDiscoveryRelinquishment()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}