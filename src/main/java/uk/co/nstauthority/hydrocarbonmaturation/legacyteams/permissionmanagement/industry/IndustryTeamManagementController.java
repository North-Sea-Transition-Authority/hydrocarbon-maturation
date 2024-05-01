package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasTeamPermission;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.LegacyTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.LegacyTeamManagementService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/permission-management/industry")
@HasTeamPermission(
    anyTeamPermissionOf = {
        RolePermission.INDUSTRY_MANAGE_ORGANISATION,
        RolePermission.INDUSTRY_SUBMIT,
        RolePermission.VIEW_SYSTEM_OF_RECORD
    },
    anyNonTeamPermissionOf = RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS
)
public class IndustryTeamManagementController extends AbstractIndustryPermissionManagement {

  private final TeamMemberViewService teamMemberViewService;
  private final UserService userService;
  private final PermissionService permissionService;
  private final TeamService teamService;
  private final LegacyTeamManagementService legacyTeamManagementService;

  @Autowired
  IndustryTeamManagementController(TeamMemberViewService teamMemberViewService,
                                   IndustryTeamService industryTeamService,
                                   UserService userService,
                                   PermissionService permissionService,
                                   TeamService teamService, LegacyTeamManagementService legacyTeamManagementService) {
    super(industryTeamService);
    this.teamMemberViewService = teamMemberViewService;
    this.userService = userService;
    this.permissionService = permissionService;
    this.teamService = teamService;
    this.legacyTeamManagementService = legacyTeamManagementService;
  }

  @GetMapping("/{teamId}")
  public ModelAndView renderMemberList(@PathVariable("teamId") TeamId teamId) {
    var user = userService.getUser();

    var team = getIndustryTeam(teamId);
    var modelAndView = new ModelAndView("remi/permissionmanagement/teamMembers")
        .addObject("pageTitle", "Manage %s".formatted(team.getDisplayName()))
        .addObject("teamName", team.getDisplayName())
        .addObject("teamRoles", IndustryTeamRole.values())
        .addObject("teamMembers", teamMemberViewService.getTeamMemberViewsForTeam(team));

    var userIsIndustryAccessManagerInRegulatorTeam = legacyTeamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    if (teamService.isUserInMultipleTeams(user) || userIsIndustryAccessManagerInRegulatorTeam) {
      modelAndView
          .addObject("backLinkUrl",
              ReverseRouter.route(on(LegacyTeamManagementController.class).renderTeamListOrRedirect()));
    }

    if (permissionService.hasPermission(
        user,
        Set.of(RolePermission.INDUSTRY_MANAGE_ORGANISATION, RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS)
    )) {
      modelAndView
          .addObject("addTeamMemberUrl",
              ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId)))
          .addObject("canRemoveUsers", true)
          .addObject("canEditUsers", true);
    }
    return modelAndView;
  }
}