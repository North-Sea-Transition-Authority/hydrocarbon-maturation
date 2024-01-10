package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamManagementControllerHelperService;

@ContextConfiguration(classes = NorthernOffshoreAreaTeamManagementController.class)
class NorthernOffshoreAreaTeamManagementControllerTest extends AbstractControllerTest {

  @MockBean
  private NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;

  @MockBean
  private AreaTeamManagementControllerHelperService areaTeamManagementControllerHelperService;

  @SecurityTest
  void renderMemberList_whenNotAuthenticated_thenUnauthorised() throws Exception {
    var teamId = new TeamId(UUID.randomUUID());
    mockMvc.perform(
            get(ReverseRouter.route(on(NorthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void renderMemberList_whenNotMemberOfTeam_thenForbidden() throws Exception {
    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(NorthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = NorthernOffshoreAreaTeamRole.class)
  void renderMemberList_whenMemberOfTeam_thenOk(
      NorthernOffshoreAreaTeamRole northernOffshoreAreaTeamRole) throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(northernOffshoreAreaTeamRole)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(northernOffshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.of(team));


    var modelAndViewOk = new ModelAndView();
    modelAndViewOk.setStatus(HttpStatus.OK);
    when(areaTeamManagementControllerHelperService.renderMemberList(team,
        Set.of(RolePermission.GRANT_NORTHERN_OFFSHORE_AREA_ROLES),
        ReverseRouter.route(on(NorthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
        NorthernOffshoreAreaTeamRole.values()))
        .thenReturn(modelAndViewOk);

    mockMvc.perform(
            get(ReverseRouter.route(on(NorthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void renderMemberList_whenNoTeamFound_thenNotFound() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(northernOffshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(NorthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }
}