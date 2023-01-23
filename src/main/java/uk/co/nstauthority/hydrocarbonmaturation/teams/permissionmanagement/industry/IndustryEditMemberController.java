package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberEditRolesValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberRolesForm;

@Controller
@RequestMapping("/permission-management/industry/{teamId}/edit/{wuaId}")
@HasPermission(permissions = RolePermission.MANAGE_ORGANISATIONS)
public class IndustryEditMemberController extends AbstractIndustryPermissionManagement {
  private final TeamMemberService teamMemberService;
  private final TeamMemberViewService teamMemberViewService;
  private final TeamMemberRoleService teamMemberRoleService;
  private final ControllerHelperService controllerHelperService;
  private final IndustryTeamMemberEditRolesValidator industryTeamMemberEditRolesValidator;

  @Autowired
  IndustryEditMemberController(
      TeamMemberService teamMemberService,
      TeamMemberViewService teamMemberViewService,
      TeamMemberRoleService teamMemberRoleService,
      ControllerHelperService controllerHelperService,
      IndustryTeamMemberEditRolesValidator industryTeamMemberEditRolesValidator,
      IndustryTeamService industryTeamService) {
    super(industryTeamService);
    this.teamMemberService = teamMemberService;
    this.teamMemberViewService = teamMemberViewService;
    this.teamMemberRoleService = teamMemberRoleService;
    this.controllerHelperService = controllerHelperService;
    this.industryTeamMemberEditRolesValidator = industryTeamMemberEditRolesValidator;
  }

  @GetMapping
  public ModelAndView renderEditMember(@PathVariable("teamId") TeamId teamId,
                                       @PathVariable("wuaId") WebUserAccountId wuaId) {

    var form = new TeamMemberRolesForm();
    var team = getIndustryTeam(teamId);

    var teamMember = teamMemberService.getTeamMember(team, wuaId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No user [%s] in team [%s]".formatted(wuaId, teamId)));

    var userView = teamMemberViewService.getTeamMemberView(teamMember)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No roles found for user [%s] in team [%s]".formatted(wuaId, teamId)));

    form.setRoles(TeamRoleUtil.getRoleNames(teamMember.roles()));

    return getEditModelAndView(teamId, userView, form);

  }

  @PostMapping
  public ModelAndView editMember(@PathVariable("teamId") TeamId teamId,
                                 @PathVariable("wuaId") WebUserAccountId wuaId,
                                 @ModelAttribute("form") TeamMemberRolesForm form,
                                 BindingResult bindingResult) {

    var team = getIndustryTeam(teamId);

    var teamMember = teamMemberService.getTeamMember(team, wuaId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No user [%s] in team [%s]".formatted(wuaId, teamId)));

    var userView = teamMemberViewService.getTeamMemberView(teamMember)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No roles found for user [%s] in team [%s]".formatted(wuaId, teamId)));

    industryTeamMemberEditRolesValidator.validate(form, bindingResult,
        new TeamMemberEditRolesValidatorHints(team, teamMember));

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getEditModelAndView(teamId, userView, form),
        () -> {
          teamMemberRoleService.updateUserTeamRoles(team, teamMember.wuaId(), form.getRoles());
          return ReverseRouter.redirect(on(IndustryTeamManagementController.class).renderMemberList(teamId));
        });

  }

  private ModelAndView getEditModelAndView(TeamId teamId, TeamMemberView userView, TeamMemberRolesForm form) {
    return new ModelAndView("remi/permissionmanagement/addTeamMemberRoles")
        .addObject("form", form)
        .addObject("pageTitle", userView.getDisplayName())
        .addObject("roles", DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(IndustryTeamRole.class))
        .addObject("backLinkUrl",
          ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId))
        );
  }
}
