package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasTeamPermission;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementService;

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
  private final UserDetailService userDetailService;
  private final PermissionService permissionService;
  private final TeamService teamService;
  private final TeamManagementService teamManagementService;

  @Autowired
  IndustryTeamManagementController(TeamMemberViewService teamMemberViewService,
                                   IndustryTeamService industryTeamService,
                                   UserDetailService userDetailService,
                                   PermissionService permissionService,
                                   TeamService teamService, TeamManagementService teamManagementService) {
    super(industryTeamService);
    this.teamMemberViewService = teamMemberViewService;
    this.userDetailService = userDetailService;
    this.permissionService = permissionService;
    this.teamService = teamService;
    this.teamManagementService = teamManagementService;
  }

  @GetMapping("/{teamId}")
  public ModelAndView renderMemberList(@PathVariable("teamId") TeamId teamId) {
    var user = userDetailService.getUserDetail();

    var team = getIndustryTeam(teamId);
    var modelAndView = new ModelAndView("remi/permissionmanagement/teamMembers")
        .addObject("pageTitle", "Manage %s".formatted(team.getDisplayName()))
        .addObject("teamName", team.getDisplayName())
        .addObject("teamRoles", IndustryTeamRole.values())
        .addObject("teamMembers", teamMemberViewService.getTeamMemberViewsForTeam(team));

    var userIsIndustryAccessManagerInRegulatorTeam = teamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    if (teamService.isUserInMultipleTeams(user) || userIsIndustryAccessManagerInRegulatorTeam) {
      modelAndView
          .addObject("backLinkUrl",
              ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()));
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