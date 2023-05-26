package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamManagementControllerHelperService;

@ContextConfiguration(classes = OnshoreAreaTeamManagementController.class)
class OnshoreAreaTeamManagementControllerTest extends AbstractControllerTest {

  @MockBean
  private OnshoreAreaTeamService onshoreAreaTeamService;

  @MockBean
  private AreaTeamManagementControllerHelperService areaTeamManagementControllerHelperService;

  @SecurityTest
  void renderMemberList_whenNotAuthenticated_thenUnauthorised() throws Exception {
    var teamId = new TeamId(UUID.randomUUID());
    mockMvc.perform(
            get(ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void renderMemberList_whenNotMemberOfTeam_thenForbidden() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = OnshoreAreaTeamRole.class)
  void renderMemberList_whenMemberOfTeam_thenOk(
      OnshoreAreaTeamRole onshoreAreaTeamRole) throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(onshoreAreaTeamRole)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(onshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var modelAndViewOk = new ModelAndView();
    modelAndViewOk.setStatus(HttpStatus.OK);
    when(areaTeamManagementControllerHelperService.renderMemberList(team,
        Set.of(RolePermission.GRANT_ONSHORE_AREA_ROLES),
        ReverseRouter.route(on(OnshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
        OnshoreAreaTeamRole.values()))
        .thenReturn(modelAndViewOk);


    mockMvc.perform(
            get(ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void renderMemberList_whenNoTeamFound_thenNotFound() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(onshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }
}