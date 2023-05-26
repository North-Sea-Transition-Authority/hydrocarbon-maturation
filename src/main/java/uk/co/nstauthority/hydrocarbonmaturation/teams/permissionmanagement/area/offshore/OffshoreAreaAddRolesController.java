package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.offshore;

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
@RequestMapping("/permission-management/offshore-area/{teamId}/add-member/{wuaId}/roles")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_OFFSHORE_AREA_ROLES
)
public class OffshoreAreaAddRolesController extends AbstractOffshoreAreaPermissionManagement {

  private final ControllerHelperService controllerHelperService;
  private final OffshoreAreaTeamMemberRolesValidator offshoreAreaTeamMemberRolesValidator;
  private final OffshoreAreaTeamService offshoreAreaTeamService;
  private final AreaAddRolesControllerHelperService areaAddRolesControllerHelperService;

  @Autowired
  public OffshoreAreaAddRolesController(OffshoreAreaTeamService offshoreAreaTeamService,
                                        ControllerHelperService controllerHelperService,
                                        OffshoreAreaTeamMemberRolesValidator offshoreAreaTeamMemberRolesValidator,
                                        AreaAddRolesControllerHelperService areaAddRolesControllerHelperService) {
    super(offshoreAreaTeamService);
    this.controllerHelperService = controllerHelperService;
    this.areaAddRolesControllerHelperService = areaAddRolesControllerHelperService;
    this.offshoreAreaTeamMemberRolesValidator = offshoreAreaTeamMemberRolesValidator;
    this.offshoreAreaTeamService = offshoreAreaTeamService;
  }

  @GetMapping
  public ModelAndView renderAddTeamMemberRoles(@PathVariable("teamId") TeamId teamId,
                                               @PathVariable("wuaId") WebUserAccountId webUserAccountId) {
    getAreaTeam(teamId);

    return areaAddRolesControllerHelperService.renderAddTeamMemberRoles(webUserAccountId,
        DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(OffshoreAreaTeamRole.class),
        ReverseRouter.route(on(OffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)));
  }

  @PostMapping
  protected ModelAndView saveAddTeamMemberRoles(@PathVariable("teamId") TeamId teamId,
                                                @PathVariable("wuaId") WebUserAccountId webUserAccountId,
                                                @ModelAttribute("form") TeamMemberRolesForm form,
                                                BindingResult bindingResult) {
    var team = getAreaTeam(teamId);
    var energyPortalUser = areaAddRolesControllerHelperService.getEnergyPortalUser((webUserAccountId));
    offshoreAreaTeamMemberRolesValidator.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        areaAddRolesControllerHelperService.getAddTeamMemberRolesModelAndView(energyPortalUser, form,
            ReverseRouter.route(on(OffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
            DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(OffshoreAreaTeamRole.class)),
        () -> {
          var regulatorRoles = getRolesToAdd(form.getRoles());
          offshoreAreaTeamService.addUserTeamRoles(team, energyPortalUser, regulatorRoles);

          return ReverseRouter.redirect(on(OffshoreAreaTeamManagementController.class).renderMemberList(teamId));
        }
    );
  }

  private Set<OffshoreAreaTeamRole> getRolesToAdd(Set<String> rolesToAdd) {
    return rolesToAdd.stream()
        .map(OffshoreAreaTeamRole::getRoleFromString)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }
}
