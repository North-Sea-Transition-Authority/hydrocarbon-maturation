package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.NewResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/discovery/post-field/new/{resourceId}")
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
public class PostFieldDiscoveryController {

  private final PostFieldDiscoveryService postFieldDiscoveryService;
  private final ResourceService resourceService;
  private final FieldPersistenceService fieldPersistenceService;
  private final InternalTeamService internalTeamService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final NewResourceDetailsFormValidator newResourceDetailsFormValidator;

  @Autowired
  public PostFieldDiscoveryController(PostFieldDiscoveryService postFieldDiscoveryService,
                                      ResourceService resourceService,
                                      FieldPersistenceService fieldPersistenceService,
                                      InternalTeamService internalTeamService,
                                      OrganisationUnitQueryService organisationUnitQueryService,
                                      NewResourceDetailsFormValidator newResourceDetailsFormValidator) {
    this.postFieldDiscoveryService = postFieldDiscoveryService;
    this.resourceService = resourceService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.internalTeamService = internalTeamService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.newResourceDetailsFormValidator = newResourceDetailsFormValidator;
  }

  @GetMapping
  public ModelAndView getCreatePostFieldDiscoveryPage(@PathVariable UUID resourceId,
                                                      User user) {
    var field = fieldPersistenceService.getFieldOrThrow(resourceId);
    var form = new ResourceDetailsForm();
    form.setOnshoreOffshore(field.getOnshoreOffshore());

    return getNewPostFieldDiscoveryPageModelAndView(form, resourceId, user);
  }

  @PostMapping
  public ModelAndView createPostFieldDiscovery(@PathVariable UUID resourceId,
                                               @ModelAttribute("form") ResourceDetailsForm form,
                                               BindingResult bindingResult,
                                               User user) {
    var resource = resourceService.getResourceById(resourceId)
            .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
                    .formatted(resourceId)));

    if (newResourceDetailsFormValidator.isValid(bindingResult, ResourceType.DISCOVERY, user)) {
      var submissionDetail = postFieldDiscoveryService.createNewDiscoveryFromField(resource, user, form);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null));
    } else {
      return getNewPostFieldDiscoveryPageModelAndView(form, resourceId, user);
    }
  }

  private ModelAndView getNewPostFieldDiscoveryPageModelAndView(ResourceDetailsForm form, UUID resourceId, User user) {
    return new ModelAndView("remi/prospect/newResource")
        .addObject("backLinkUrl", ReverseRouter.route(on(StartPostFieldDiscoveryController.class)
            .getStartPostFieldDiscoveryPage(resourceId, null)))
        .addObject("actionUrl", ReverseRouter.route(on(PostFieldDiscoveryController.class)
            .createPostFieldDiscovery(resourceId, null, null, null)))
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)))
        .addObject("resourceType", ResourceType.DISCOVERY.getDisplayName().toLowerCase())
        .addObject("isRegulatorOrAreaTeamUser", internalTeamService.isRegulatorOrAreaTeamUser(user))
        .addObject("form", form)
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("alreadySelectedOrganisation",
            organisationUnitQueryService.getPreSelectedOrganisationUnit(
                  NumberUtils.createInteger(form.getResponsibleOrganisationId()))
        );
  }
}
