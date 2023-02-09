package uk.co.nstauthority.hydrocarbonmaturation.prospect;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Controller
@RequestMapping("prospect/create")
@HasPermission(permissions = {RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT})
public class NewProspectController {

  private final ControllerHelperService controllerHelperService;
  private final StartProspectService startProspectService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  public NewProspectController(ControllerHelperService controllerHelperService,
                               StartProspectService startProspectService,
                               UserDetailService userDetailService,
                               RegulatorTeamService regulatorTeamService) {
    this.controllerHelperService = controllerHelperService;
    this.startProspectService = startProspectService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  @GetMapping
  public ModelAndView getNewProspectPage(@ModelAttribute("form") ResourceDetailsForm form) {
    return getNewProspectPageModelAndView(form);
  }

  @PostMapping
  public ModelAndView createProspect(@Valid @ModelAttribute("form") ResourceDetailsForm form,
                                     BindingResult bindingResult) {
    bindingResult = startProspectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewProspectPageModelAndView(form),
        () -> {
          var submissionDetail = startProspectService.createInitialProspect(form);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
        }
    );
  }

  private ModelAndView getNewProspectPageModelAndView(ResourceDetailsForm form) {
    return new ModelAndView("remi/prospect/newResource")
        .addObject("backLinkUrl", ReverseRouter.route(on(StartProspectController.class).getStartPage()))
        .addObject("actionUrl", ReverseRouter.route(on(NewProspectController.class)
            .createProspect(null, null)))
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null)))
        .addObject("resourceType",
            ResourceType.PROSPECT.getDisplayName().toLowerCase())
        .addObject("isRegulator", regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail()))
        .addObject("form", form)
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore());
  }
}
