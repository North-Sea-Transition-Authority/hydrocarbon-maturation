package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/view-resource-record/{resourceId}")
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
public class ResourceArchiveController {

  static final String ARCHIVE_QUESTION = "Are you sure you want to archive this resource?";
  static final String UNARCHIVE_QUESTION = "Are you sure you want to unarchive this resource?";

  private final ResourceService resourceService;
  private final ResourceArchiveService resourceArchiveService;
  private final ViewResourceRecordService viewResourceRecordService;
  private final ResponsibleTeamService responsibleTeamService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public ResourceArchiveController(ResourceService resourceService,
                                   ResourceArchiveService resourceArchiveService,
                                   ViewResourceRecordService viewResourceRecordService,
                                   ResponsibleTeamService responsibleTeamService,
                                   SubmissionResourceService submissionResourceService,
                                   SubmissionDetailService submissionDetailService) {
    this.resourceService = resourceService;
    this.resourceArchiveService = resourceArchiveService;
    this.viewResourceRecordService = viewResourceRecordService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionResourceService = submissionResourceService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping("/archive")
  public ModelAndView archiveResourceConfirmation(@PathVariable UUID resourceId, User user) {
    var resource = resourceService.getResourceOrThrow(resourceId);
    var resourceRecord = getResourceRecordOrThrowIfNotInResponsibleTeam(user, resource);
    var cannotBeArchivedWarningOptional = resourceArchiveService.resourceCannotBeArchivedWarning(resource);

    var modelAndView = getArchiveUnarchiveModelAndView(resource, resourceRecord,
        ARCHIVE_QUESTION);
    cannotBeArchivedWarningOptional.ifPresent(
        warning -> modelAndView.addObject("cannotBeArchivedReason", warning)
    );
    return modelAndView
            .addObject("buttonText", "Archive")
            .addObject("buttonClass", "govuk-button--warning");
  }

  @PostMapping("/archive")
  public ModelAndView archiveResource(@PathVariable UUID resourceId, User user) {
    var resource = resourceService.getResourceOrThrow(resourceId);

    checkUserIsInResponsibleTeam(user, resource);

    resourceArchiveService.resourceCannotBeArchivedWarning(resource)
        .ifPresent(warning -> {
          throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "Resource %s cannot be archived. %s.".formatted(resourceId, warning)
          );
        });

    resourceArchiveService.archiveResource(resource);
    return ReverseRouter.redirect(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null));
  }

  @GetMapping("/unarchive")
  public ModelAndView unarchiveResourceConfirmation(@PathVariable UUID resourceId, User user) {
    var resource = resourceService.getResourceOrThrow(resourceId);
    var resourceRecord = getResourceRecordOrThrowIfNotInResponsibleTeam(user, resource);


    var cannotBeUnarchivedWarningOptional = resourceArchiveService.resourceCannotBeUnarchivedWarning(resource);

    var modelAndView = getArchiveUnarchiveModelAndView(resource, resourceRecord, UNARCHIVE_QUESTION);

    cannotBeUnarchivedWarningOptional.ifPresent(
        warning -> modelAndView.addObject("cannotBeUnarchivedReason", warning)
    );

    return modelAndView
        .addObject("buttonText", "Unarchive")
        .addObject("buttonClass", "govuk-button--primary");
  }

  @PostMapping("/unarchive")
  public ModelAndView unarchiveResource(@PathVariable UUID resourceId, User user) {
    var resource = resourceService.getResourceOrThrow(resourceId);

    checkUserIsInResponsibleTeam(user, resource);

    resourceArchiveService.resourceCannotBeUnarchivedWarning(resource)
        .ifPresent(warning -> {
          throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "Resource %s cannot be unarchived. %s.".formatted(resourceId, warning)
          );
        });

    resourceArchiveService.unarchiveResource(resource);
    return ReverseRouter.redirect(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null));
  }

  private ModelAndView getArchiveUnarchiveModelAndView(Resource resource,
                                                       ResourceRecordSummaryView resourceRecordSummaryView,
                                                       String archiveQuestion) {
    var resourceRecordUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(resource.getId(), null));

    var submissionOnResource = submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource);
    String resourceDisplay;
    if (submissionOnResource.isPresent()) {
      resourceDisplay = submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
          submissionOnResource.get());
    } else {
      resourceDisplay = resource.getType().getDisplayName();
    }

    var modelAndView = new ModelAndView("remi/systemofrecord/archiveUnarchiveResource")
        .addObject("pageTitle", archiveQuestion)
        .addObject("caption", resourceDisplay)
        .addObject("cancelUrl", resourceRecordUrl);

    addBreadcrumbs(modelAndView, resource, archiveQuestion, resourceRecordUrl, resourceRecordSummaryView);

    return modelAndView;
  }

  private void addBreadcrumbs(ModelAndView modelAndView, Resource resource, String question,
                              String resourceRecordUrl, ResourceRecordSummaryView resourceRecord) {
    var resourceName = SubmissionDetailDisplayUtil.getDisplayName(
        resourceRecord.resourceName(),
        resourceRecord.centreOfInterest(),
        null
    );

    String baseSearchUrl;
    String baseSearchKey;
    if (ResourceType.FIELD.equals(resource.getType())) {
      baseSearchUrl = ReverseRouter.route(on(SearchFieldRecordsController.class).searchResourceRecords(null, null));
      baseSearchKey = SearchFieldRecordsController.PAGE_TITLE;
    } else {
      baseSearchUrl = ReverseRouter.route(
          on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null));
      baseSearchKey = SearchProspectAndDiscoveryRecordsController.PAGE_TITLE;
    }

    var breadcrumbs = Breadcrumbs.builder(question)
        .addBreadcrumb(baseSearchKey, baseSearchUrl)
        .addBreadcrumb(resourceName, resourceRecordUrl)
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }

  private ResourceRecordSummaryView getResourceRecordOrThrowIfNotInResponsibleTeam(User user, Resource resource) {
    // we have to check responsible team here as the @IsFromResponsibleTeam annotation is only built out for submission details
    var resourceRecord = viewResourceRecordService.getResourceRecordOrThrow(resource.getId());
    if (!responsibleTeamService.isInResponsibleTeam(user, resourceRecord.responsibleTeam())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User %s is not in responsible team for resource %s.".formatted(user.wuaId(), resource.getId())
      );

    }
    return resourceRecord;
  }

  private void checkUserIsInResponsibleTeam(User user, Resource resource) {
    var resourceRecord = viewResourceRecordService.getResourceRecordOrThrow(resource.getId());
    if (!responsibleTeamService.isInResponsibleTeam(user, resourceRecord.responsibleTeam())) {
      throw new ResponseStatusException(
              HttpStatus.FORBIDDEN,
              "User %s is not in responsible team for resource %s.".formatted(user.wuaId(), resource.getId())
      );

    }
  }
}
