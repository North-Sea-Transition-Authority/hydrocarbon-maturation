package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.EnergyPortalConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddTeamMemberForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddTeamMemberValidator;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/permission-management/industry/{teamId}")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.INDUSTRY_MANAGE_ORGANISATION,
    anyNonTeamPermissionOf = RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS
)
class IndustryAddMemberController extends AbstractIndustryPermissionManagement {

  private final ControllerHelperService controllerHelperService;
  private final AddTeamMemberValidator addTeamMemberValidator;
  private final EnergyPortalUserService energyPortalUserService;
  private final EnergyPortalConfiguration energyPortalConfiguration;

  @Autowired
  IndustryAddMemberController(ControllerHelperService controllerHelperService,
                              AddTeamMemberValidator addTeamMemberValidator,
                              EnergyPortalUserService energyPortalUserService,
                              EnergyPortalConfiguration energyPortalConfiguration,
                              IndustryTeamService industryTeamService) {
    super(industryTeamService);
    this.controllerHelperService = controllerHelperService;
    this.addTeamMemberValidator = addTeamMemberValidator;
    this.energyPortalUserService = energyPortalUserService;
    this.energyPortalConfiguration = energyPortalConfiguration;
  }

  @GetMapping("/add-member")
  ModelAndView renderAddTeamMember(@PathVariable("teamId") TeamId teamId) {
    var form = new AddTeamMemberForm();
    return getAddTeamMemberModelAndView(teamId, form);
  }

  @PostMapping("/add-member")
  public ModelAndView addMemberToTeamSubmission(@PathVariable("teamId") TeamId teamId,
                                                @ModelAttribute("form") AddTeamMemberForm form,
                                                BindingResult bindingResult) {
    addTeamMemberValidator.validate(form, bindingResult);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAddTeamMemberModelAndView(teamId, form),
        () -> {
          var userToAdd = energyPortalUserService.findUserByUsername(form.getUsername()).get(0);
          return ReverseRouter.redirect(on(IndustryAddRolesController.class)
              .renderAddTeamMemberRoles(teamId, new WebUserAccountId(userToAdd.webUserAccountId())));
        }
    );
  }

  private ModelAndView getAddTeamMemberModelAndView(TeamId teamId, AddTeamMemberForm form) {
    var team = getIndustryTeam(teamId);
    return new ModelAndView("remi/permissionmanagement/addTeamMember")
        .addObject("htmlTitle", "Add user to %s".formatted(team.getDisplayName()))
        .addObject("registrationUrl", energyPortalConfiguration.registrationUrl())
        .addObject("form", form)
        .addObject(
            "submitUrl",
            ReverseRouter.route(on(IndustryAddMemberController.class)
                .addMemberToTeamSubmission(teamId, form, ReverseRouter.emptyBindingResult()))
        )
        .addObject(
            "backLinkUrl",
            ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId))
        );
  }
}