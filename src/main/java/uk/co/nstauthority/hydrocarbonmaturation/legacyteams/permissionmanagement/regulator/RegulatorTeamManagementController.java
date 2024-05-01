package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsMemberOfTeam;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.LegacyTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.LegacyTeamManagementService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/permission-management/regulator")
@AccessibleByServiceUsers
public class RegulatorTeamManagementController extends AbstractRegulatorPermissionManagement {

  private final TeamMemberViewService teamMemberViewService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserService userService;
  private final CustomerConfigurationProperties customerConfigurationProperties;
  private final TeamMemberService teamMemberService;
  private final TeamService teamService;
  private final LegacyTeamManagementService legacyTeamManagementService;

  @Autowired
  RegulatorTeamManagementController(TeamMemberViewService teamMemberViewService,
                                    RegulatorTeamService regulatorTeamService,
                                    UserService userService,
                                    CustomerConfigurationProperties customerConfigurationProperties,
                                    TeamMemberService teamMemberService,
                                    TeamService teamService, LegacyTeamManagementService legacyTeamManagementService) {
    super(regulatorTeamService);
    this.teamMemberViewService = teamMemberViewService;
    this.regulatorTeamService = regulatorTeamService;
    this.userService = userService;
    this.customerConfigurationProperties = customerConfigurationProperties;
    this.teamMemberService = teamMemberService;
    this.teamService = teamService;
    this.legacyTeamManagementService = legacyTeamManagementService;
  }

  @GetMapping
  public ModelAndView renderMemberListRedirect() {

    var team = regulatorTeamService.getRegulatorTeamForUser(userService.getUser())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "User with ID %s is not a member of regulator team".formatted(userService.getUser())
        ));

    return ReverseRouter.redirect(on(RegulatorTeamManagementController.class)
        .renderMemberList(team.toTeamId()));
  }

  @GetMapping("/{teamId}")
  @IsMemberOfTeam
  public ModelAndView renderMemberList(@PathVariable("teamId") TeamId teamId) {

    var team = getRegulatorTeam(teamId);

    var user = userService.getUser();

    var modelAndView = new ModelAndView("remi/permissionmanagement/teamMembers")
        .addObject("pageTitle", "Manage %s".formatted(customerConfigurationProperties.mnemonic()))
        .addObject("teamName", customerConfigurationProperties.mnemonic())
        .addObject("teamRoles", RegulatorTeamRole.values())
        .addObject("teamMembers", teamMemberViewService.getTeamMemberViewsForTeam(team));

    var userIsIndustryAccessManagerInRegulatorTeam = legacyTeamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    if (teamService.isUserInMultipleTeams(user) || userIsIndustryAccessManagerInRegulatorTeam) {
      modelAndView
          .addObject("backLinkUrl",
              ReverseRouter.route(on(LegacyTeamManagementController.class).renderTeamListOrRedirect()));
    }

    if (regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.ACCESS_MANAGER)) {
      modelAndView
          .addObject("addTeamMemberUrl",
              ReverseRouter.route(on(RegulatorAddMemberController.class).renderAddTeamMember(teamId)))
          .addObject("canRemoveUsers", teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user,
              Set.of(RegulatorTeamRole.ACCESS_MANAGER.name())))
          .addObject("canEditUsers", teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user,
              Set.of(RegulatorTeamRole.ACCESS_MANAGER.name())));
    }

    return modelAndView;
  }

}