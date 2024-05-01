package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
class SubmissionQueryService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionFilterService submissionFilterService;
  private final SubmissionSearchSelectorServiceFactory submissionSearchSelectorServiceFactory;
  private final ResourceGroupService resourceGroupService;
  private final ResourceLinkService resourceLinkService;
  private final IndustryTeamService industryTeamService;
  private final InternalTeamService internalTeamService;

  @Autowired
  SubmissionQueryService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                         SubmissionDetailService submissionDetailService,
                         SubmissionResourceService submissionResourceService,
                         SubmissionFilterService submissionFilterService,
                         SubmissionSearchSelectorServiceFactory submissionSearchSelectorServiceFactory,
                         ResourceGroupService resourceGroupService,
                         ResourceLinkService resourceLinkService, IndustryTeamService industryTeamService,
                         InternalTeamService internalTeamService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionFilterService = submissionFilterService;
    this.submissionSearchSelectorServiceFactory = submissionSearchSelectorServiceFactory;
    this.resourceGroupService = resourceGroupService;
    this.resourceLinkService = resourceLinkService;
    this.industryTeamService = industryTeamService;
    this.internalTeamService = internalTeamService;
  }

  public List<ResourceView> getLatestDiscoveriesByNameForSubmissionType(String name,
                                                                        SubmissionType submissionType) {
    //gets every discovery resource with the name specified
    var distinctDiscoveries =
        submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, name);

    //gets the latest submission for each discovery
    var latestSubmissionsWhichCanBeSubmittedUpon =
        distinctDiscoveries.stream()
            .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
            .flatMap(Optional::stream)
            .filter(detail -> submissionFilterService.filterDiscoverySubmissionByOtherSubmissions(detail, submissionType));

    return submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy()
        .getResourceDetailsForSubmissionsWithTeam(latestSubmissionsWhichCanBeSubmittedUpon, submissionType)
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  public List<ResourceView> getLatestProspectsByNameForSubmissionType(String name, SubmissionType submissionType) {
    //gets every prospect resource
    var distinctProspectsForSubmissionType =
        submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, name);

    //gets the latest approved submission if any for each prospect and filters out submissions from which you
    //can't start a submission
    var latestSubmissionsWhichCanBeSubmittedUpon =
        distinctProspectsForSubmissionType.stream()
            .map(submissionDetailService::getLatestApprovedProspectSubmissionForResource)
            .flatMap(Optional::stream)
            .filter(detail -> submissionFilterService.filterProspectSubmissionByOtherSubmissions(detail, submissionType));

    return submissionSearchSelectorServiceFactory
        .getSubmissionSearchSelectorStrategy()
        .getResourceDetailsForSubmissionsWithTeam(latestSubmissionsWhichCanBeSubmittedUpon, submissionType)
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  public List<ResourceView> getLatestResourceSubmissionsByName(String name, User user) {
    // Get every resource by name
    return submissionResourceService.getResourcesByName(name).stream()
        // remove any prospects that are linked to a discovery
        .filter(resource ->
            resource.getType() == ResourceType.DISCOVERY
                || (resource.getType() == ResourceType.PROSPECT && !resourceLinkService.prospectIsLinkedToDiscovery(resource))
        )
        .filter(resource -> filterResourceByUser(resource, user))
        // Get the latest approved submission for each resource
        .map(submissionDetailService::getLatestApprovedSubmissionDetailForResource)
        .flatMap(Optional::stream)
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        // Create a list of resource views for prospects to be displayed in a search selector
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  public List<ResourceView> getLatestNotRelinquishedAndNotInGroupOrPreferredViewDiscoveriesByName(String name) {
    //gets every discovery resource with the name specified
    var distinctDiscoveries =
        submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, name);

    //gets the latest submission for each discovery
    //filtering out ones in a group but not the preferred view
    var latestSubmissionsWhichCanBeSubmittedUpon =
        distinctDiscoveries.stream()
            .filter(resource -> !resourceGroupService.isInGroupAndIsNotPreferredView(resource))
            .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
            .flatMap(Optional::stream)
            .filter(submissionDetail -> !submissionDetail.isDiscoveryRelinquishment());

    return latestSubmissionsWhichCanBeSubmittedUpon
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  private ResourceView getResourceViewFromResourceDetail(ResourceDetail resourceDetail) {
    var resourceDisplayName = submissionResourceService.getResourceDisplayName(resourceDetail);
    return new ResourceView(resourceDetail.getId(), resourceDisplayName);
  }

  private boolean filterResourceByUser(Resource resource, User user) {
    if (internalTeamService.isRegulatorOrAreaTeamUser()) {
      return true;
    }

    Integer responsibleOrganisationId = submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource)
        .map(submissionDetail -> resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail).getOrganisationId())
        .orElse(null);
    var userSubmitterOrgUnits = industryTeamService
        .getOrganisationUnitIdsForUser(user);

    return (responsibleOrganisationId != null && userSubmitterOrgUnits.contains(responsibleOrganisationId));
  }
}