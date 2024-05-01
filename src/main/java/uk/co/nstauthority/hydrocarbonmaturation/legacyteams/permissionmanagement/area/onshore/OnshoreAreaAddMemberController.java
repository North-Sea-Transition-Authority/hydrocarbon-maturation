package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.AddTeamMemberForm;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.AddTeamMemberValidator;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaAddMemberControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/permission-management/onshore-area/{teamId}/add-member")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_ONSHORE_AREA_ROLES
)
public class OnshoreAreaAddMemberController extends AbstractOnshoreAreaPermissionManagement {

  private final AddTeamMemberValidator addTeamMemberValidator;
  private final EnergyPortalUserService energyPortalUserService;
  private final AreaAddMemberControllerHelperService areaAddMemberControllerHelperService;

  @Autowired
  public OnshoreAreaAddMemberController(OnshoreAreaTeamService onshoreAreaTeamService,
                                        AddTeamMemberValidator addTeamMemberValidator,
                                        EnergyPortalUserService energyPortalUserService,
                                        AreaAddMemberControllerHelperService areaAddMemberControllerHelperService) {
    super(onshoreAreaTeamService);
    this.addTeamMemberValidator = addTeamMemberValidator;
    this.energyPortalUserService = energyPortalUserService;
    this.areaAddMemberControllerHelperService = areaAddMemberControllerHelperService;
  }

  @GetMapping
  ModelAndView renderAddTeamMember(@PathVariable("teamId") TeamId teamId) {
    var form = new AddTeamMemberForm();
    var submitUrl = ReverseRouter.route(on(OnshoreAreaAddMemberController.class)
        .addMemberToTeamSubmission(teamId, form, ReverseRouter.emptyBindingResult()));
    var backLinkUrl = ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId));

    return areaAddMemberControllerHelperService.getAddTeamMemberModelAndView(form, submitUrl, backLinkUrl, getAreaTeam(teamId));
  }

  @PostMapping
  public ModelAndView addMemberToTeamSubmission(@PathVariable("teamId") TeamId teamId,
                                                @ModelAttribute("form") AddTeamMemberForm form,
                                                BindingResult bindingResult) {
    addTeamMemberValidator.validate(form, bindingResult);

    var userToAdd = energyPortalUserService.findUserByUsername(form.getUsername()).get(0);
    var successfulRedirect = ReverseRouter.redirect(on(OnshoreAreaAddRolesController.class)
        .renderAddTeamMemberRoles(teamId, new WebUserAccountId(userToAdd.webUserAccountId())));
    var submitUrl = ReverseRouter.route(on(OnshoreAreaAddMemberController.class)
        .addMemberToTeamSubmission(teamId, form, ReverseRouter.emptyBindingResult()));
    var backLinkUrl = ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId));

    return areaAddMemberControllerHelperService.addMemberToTeamSubmission(
        bindingResult, form, getAreaTeam(teamId),
        successfulRedirect, submitUrl, backLinkUrl);
  }
}
