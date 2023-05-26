package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.offshore;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasTeamPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberEditRolesValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberRolesForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaEditMemberControllerHelperService;

@Controller
@RequestMapping("/permission-management/offshore-area/{teamId}/edit/{wuaId}")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_OFFSHORE_AREA_ROLES
)
public class OffshoreAreaEditMemberController extends AbstractOffshoreAreaPermissionManagement {

  private final TeamMemberRoleService teamMemberRoleService;
  private final ControllerHelperService controllerHelperService;
  private final OffshoreAreaTeamMemberEditRolesValidator offshoreAreaTeamMemberEditRolesValidator;
  private final AreaEditMemberControllerHelperService areaEditMemberControllerHelperService;

  private static final Map<String, String> ROLE_MAP = DisplayableEnumOptionUtil
      .getDisplayableOptionsWithDescription(OffshoreAreaTeamRole.class);

  @Autowired
  public OffshoreAreaEditMemberController(OffshoreAreaTeamService offshoreAreaTeamService,
                                          TeamMemberRoleService teamMemberRoleService,
                                          ControllerHelperService controllerHelperService,
                                          OffshoreAreaTeamMemberEditRolesValidator areaTeamMemberRolesValidator,
                                          AreaEditMemberControllerHelperService areaEditMemberControllerHelperService) {
    super(offshoreAreaTeamService);
    this.teamMemberRoleService = teamMemberRoleService;
    this.controllerHelperService = controllerHelperService;
    this.offshoreAreaTeamMemberEditRolesValidator = areaTeamMemberRolesValidator;
    this.areaEditMemberControllerHelperService = areaEditMemberControllerHelperService;
  }

  @GetMapping
  public ModelAndView renderEditMember(@PathVariable("teamId") TeamId teamId,
                                       @PathVariable("wuaId") WebUserAccountId wuaId) {

    var team = getAreaTeam(teamId);

    return areaEditMemberControllerHelperService.renderEditMember(team, wuaId, ROLE_MAP,
        ReverseRouter.route(on(OffshoreAreaTeamManagementController.class).renderMemberList(teamId)));
  }

  @PostMapping
  public ModelAndView editMember(@PathVariable("teamId") TeamId teamId,
                                 @PathVariable("wuaId") WebUserAccountId wuaId,
                                 @ModelAttribute("form") TeamMemberRolesForm form,
                                 BindingResult bindingResult) {

    var team = getAreaTeam(teamId);

    var teamMember = areaEditMemberControllerHelperService.getTeamMemberOrThrow(team, wuaId);
    var userView = areaEditMemberControllerHelperService.getTeamMemberViewOrThrow(teamMember);

    offshoreAreaTeamMemberEditRolesValidator.validate(form, bindingResult,
        new TeamMemberEditRolesValidatorHints(team, teamMember));

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getEditModelAndView(teamId, userView, form),
        () -> {
          teamMemberRoleService.updateUserTeamRoles(team, teamMember.wuaId(), form.getRoles());
          return ReverseRouter.redirect(on(OffshoreAreaTeamManagementController.class).renderMemberList(teamId));
        });
  }

  private ModelAndView getEditModelAndView(TeamId teamId, TeamMemberView userView, TeamMemberRolesForm form) {
    return new ModelAndView("remi/permissionmanagement/addTeamMemberRoles")
        .addObject("form", form)
        .addObject("pageTitle", userView.getDisplayName())
        .addObject("roles", ROLE_MAP)
        .addObject("backLinkUrl",
            ReverseRouter.route(on(OffshoreAreaTeamManagementController.class).renderMemberList(teamId))
        );
  }
}
