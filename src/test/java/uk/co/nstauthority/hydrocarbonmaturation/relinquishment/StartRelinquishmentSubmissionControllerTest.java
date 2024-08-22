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

import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = StartRelinquishmentSubmissionController.class)
class StartRelinquishmentSubmissionControllerTest extends AbstractSubmissionControllerTest {

  @SecurityTest
  void getStartProspectRelinquishmentPage_hasPermission_assertIsOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

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
            ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm(null)),
            ResourceType.PROSPECT);
  }

  @SecurityTest
  void getStartDiscoveryRelinquishmentPage_hasPermission_assertIsOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

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
            ReverseRouter.route(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm(null)),
            ResourceType.DISCOVERY);
  }

  @SecurityTest
  void startProspectRelinquishment_hasPermission_assertRedirect() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

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
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

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
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartProspectRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getStartDiscoveryRelinquishmentPage_hasNoPermissions_assertIsForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartDiscoveryRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startProspectRelinquishment_hasNoPermission_assertRedirect() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .startProspectRelinquishment()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startDiscoveryRelinquishment_hasNoPermission_assertRedirect() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .startDiscoveryRelinquishment()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}