package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddTeamMemberForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddTeamMemberValidator;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaAddMemberControllerHelperService;

@Controller
@RequestMapping("/permission-management/southern-offshore-area/{teamId}/add-member")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_SOUTHERN_OFFSHORE_AREA_ROLES
)
public class SouthernOffshoreAreaAddMemberController extends AbstractSouthernOffshoreAreaPermissionManagement {

  private final AddTeamMemberValidator addTeamMemberValidator;
  private final EnergyPortalUserService energyPortalUserService;
  private final AreaAddMemberControllerHelperService areaAddMemberControllerHelperService;

  @Autowired
  public SouthernOffshoreAreaAddMemberController(SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService,
                                                 AddTeamMemberValidator addTeamMemberValidator,
                                                 EnergyPortalUserService energyPortalUserService,
                                                 AreaAddMemberControllerHelperService areaAddMemberControllerHelperService) {
    super(southernOffshoreAreaTeamService);
    this.addTeamMemberValidator = addTeamMemberValidator;
    this.energyPortalUserService = energyPortalUserService;
    this.areaAddMemberControllerHelperService = areaAddMemberControllerHelperService;
  }

  @GetMapping
  ModelAndView renderAddTeamMember(@PathVariable("teamId") TeamId teamId) {
    var form = new AddTeamMemberForm();
    var submitUrl = ReverseRouter.route(on(SouthernOffshoreAreaAddMemberController.class)
        .addMemberToTeamSubmission(teamId, form, ReverseRouter.emptyBindingResult()));
    var backLinkUrl = ReverseRouter.route(on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId));

    return areaAddMemberControllerHelperService.getAddTeamMemberModelAndView(form, submitUrl, backLinkUrl, getAreaTeam(teamId));
  }

  @PostMapping
  public ModelAndView addMemberToTeamSubmission(@PathVariable("teamId") TeamId teamId,
                                                @ModelAttribute("form") AddTeamMemberForm form,
                                                BindingResult bindingResult) {
    addTeamMemberValidator.validate(form, bindingResult);

    var userToAdd = energyPortalUserService.findUserByUsername(form.getUsername()).get(0);
    var successfulRedirect = ReverseRouter.redirect(on(SouthernOffshoreAreaAddRolesController.class)
        .renderAddTeamMemberRoles(teamId, new WebUserAccountId(userToAdd.webUserAccountId())));
    var submitUrl = ReverseRouter.route(on(SouthernOffshoreAreaAddMemberController.class)
        .addMemberToTeamSubmission(teamId, form, ReverseRouter.emptyBindingResult()));
    var backLinkUrl = ReverseRouter.route(on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId));

    return areaAddMemberControllerHelperService.addMemberToTeamSubmission(
        bindingResult, form, getAreaTeam(teamId),
        successfulRedirect, submitUrl, backLinkUrl);
  }
}
