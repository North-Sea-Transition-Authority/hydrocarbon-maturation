package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore;

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
@RequestMapping("/permission-management/southern-offshore-area/{teamId}/add-member/{wuaId}/roles")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_SOUTHERN_OFFSHORE_AREA_ROLES
)
public class SouthernOffshoreAreaAddRolesController extends AbstractSouthernOffshoreAreaPermissionManagement {

  private final ControllerHelperService controllerHelperService;
  private final SouthernOffshoreAreaTeamMemberRolesValidator southernOffshoreAreaTeamMemberRolesValidator;
  private final SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;
  private final AreaAddRolesControllerHelperService areaAddRolesControllerHelperService;

  @Autowired
  public SouthernOffshoreAreaAddRolesController(SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService,
                                                ControllerHelperService controllerHelperService,
                                                SouthernOffshoreAreaTeamMemberRolesValidator
                                                      southernOffshoreAreaTeamMemberRolesValidator,
                                                AreaAddRolesControllerHelperService areaAddRolesControllerHelperService) {
    super(southernOffshoreAreaTeamService);
    this.controllerHelperService = controllerHelperService;
    this.areaAddRolesControllerHelperService = areaAddRolesControllerHelperService;
    this.southernOffshoreAreaTeamMemberRolesValidator = southernOffshoreAreaTeamMemberRolesValidator;
    this.southernOffshoreAreaTeamService = southernOffshoreAreaTeamService;
  }

  @GetMapping
  public ModelAndView renderAddTeamMemberRoles(@PathVariable("teamId") TeamId teamId,
                                               @PathVariable("wuaId") WebUserAccountId webUserAccountId) {
    getAreaTeam(teamId);

    return areaAddRolesControllerHelperService.renderAddTeamMemberRoles(webUserAccountId,
        DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(
            SouthernOffshoreAreaTeamRole.class),
        ReverseRouter.route(on(
            SouthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)));
  }

  @PostMapping
  protected ModelAndView saveAddTeamMemberRoles(@PathVariable("teamId") TeamId teamId,
                                                @PathVariable("wuaId") WebUserAccountId webUserAccountId,
                                                @ModelAttribute("form") TeamMemberRolesForm form,
                                                BindingResult bindingResult) {
    var team = getAreaTeam(teamId);
    var energyPortalUser = areaAddRolesControllerHelperService.getEnergyPortalUser((webUserAccountId));
    southernOffshoreAreaTeamMemberRolesValidator.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        areaAddRolesControllerHelperService.getAddTeamMemberRolesModelAndView(energyPortalUser, form,
            ReverseRouter.route(on(SouthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
            DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(
                SouthernOffshoreAreaTeamRole.class)),
        () -> {
          var regulatorRoles = getRolesToAdd(form.getRoles());
          southernOffshoreAreaTeamService.addUserTeamRoles(team, energyPortalUser, regulatorRoles);

          return ReverseRouter.redirect(on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId));
        }
    );
  }

  private Set<SouthernOffshoreAreaTeamRole> getRolesToAdd(Set<String> rolesToAdd) {
    return rolesToAdd.stream()
        .map(SouthernOffshoreAreaTeamRole::getRoleFromString)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }
}
