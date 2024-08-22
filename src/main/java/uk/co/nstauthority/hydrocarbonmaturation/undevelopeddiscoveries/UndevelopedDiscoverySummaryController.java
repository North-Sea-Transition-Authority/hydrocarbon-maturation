package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchProspectAndDiscoveryRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformationController;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/undeveloped-discovery")
@HasAnyRole(role = Role.RESERVES_AND_RESOURCES_ADVISOR, teamType = TeamType.REGULATOR)
public class UndevelopedDiscoverySummaryController {

  static final String PAGE_TITLE = "Undeveloped discovery";

  private final UndevelopedDiscoveryService undevelopedDiscoveryService;

  private final UndevelopedDiscoverySummaryService undevelopedDiscoverySummaryService;

  private final ViewResourceRecordService resourceRecordService;

  private final ResourceService resourceService;

  @Autowired
  public UndevelopedDiscoverySummaryController(UndevelopedDiscoveryService undevelopedDiscoveryService,
                                               UndevelopedDiscoverySummaryService undevelopedDiscoverySummaryService,
                                               ViewResourceRecordService resourceRecordService,
                                               ResourceService resourceService) {
    this.undevelopedDiscoveryService = undevelopedDiscoveryService;
    this.undevelopedDiscoverySummaryService = undevelopedDiscoverySummaryService;
    this.resourceRecordService = resourceRecordService;
    this.resourceService = resourceService;
  }

  @GetMapping
  public ModelAndView getUndevelopedDiscoverySummary(@PathVariable("resourceId") UUID resourceId, User user) {
    var resource = resourceService.getResourceOfTypeOrThrow(resourceId, ResourceType.DISCOVERY);

    var undevelopedDiscovery = undevelopedDiscoveryService.getUndevelopedDiscoveryForResource(resource)
        .orElseGet(() -> getNewUndevelopedDiscovery(resource));

    return getSummaryModelAndView(undevelopedDiscovery, resourceId, user);
  }

  private ModelAndView getSummaryModelAndView(UndevelopedDiscovery undevelopedDiscovery, UUID resourceId, User user) {
    var summarySections = undevelopedDiscoverySummaryService.getSummarySections(undevelopedDiscovery, user);

    var resourceRecord = resourceRecordService.getResourceRecordOrThrow(resourceId);

    var modelAndView = new ModelAndView("remi/undevelopeddiscoveries/undevelopedDiscoverySummary")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("summarySections", summarySections)
        .addObject("addDiscoveryInformationUrl", ReverseRouter.route(
            on(UndevelopedDiscoveryInformationController.class).getUndevelopedDiscoveryInformation(resourceId)))
        .addObject("accordionId", undevelopedDiscovery.getId().toString());

    var resourceBreadcrumb = SubmissionDetailDisplayUtil.getDisplayName(
        resourceRecord.resourceName(),
        resourceRecord.centreOfInterest(),
        resourceRecord.licenceApplicationReference()
    );

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_TITLE)
        .addBreadcrumb(
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE,
            ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)))
        .addBreadcrumb(
            resourceBreadcrumb,
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }

  private UndevelopedDiscovery getNewUndevelopedDiscovery(Resource resource) {
    var undevelopedDiscovery = new UndevelopedDiscovery();
    undevelopedDiscovery.setResource(resource);
    return undevelopedDiscovery;
  }
}
