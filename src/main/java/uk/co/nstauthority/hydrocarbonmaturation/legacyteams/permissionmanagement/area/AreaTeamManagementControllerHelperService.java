package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.LegacyTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

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

  public ModelAndView renderMemberList(LegacyTeam legacyTeam, Set<RolePermission> managerRolePermissions, String addTeamMemberUrl,
                                       TeamRole[] teamRoles) {
    var user = userService.getUser();

    var modelAndView = new ModelAndView("remi/permissionmanagement/teamMembers")
        .addObject("pageTitle", "Manage %s".formatted(legacyTeam.getDisplayName()))
        .addObject("teamName", legacyTeam.getDisplayName())
        .addObject("teamRoles", teamRoles)
        .addObject("teamMembers", teamMemberViewService.getTeamMemberViewsForTeam(legacyTeam));

    if (teamService.isUserInMultipleTeams(user)) {
      modelAndView
          .addObject("backLinkUrl",
              ReverseRouter.route(on(LegacyTeamManagementController.class).renderTeamListOrRedirect()));
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
