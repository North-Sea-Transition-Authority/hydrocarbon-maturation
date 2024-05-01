package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore;

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
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamMemberRolesForm;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaAddRolesControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/permission-management/northern-offshore-area/{teamId}/add-member/{wuaId}/roles")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_NORTHERN_OFFSHORE_AREA_ROLES
)
public class NorthernOffshoreAreaAddRolesController extends AbstractNorthernOffshoreAreaPermissionManagement {

  private final ControllerHelperService controllerHelperService;
  private final NorthernOffshoreAreaTeamMemberRolesValidator northernOffshoreAreaTeamMemberRolesValidator;
  private final NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;
  private final AreaAddRolesControllerHelperService areaAddRolesControllerHelperService;

  @Autowired
  public NorthernOffshoreAreaAddRolesController(NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService,
                                                ControllerHelperService controllerHelperService,
                                                NorthernOffshoreAreaTeamMemberRolesValidator
                                                      northernOffshoreAreaTeamMemberRolesValidator,
                                                AreaAddRolesControllerHelperService areaAddRolesControllerHelperService) {
    super(northernOffshoreAreaTeamService);
    this.controllerHelperService = controllerHelperService;
    this.areaAddRolesControllerHelperService = areaAddRolesControllerHelperService;
    this.northernOffshoreAreaTeamMemberRolesValidator = northernOffshoreAreaTeamMemberRolesValidator;
    this.northernOffshoreAreaTeamService = northernOffshoreAreaTeamService;
  }

  @GetMapping
  public ModelAndView renderAddTeamMemberRoles(@PathVariable("teamId") TeamId teamId,
                                               @PathVariable("wuaId") WebUserAccountId webUserAccountId) {
    getAreaTeam(teamId);

    return areaAddRolesControllerHelperService.renderAddTeamMemberRoles(webUserAccountId,
        DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(NorthernOffshoreAreaTeamRole.class),
        ReverseRouter.route(on(NorthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)));
  }

  @PostMapping
  protected ModelAndView saveAddTeamMemberRoles(@PathVariable("teamId") TeamId teamId,
                                                @PathVariable("wuaId") WebUserAccountId webUserAccountId,
                                                @ModelAttribute("form") TeamMemberRolesForm form,
                                                BindingResult bindingResult) {
    var team = getAreaTeam(teamId);
    var energyPortalUser = areaAddRolesControllerHelperService.getEnergyPortalUser((webUserAccountId));
    northernOffshoreAreaTeamMemberRolesValidator.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        areaAddRolesControllerHelperService.getAddTeamMemberRolesModelAndView(energyPortalUser, form,
            ReverseRouter.route(on(NorthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
            DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(NorthernOffshoreAreaTeamRole.class)),
        () -> {
          var regulatorRoles = getRolesToAdd(form.getRoles());
          northernOffshoreAreaTeamService.addUserTeamRoles(team, energyPortalUser, regulatorRoles);

          return ReverseRouter.redirect(on(NorthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId));
        }
    );
  }

  private Set<NorthernOffshoreAreaTeamRole> getRolesToAdd(Set<String> rolesToAdd) {
    return rolesToAdd.stream()
        .map(NorthernOffshoreAreaTeamRole::getRoleFromString)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }
}
