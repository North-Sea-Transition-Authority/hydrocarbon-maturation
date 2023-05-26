package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamRole;

@ExtendWith(MockitoExtension.class)
class AreaTeamManagementControllerHelperServiceTest {

  @Mock
  private TeamMemberViewService teamMemberViewService;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private PermissionService permissionService;

  @Mock
  private TeamService teamService;

  @InjectMocks
  private AreaTeamManagementControllerHelperService areaTeamManagementControllerHelperService;

  private final Team team = new Team();
  private final WebUserAccountId wuaId = new WebUserAccountId(42L);
  private final TeamView teamView = new TeamView(new TeamId(UUID.randomUUID()), TeamType.REGULATOR, "team");
  private final TeamMemberView teamMemberView = new TeamMemberView(wuaId, teamView, "title", "a", "b", "email", "0", Set.of());
  private final String addTeamMemberUrl = "addTeamMemberUrl";
  private final ServiceUserDetail user = ServiceUserDetailTestUtil.Builder().build();
  private final TeamRole[] teamRoles = {NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM, OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM};

  @Test
  void renderMemberList_inOneTeam_notAccessManagerPermission() {
    team.setDisplayName("team");

    when(userDetailService.getUserDetail()).thenReturn(user);
    when(teamMemberViewService.getTeamMemberViewsForTeam(team)).thenReturn(List.of(teamMemberView));
    when(teamService.isUserInMultipleTeams(user)).thenReturn(false);
    when(permissionService.hasPermission(user, Set.of())).thenReturn(false);

    var modelAndView = areaTeamManagementControllerHelperService.renderMemberList(team, Set.of(),
        addTeamMemberUrl, teamRoles);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/teamMembers");
    assertThat(modelAndView.getModel()).containsEntry("pageTitle", "Manage %s".formatted(team.getDisplayName()));
    assertThat(modelAndView.getModel()).containsEntry("teamName", team.getDisplayName());
    assertThat(modelAndView.getModel()).containsEntry("teamRoles", teamRoles);
    assertThat(modelAndView.getModel()).containsEntry("teamMembers", List.of(teamMemberView));

    assertThat(modelAndView.getModel().keySet())
        .doesNotContain("backLinkUrl", "addTeamMemberUrl", "canRemoveUsers", "canEditUsers");
  }

  @Test
  void renderMemberList_inMultipleTeams_accessManagerPermission() {

    when(userDetailService.getUserDetail()).thenReturn(user);
    when(teamMemberViewService.getTeamMemberViewsForTeam(team)).thenReturn(List.of(teamMemberView));
    when(teamService.isUserInMultipleTeams(user)).thenReturn(true);
    when(permissionService.hasPermission(user, Set.of())).thenReturn(true);

    var modelAndView = areaTeamManagementControllerHelperService.renderMemberList(team, Set.of(),
        addTeamMemberUrl, teamRoles);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/teamMembers");
    assertThat(modelAndView.getModel()).containsEntry("backLinkUrl",
        ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()));
    assertThat(modelAndView.getModel()).containsEntry("addTeamMemberUrl", addTeamMemberUrl);
    assertThat(modelAndView.getModel()).containsEntry("canRemoveUsers", true);
    assertThat(modelAndView.getModel()).containsEntry("canEditUsers", true);
  }
}