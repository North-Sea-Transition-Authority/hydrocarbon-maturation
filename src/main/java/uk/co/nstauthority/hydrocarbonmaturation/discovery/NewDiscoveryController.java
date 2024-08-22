package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/discovery/create")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
public class NewDiscoveryController {

  private final ControllerHelperService controllerHelperService;
  private final StartDiscoveryService startDiscoveryService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final InternalTeamService internalTeamService;

  @Autowired
  public NewDiscoveryController(ControllerHelperService controllerHelperService,
                                StartDiscoveryService startDiscoveryService,
                                OrganisationUnitQueryService organisationUnitQueryService,
                                InternalTeamService internalTeamService) {
    this.controllerHelperService = controllerHelperService;
    this.startDiscoveryService = startDiscoveryService;
    this.internalTeamService = internalTeamService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @GetMapping
  public ModelAndView getNewDiscoveryPage(@ModelAttribute("form") ResourceDetailsForm form, User user) {
    return getNewDiscoveryPageModelAndView(form, user);
  }

  @PostMapping
  public ModelAndView createDiscovery(@Valid @ModelAttribute("form") ResourceDetailsForm form,
                                      BindingResult bindingResult,
                                      User user) {
    bindingResult = startDiscoveryService.validateNewDiscovery(form, bindingResult, user);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewDiscoveryPageModelAndView(form, user),
        () -> {
          var submissionDetail = startDiscoveryService.createNewDiscovery(form, user);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null));
        }
    );
  }

  private ModelAndView getNewDiscoveryPageModelAndView(ResourceDetailsForm form, User user) {
    return new ModelAndView("remi/prospect/newResource")
        .addObject("backLinkUrl", ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()))
        .addObject("actionUrl", ReverseRouter.route(on(NewDiscoveryController.class)
            .createDiscovery(null, null, null)))
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)))
        .addObject("resourceType",
            ResourceType.DISCOVERY.getDisplayName().toLowerCase())
        .addObject("isRegulatorOrAreaTeamUser", isRegulatorOrAreaTeamUser(user))
        .addObject("form", form)
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("alreadySelectedOrganisation",
            organisationUnitQueryService.getPreSelectedOrganisationUnit(
                NumberUtils.createInteger(form.getResponsibleOrganisationId()))
        );
  }

  private boolean isRegulatorOrAreaTeamUser(User user) {
    return internalTeamService.isRegulatorOrAreaTeamUser(user);
  }
}

