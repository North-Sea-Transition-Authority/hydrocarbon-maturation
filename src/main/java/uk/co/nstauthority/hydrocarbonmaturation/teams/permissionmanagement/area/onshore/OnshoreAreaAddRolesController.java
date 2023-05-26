package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberRolesForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaAddRolesControllerHelperService;

@Controller
@RequestMapping("/permission-management/onshore-area/{teamId}/add-member/{wuaId}/roles")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_ONSHORE_AREA_ROLES
)
public class OnshoreAreaAddRolesController extends AbstractOnshoreAreaPermissionManagement {

  private final ControllerHelperService controllerHelperService;
  private final OnshoreAreaTeamMemberRolesValidator onshoreAreaTeamMemberRolesValidator;
  private final OnshoreAreaTeamService onshoreAreaTeamService;
  private final AreaAddRolesControllerHelperService areaAddRolesControllerHelperService;

  @Autowired
  public OnshoreAreaAddRolesController(OnshoreAreaTeamService onshoreAreaTeamService,
                                       ControllerHelperService controllerHelperService,
                                       OnshoreAreaTeamMemberRolesValidator onshoreAreaTeamMemberRolesValidator,
                                       AreaAddRolesControllerHelperService areaAddRolesControllerHelperService) {
    super(onshoreAreaTeamService);
    this.controllerHelperService = controllerHelperService;
    this.areaAddRolesControllerHelperService = areaAddRolesControllerHelperService;
    this.onshoreAreaTeamMemberRolesValidator = onshoreAreaTeamMemberRolesValidator;
    this.onshoreAreaTeamService = onshoreAreaTeamService;
  }

  @GetMapping
  public ModelAndView renderAddTeamMemberRoles(@PathVariable("teamId") TeamId teamId,
                                               @PathVariable("wuaId") WebUserAccountId webUserAccountId) {
    getAreaTeam(teamId);

    return areaAddRolesControllerHelperService.renderAddTeamMemberRoles(webUserAccountId,
        DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(OnshoreAreaTeamRole.class),
        ReverseRouter.route(on(OnshoreAreaAddMemberController.class).renderAddTeamMember(teamId)));
  }

  @PostMapping
  protected ModelAndView saveAddTeamMemberRoles(@PathVariable("teamId") TeamId teamId,
                                                @PathVariable("wuaId") WebUserAccountId webUserAccountId,
                                                @ModelAttribute("form") TeamMemberRolesForm form,
                                                BindingResult bindingResult) {
    var team = getAreaTeam(teamId);
    var energyPortalUser = areaAddRolesControllerHelperService.getEnergyPortalUser(webUserAccountId);
    onshoreAreaTeamMemberRolesValidator.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        areaAddRolesControllerHelperService.getAddTeamMemberRolesModelAndView(energyPortalUser, form,
            ReverseRouter.route(on(OnshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
            DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(OnshoreAreaTeamRole.class)),
        () -> {
          var onshoreAreaTeamRoles = getRolesToAdd(form.getRoles());
          onshoreAreaTeamService.addUserTeamRoles(team, energyPortalUser, onshoreAreaTeamRoles);

          return ReverseRouter.redirect(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId));
        }
    );
  }

  private Set<OnshoreAreaTeamRole> getRolesToAdd(Set<String> rolesToAdd) {
    return rolesToAdd.stream()
        .map(OnshoreAreaTeamRole::getRoleFromString)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }
}
