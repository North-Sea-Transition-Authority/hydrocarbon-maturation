package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

  @Autowired
  SubmissionQueryService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                         SubmissionDetailService submissionDetailService,
                         SubmissionResourceService submissionResourceService,
                         SubmissionFilterService submissionFilterService,
                         SubmissionSearchSelectorServiceFactory submissionSearchSelectorServiceFactory) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionFilterService = submissionFilterService;
    this.submissionSearchSelectorServiceFactory = submissionSearchSelectorServiceFactory;
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
        .getResourceDetailsForSubmissions(latestSubmissionsWhichCanBeSubmittedUpon, submissionType)
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
        .getResourceDetailsForSubmissions(latestSubmissionsWhichCanBeSubmittedUpon, submissionType)
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  public List<ResourceView> getLatestResourceSubmissionsByName(String name) {
    // Get every resource by name
    return submissionResourceService.getResourcesByName(name).stream()
        // Get the latest approved submission for each resource
        .map(submissionDetailService::getLatestApprovedSubmissionDetailForResource)
        .flatMap(Optional::stream)
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        // Create a list of resource views for prospects to be displayed in a search selector
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  private ResourceView getResourceViewFromResourceDetail(ResourceDetail resourceDetail) {
    var resourceDisplayName = submissionResourceService.getResourceDisplayName(resourceDetail);
    return new ResourceView(resourceDetail.getId(), resourceDisplayName);
  }
}
