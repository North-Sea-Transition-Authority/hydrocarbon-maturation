package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.NewIndustryTeamController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamManagementController;

@ContextConfiguration(classes = TeamManagementController.class)
class TeamManagementControllerTest extends AbstractControllerTest {

  @MockBean
  private TeamManagementService teamManagementService;

  @SecurityTest
  void renderTeamListOrRedirect_whenNotAuthenticated_thenUnauthorised() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect())))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void renderTeamListOrRedirect_whenNoAccess_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    when(teamManagementService.getTeamsThatUserHasAccessTo(user)).thenReturn(Collections.emptyList());

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderTeamListOrRedirect_whenAccessToIndustryTeam_thenOK_assertProperties() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    var team2 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    var teamViewList = List.of(TeamView.fromTeam(team1), TeamView.fromTeam(team2));

    when(teamManagementService.getTeamsThatUserHasAccessTo(user)).thenReturn(teamViewList);
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/teamList"))
        .andExpect(model().attribute("pageTitle", "Choose team to manage"))
        .andExpect(model().attribute("teamGroupMap", Map.of(TeamType.INDUSTRY, teamViewList)));
  }

  @Test
  void renderTeamListOrRedirect_whenAccessToSingleIndustryTeam_thenRedirect() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    when(teamManagementService.getTeamsThatUserHasAccessTo(user)).thenReturn(List.of(TeamView.fromTeam(team1)));
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(false);

    mockMvc
        .perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()))
                .with(user(user)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(IndustryTeamManagementController.class)
            .renderMemberList(team1.toTeamId()))));
  }

  @Test
  void renderTeamListOrRedirect_whenAccessToRegulatorTeam_thenRedirect() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    when(teamManagementService.getTeamsThatUserHasAccessTo(user)).thenReturn(List.of(TeamView.fromTeam(team1)));
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(false);

    mockMvc
        .perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()))
                .with(user(user)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(RegulatorTeamManagementController.class)
            .renderMemberList(team1.toTeamId()))));
  }

  @Test
  void renderTeamListOrRedirect_whenAccessToSingleIndustryTeam_andIsIndustryAccessManagerInRegularTeam_thenOk() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    when(teamManagementService.getTeamsThatUserHasAccessTo(user)).thenReturn(List.of(TeamView.fromTeam(team1)));
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/teamList"))
        .andExpect(model().attribute("pageTitle", "Choose team to manage"))
        .andExpect(model().attribute("teamGroupMap", Map.of(TeamType.INDUSTRY, List.of(TeamView.fromTeam(team1)))))
        .andExpect(model().attribute("newIndustryTeamFormUrl",
            ReverseRouter.route(on(NewIndustryTeamController.class).renderNewIndustryTeamForm(null))));
  }

  @Test
  void renderTeamListOrRedirect_whenRegulatorAndIndustryAccessManager_thenOk() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    var team2 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    when(teamManagementService.getTeamsThatUserHasAccessTo(user)).thenReturn(List.of(TeamView.fromTeam(team1), TeamView.fromTeam(team2)));

    mockMvc.perform(
            get(ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()))
                .with(user(user)))
        .andExpect(status().isOk());
  }
}