package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;

@Service
public class AreaTeamManagementControllerHelperService {

  private final TeamMemberViewService teamMemberViewService;
  private final UserService userService;
  private final PermissionService permissionService;
  private final TeamService teamService;

  @Autowired
  public AreaTeamManagementControllerHelperService(TeamMemberViewService teamMemberViewService,
                                                   UserService userService,
                                                   PermissionService permissionService, TeamService teamService) {
    this.teamMemberViewService = teamMemberViewService;
    this.userService = userService;
    this.permissionService = permissionService;
    this.teamService = teamService;
  }

  public ModelAndView renderMemberList(Team team, Set<RolePermission> managerRolePermissions, String addTeamMemberUrl,
                                       TeamRole[] teamRoles) {
    var user = userService.getUser();

    var modelAndView = new ModelAndView("remi/permissionmanagement/teamMembers")
        .addObject("pageTitle", "Manage %s".formatted(team.getDisplayName()))
        .addObject("teamName", team.getDisplayName())
        .addObject("teamRoles", teamRoles)
        .addObject("teamMembers", teamMemberViewService.getTeamMemberViewsForTeam(team));

    if (teamService.isUserInMultipleTeams(user)) {
      modelAndView
          .addObject("backLinkUrl",
              ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()));
    }

    if (permissionService.hasPermission(user, managerRolePermissions)) {
      modelAndView
          .addObject("addTeamMemberUrl", addTeamMemberUrl)
          .addObject("canRemoveUsers", true)
          .addObject("canEditUsers", true);
    }
    return modelAndView;
  }
}
