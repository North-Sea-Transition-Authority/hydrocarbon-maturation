package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@Service
public class SubmissionQueryService {

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceLinkService resourceLinkService;

  @Autowired
  SubmissionQueryService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                         TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                         SubmissionDetailService submissionDetailService,
                         ResourceLinkService resourceLinkService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.resourceLinkService = resourceLinkService;
  }

  public List<ResourceView> getLatestDiscoveriesByName(String name) {
    //gets every discovery resource with the name specified
    Stream<Resource> distinctDiscoveries =
        generalDetailsPersistenceService.getGeneralDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getResourceId())
        .distinct()
        .filter(resource -> filterByResourceType(resource, ResourceType.DISCOVERY));

    //gets the latest general detail for each discovery
    Stream<GeneralDetail> latestGeneralDetailsForResources =
        distinctDiscoveries
            .map(submissionDetailService::getLatestSubmissionDetailForResourceOrThrow)
            .map(generalDetailsPersistenceService::getGeneralDetailOrThrow);

    //Gets the list of resource views for discoveries to be displayed in a search selector.
    return latestGeneralDetailsForResources
        .map(this::getResourceViewFromGeneralDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  public List<ResourceView> getLatestProspectsByNameForSubmissionType(String name, SubmissionType submissionType) {
    //gets every prospect resource
    Stream<Resource> distinctProspectsForSubmissionType =
        generalDetailsPersistenceService.getGeneralDetailsByName(name)
            .stream()
            .map(detail -> detail.getSubmissionDetail().getResourceId())
            .distinct()
            .filter(resource -> filterByResourceType(resource, ResourceType.PROSPECT));

    //gets the latest submission for each prospect and filters out submissions from which you can't start a submission
    Stream<SubmissionDetail> latestSubmissionsWhichCanBeSubmittedUpon =
        distinctProspectsForSubmissionType
            .map(submissionDetailService::getLatestSubmissionDetailForResourceOrThrow)
            .filter(detail -> filterSubmissionByOtherSubmissions(detail, submissionType));

    //Gets the list of resource views for prospects to be displayed in a search selector
    return latestSubmissionsWhichCanBeSubmittedUpon
        .map(generalDetailsPersistenceService::getGeneralDetailOrThrow)
        .map(this::getResourceViewFromGeneralDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  private boolean filterByResourceType(Resource currentResource, ResourceType resourceType) {
    return resourceType == currentResource.getType();
  }

  /**
   * Checks the resource for any links to ensure the submissions of the linked resources are added to the list.
   */
  private List<SubmissionDetail> addSubmissionDetailsFromLinkedDiscoveries(Resource resource,
                                                                           List<SubmissionDetail> submissionDetails) {
    var resourceLinks = resourceLinkService.getResourceLinksByProspect(resource);
    resourceLinks.forEach(resourceLink ->
        submissionDetails.addAll(
            submissionDetailService.getSubmissionDetailsForResource(resourceLink.getDiscoveryResource()))
    );

    return submissionDetails;
  }

  /** Checks other submissions on the resource to determine whether a new submission can be made.
   * @param submission Current submission being checked
   * @param submissionType submission type that is being created
   * @return if the submission should be available to be chosen
   */
  private boolean filterSubmissionByOtherSubmissions(SubmissionDetail submission,
                                                     SubmissionType submissionType) {
    var status = submission.getStatus();
    var type = submission.getSubmissionType();
    //ensures only approved prospect submissions can be shown on search selectors
    if ((type != SubmissionType.CREATE_PROSPECT
        && status != SubmissionDetailStatus.APPROVED)
        || (type != SubmissionType.PROSPECT_UPDATE
        && status != SubmissionDetailStatus.APPROVED)) {
      return false;
    }

    var otherSubmissionsOnResource = addSubmissionDetailsFromLinkedDiscoveries(
        submission.getResourceId(), submissionDetailService.getSubmissionDetailsForResource(submission.getResourceId()));

    return otherSubmissionsOnResource
        .stream()
        .allMatch(submissionDetail -> {
          //checks each submission detail for that resource for the existence of a submission
          //which prevents the submission of another prospect update, dry well and discovery submission
          var existingSubmissionType = submissionDetail.getSubmissionType();
          if (existingSubmissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
            return false;
          }
          return submissionType != SubmissionType.PROSPECT_UPDATE
              || existingSubmissionType != SubmissionType.CREATE_DISCOVERY;
        });
  }

  private ResourceView getResourceViewFromGeneralDetail(GeneralDetail generalDetail) {
    var centreOfInterestBlock = trapPropertiesPersistenceService
        .getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail());
    var prospectDisplayName = generalDetail.getProspectName() + centreOfInterestBlock
        .map(item -> " (" + item + ")")
        .orElse("");
    return new ResourceView(generalDetail.getId(), prospectDisplayName);
  }
}
