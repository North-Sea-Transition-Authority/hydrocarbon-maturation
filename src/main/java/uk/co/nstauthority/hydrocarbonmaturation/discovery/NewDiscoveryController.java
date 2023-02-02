package uk.co.nstauthority.hydrocarbonmaturation.discovery;

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
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Controller
@RequestMapping("/discovery/create")
@HasPermission(permissions = {RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT})
public class NewDiscoveryController {

  private final ControllerHelperService controllerHelperService;
  private final StartDiscoveryService startDiscoveryService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  public NewDiscoveryController(ControllerHelperService controllerHelperService,
                                StartDiscoveryService startDiscoveryService,
                                UserDetailService userDetailService,
                                RegulatorTeamService regulatorTeamService) {
    this.controllerHelperService = controllerHelperService;
    this.startDiscoveryService = startDiscoveryService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  @GetMapping
  public ModelAndView getNewDiscoveryPage(@ModelAttribute("form") NewResourceForm form) {
    return getNewDiscoveryPageModelAndView(form);
  }

  @PostMapping
  public ModelAndView createDiscovery(@Valid @ModelAttribute("form") NewResourceForm form,
                                      BindingResult bindingResult) {
    bindingResult = startDiscoveryService.validateNewDiscovery(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewDiscoveryPageModelAndView(form),
        () -> {
          var submissionDetail = startDiscoveryService.createNewDiscovery(form);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
        }
    );
  }

  private ModelAndView getNewDiscoveryPageModelAndView(NewResourceForm form) {
    return new ModelAndView("remi/prospect/newResource")
        .addObject("backLinkUrl", ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()))
        .addObject("actionUrl", ReverseRouter.route(on(NewDiscoveryController.class)
            .createDiscovery(null, null)))
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null)))
        .addObject("resourceType",
            ResourceType.DISCOVERY.getDisplayName().toLowerCase())
        .addObject("isRegulator", regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail()))
        .addObject("form", form)
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore());
  }
}

