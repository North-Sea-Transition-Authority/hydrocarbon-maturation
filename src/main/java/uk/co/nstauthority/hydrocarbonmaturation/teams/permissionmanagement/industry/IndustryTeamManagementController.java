package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementService;


@Controller
@RequestMapping("/permission-management/industry")
@AccessibleByServiceUsers
public class IndustryTeamManagementController extends AbstractIndustryPermissionManagement {

  private final ControllerHelperService controllerHelperService;
  private final TeamMemberViewService teamMemberViewService;
  private final IndustryTeamService industryTeamService;
  private final UserDetailService userDetailService;
  private final NewTeamFormValidator newTeamFormValidator;
  private final OrganisationGroupQueryService organisationGroupService;
  private final PermissionService permissionService;
  private final TeamService teamService;
  private final TeamManagementService teamManagementService;

  @Autowired
  IndustryTeamManagementController(ControllerHelperService controllerHelperService,
                                   TeamMemberViewService teamMemberViewService,
                                   IndustryTeamService industryTeamService,
                                   UserDetailService userDetailService,
                                   NewTeamFormValidator newTeamFormValidator,
                                   OrganisationGroupQueryService organisationGroupService,
                                   PermissionService permissionService, TeamService teamService,
                                   TeamManagementService teamManagementService) {
    super(industryTeamService);
    this.controllerHelperService = controllerHelperService;
    this.teamMemberViewService = teamMemberViewService;
    this.industryTeamService = industryTeamService;
    this.userDetailService = userDetailService;
    this.newTeamFormValidator = newTeamFormValidator;
    this.organisationGroupService = organisationGroupService;
    this.permissionService = permissionService;
    this.teamService = teamService;
    this.teamManagementService = teamManagementService;
  }

  @GetMapping("/new-team")
  public ModelAndView renderNewIndustryTeamForm(@ModelAttribute("form") NewTeamForm form) {
    var user = userDetailService.getUserDetail();

    if (!teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with ID %s is not an industry access manager".formatted(user)
      );
    }

    var organisationGroupSearchRestUrl =
        ReverseRouter.route(on(OrganisationGroupRestController.class).getOrganisationGroupSearchResults(null));
    return new ModelAndView("remi/permissionmanagement/addTeam")
        .addObject("pageTitle", "Add new industry team")
        .addObject("organisationGroupSearchRestUrl", organisationGroupSearchRestUrl)
        .addObject("submitFormUrl", ReverseRouter.route(on(IndustryTeamManagementController.class)
            .addNewIndustryTeam(null, null, null)))
        .addObject(
            "backLinkUrl",
            ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect())
        );
  }

  @PostMapping("/new-team")
  public ModelAndView addNewIndustryTeam(@ModelAttribute("form") NewTeamForm form,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
    var user = userDetailService.getUserDetail();

    if (!teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with ID %s is not an industry access manager".formatted(user)
      );
    }

    newTeamFormValidator.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        renderNewIndustryTeamForm(form),
        () -> {
          var groupId = Integer.parseInt(form.getOrganisationGroupId());
          var orgGroup =
                organisationGroupService.getOrganisationGroupById(groupId)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No organisation group with id %d could not be found".formatted(groupId)
                    ));

          var groupName = orgGroup.getOrganisationGroupName();
          var team = industryTeamService.getTeamByOrganisationGroupId(groupId)
              .orElseGet(() -> {
                var t = industryTeamService.createTeam(
                    groupName,
                    groupId,
                    user.wuaId()
                );

                NotificationBannerUtil.addSuccessNotification(redirectAttributes,
                    "Added new organisation group team",
                    "Team for organisation group %s has been created".formatted(groupName));

                return t;
              });

          return ReverseRouter.redirect(on(IndustryTeamManagementController.class)
              .renderMemberList(team.toTeamId()));
        });
  }

  @GetMapping("/{teamId}")
  public ModelAndView renderMemberList(@PathVariable("teamId") TeamId teamId) {
    var user = userDetailService.getUserDetail();

    if (!teamManagementService.isMemberOfTeamOrIndustryAccessManager(teamId, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with ID %s is not a member of the team %s nor an industry access manager".formatted(teamId.uuid(), user)
      );
    }

    var team = getIndustryTeam(teamId);
    var modelAndView = new ModelAndView("remi/permissionmanagement/teamMembers")
        .addObject("pageTitle", "Manage %s".formatted(team.getDisplayName()))
        .addObject("teamName", team.getDisplayName())
        .addObject("teamRoles", IndustryTeamRole.values())
        .addObject("teamMembers", teamMemberViewService.getTeamMemberViewsForTeam(team));

    if (teamService.isUserInMultipleTeams(user)) {
      modelAndView
          .addObject("backLinkUrl",
              ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()));
    }

    if (permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))) {
      modelAndView
          .addObject("addTeamMemberUrl",
              ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId)))
          .addObject("canRemoveUsers", true)
          .addObject("canEditUsers", true);
    }
    return modelAndView;
  }
}