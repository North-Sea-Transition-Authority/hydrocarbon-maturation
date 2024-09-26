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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.NewResourceDetailsFormValidator;
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

  private final NewResourceDetailsFormValidator newResourceDetailsFormValidator;
  private final InternalTeamService internalTeamService;
  private final SubmissionCreateService submissionCreateService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  public NewDiscoveryController(NewResourceDetailsFormValidator newResourceDetailsFormValidator,
                                InternalTeamService internalTeamService,
                                SubmissionCreateService submissionCreateService,
                                OrganisationUnitQueryService organisationUnitQueryService) {
    this.newResourceDetailsFormValidator = newResourceDetailsFormValidator;
    this.internalTeamService = internalTeamService;
    this.submissionCreateService = submissionCreateService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @GetMapping
  public ModelAndView getNewDiscoveryPage(@ModelAttribute("form") ResourceDetailsForm form, User user) {
    return getNewDiscoveryPageModelAndView(form, user);
  }

  @PostMapping
  public ModelAndView createDiscovery(@Valid @ModelAttribute("form") ResourceDetailsForm form,
                                      BindingResult bindingResult,
                                      User user
  ) {
    if (newResourceDetailsFormValidator.isValid(bindingResult, ResourceType.DISCOVERY, user)) {
      var submissionDetail = submissionCreateService.createDiscovery(form, user);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null));
    } else {
      return getNewDiscoveryPageModelAndView(form, user);
    }
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
        .addObject("isRegulatorOrAreaTeamUser", internalTeamService.isRegulatorOrAreaTeamUser(user))
        .addObject("form", form)
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("alreadySelectedOrganisation",
            organisationUnitQueryService.getPreSelectedOrganisationUnit(
                NumberUtils.createInteger(form.getResponsibleOrganisationId()))
        );
  }
}

