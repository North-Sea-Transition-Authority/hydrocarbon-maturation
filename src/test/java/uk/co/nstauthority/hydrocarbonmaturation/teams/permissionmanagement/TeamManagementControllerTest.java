package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ContextConfiguration(classes = TeamManagementController.class)
class TeamManagementControllerTest extends AbstractControllerTest {
  @MockBean
  private IndustryTeamService industryTeamService;
  @MockBean
  private RegulatorTeamService regulatorTeamService;

  @Test
  void renderTeamTypesOrRedirect_UserIsInRegulatorTeamAndIndustryTeam_thenOK() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(UUID.randomUUID())
        .build();
    var team2 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.of(team1));
    when(industryTeamService.getIndustryTeamsForUser(user)).thenReturn(List.of(TeamView.fromTeam(team2)));

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypesOrRedirect()))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @Test
  void renderTeamTypesOrRedirect_UserIsInRegulatorAndIsIndustryAccessManager_thenOK() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(UUID.randomUUID())
        .build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.of(team1));
    when(regulatorTeamService.isIndustryAccessManager(TeamId.valueOf(team1.getUuid()), user)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypesOrRedirect()))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @Test
  void renderTeamTypesOrRedirect_UserIsInRegulatorButIsNotIndustryAccessManager_thenRedirect() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(UUID.randomUUID())
        .build();
    var teamId = TeamId.valueOf(team1.getUuid());
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.of(team1));
    when(regulatorTeamService.isIndustryAccessManager(teamId, user)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypesOrRedirect()))
                .with(user(user)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(RegulatorTeamManagementController.class)
            .renderMemberList(teamId))));
  }

  @Test
  void renderTeamTypesOrRedirect_UserIsInAnIndustryTeam_thenRedirect() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();
    when(industryTeamService.getIndustryTeamsForUser(user)).thenReturn(List.of(TeamView.fromTeam(team1)));

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypesOrRedirect()))
                .with(user(user)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(IndustryTeamManagementController.class)
            .renderTeamList())));
  }

  @SecurityTest
  void renderTeamTypesOrRedirect_UserIsInAnIndustryTeam_thenForbidden() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(industryTeamService.getIndustryTeamsForUser(user)).thenReturn(List.of());
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypesOrRedirect()))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void renderTeamTypesOrRedirect_NotAuthenticated_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypesOrRedirect())))
        .andExpect(status().isUnauthorized());
  }
}