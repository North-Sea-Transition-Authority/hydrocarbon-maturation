package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.LegacyTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore.OnshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ExtendWith(MockitoExtension.class)
class AreaLegacyLegacyTeamManagementControllerHelperServiceTest {

  @Mock
  private TeamMemberViewService teamMemberViewService;

  @Mock
  private UserService userService;

  @Mock
  private PermissionService permissionService;

  @Mock
  private TeamService teamService;

  @InjectMocks
  private AreaTeamManagementControllerHelperService areaTeamManagementControllerHelperService;

  private final LegacyTeam legacyTeam = new LegacyTeam();
  private final WebUserAccountId wuaId = new WebUserAccountId(42L);
  private final TeamView teamView = new TeamView(new TeamId(UUID.randomUUID()), TeamType.REGULATOR, "team");
  private final TeamMemberView teamMemberView = new TeamMemberView(wuaId, teamView, "title", "a", "b", "email", "0", Set.of());
  private final String addTeamMemberUrl = "addTeamMemberUrl";
  private final User user = UserTestUtil.Builder().build();
  private final TeamRole[] teamRoles = {NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM, OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM};

  @Test
  void renderMemberList_inOneTeam_notAccessManagerPermission() {
    legacyTeam.setDisplayName("team");

    when(userService.getUser()).thenReturn(user);
    when(teamMemberViewService.getTeamMemberViewsForTeam(legacyTeam)).thenReturn(List.of(teamMemberView));
    when(teamService.isUserInMultipleTeams(user)).thenReturn(false);
    when(permissionService.hasPermission(user, Set.of())).thenReturn(false);

    var modelAndView = areaTeamManagementControllerHelperService.renderMemberList(legacyTeam, Set.of(),
        addTeamMemberUrl, teamRoles);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/teamMembers");
    assertThat(modelAndView.getModel()).containsEntry("pageTitle", "Manage %s".formatted(legacyTeam.getDisplayName()));
    assertThat(modelAndView.getModel()).containsEntry("teamName", legacyTeam.getDisplayName());
    assertThat(modelAndView.getModel()).containsEntry("teamRoles", teamRoles);
    assertThat(modelAndView.getModel()).containsEntry("teamMembers", List.of(teamMemberView));

    assertThat(modelAndView.getModel().keySet())
        .doesNotContain("backLinkUrl", "addTeamMemberUrl", "canRemoveUsers", "canEditUsers");
  }

  @Test
  void renderMemberList_inMultipleTeams_accessManagerPermission() {

    when(userService.getUser()).thenReturn(user);
    when(teamMemberViewService.getTeamMemberViewsForTeam(legacyTeam)).thenReturn(List.of(teamMemberView));
    when(teamService.isUserInMultipleTeams(user)).thenReturn(true);
    when(permissionService.hasPermission(user, Set.of())).thenReturn(true);

    var modelAndView = areaTeamManagementControllerHelperService.renderMemberList(legacyTeam, Set.of(),
        addTeamMemberUrl, teamRoles);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/teamMembers");
    assertThat(modelAndView.getModel()).containsEntry("backLinkUrl",
        ReverseRouter.route(on(LegacyTeamManagementController.class).renderTeamListOrRedirect()));
    assertThat(modelAndView.getModel()).containsEntry("addTeamMemberUrl", addTeamMemberUrl);
    assertThat(modelAndView.getModel()).containsEntry("canRemoveUsers", true);
    assertThat(modelAndView.getModel()).containsEntry("canEditUsers", true);
  }
}