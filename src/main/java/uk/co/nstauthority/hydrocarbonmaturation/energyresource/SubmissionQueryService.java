package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static uk.co.nstauthority.hydrocarbonmaturation.energyresource.SubmissionFilterUtil.filterByResourceType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

  public List<ResourceView> getLatestDiscoveriesByNameForSubmissionType(String name,
                                                                        SubmissionType submissionType) {
    //gets every discovery resource with the name specified
    Stream<Resource> distinctDiscoveries =
        generalDetailsPersistenceService.getGeneralDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getResourceId())
        .distinct()
        .filter(resource -> SubmissionFilterUtil.filterByResourceType(resource, ResourceType.DISCOVERY));

    //gets the latest general detail for each discovery
    Stream<SubmissionDetail> latestSubmissionsWhichCanBeSubmittedUpon =
        distinctDiscoveries
            .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
            .flatMap(Optional::stream)
            .filter(detail -> filterDiscoverySubmissionByOtherSubmissions(detail, submissionType));

    //Gets the list of resource views for discoveries to be displayed in a search selector.
    return latestSubmissionsWhichCanBeSubmittedUpon
        .map(generalDetailsPersistenceService::getGeneralDetailOrThrow)
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

    //gets the latest approved submission if any for each prospect and filters out submissions from which you
    //can't start a submission
    Stream<SubmissionDetail> latestSubmissionsWhichCanBeSubmittedUpon =
        distinctProspectsForSubmissionType
            .map(submissionDetailService::getLatestApprovedProspectSubmissionForResource)
            .flatMap(Optional::stream)
            .filter(detail -> filterProspectSubmissionByOtherSubmissions(detail, submissionType));

    //Gets the list of resource views for prospects to be displayed in a search selector
    return latestSubmissionsWhichCanBeSubmittedUpon
        .map(generalDetailsPersistenceService::getGeneralDetailOrThrow)
        .map(this::getResourceViewFromGeneralDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  /**
   * Gets the discoveries linked to the prospect.
   */
  private List<Resource> getLinkedDiscoveries(Resource prospect) {
    var resourceLinks = resourceLinkService.getResourceLinksByProspect(prospect);
    var linkedDiscoveries = new ArrayList<Resource>();
    resourceLinks.forEach(resourceLink ->
        linkedDiscoveries.add(resourceLink.getDiscoveryResource())
    );

    return linkedDiscoveries;
  }

  /** Checks other submissions on the discovery to determine whether a new submission can be made.
  * @param submission Current latest approved submission being checked
  * @param submissionToBeCreatedType Submission type that is being created
  * @return if the submission should be available to be chosen
  */
  private boolean filterDiscoverySubmissionByOtherSubmissions(SubmissionDetail submission,
                                                              SubmissionType submissionToBeCreatedType) {
    var otherSubmissionsOnResource = submissionDetailService
        .getSubmissionDetailsForResource(submission.getResourceId());

    return otherSubmissionsOnResource
        .stream()
        .noneMatch(submissionToBeChecked ->
            isSubmissionPreventingNewSubmission(submissionToBeChecked, submissionToBeCreatedType)
        );
  }

  /** Checks other submissions on the prospect to determine whether a new submission can be made.
   * @param submission Current latest approved submission being checked
   * @param submissionToBeCreatedType Submission type that is being created
   * @return if the submission should be available to be chosen
   */
  private boolean filterProspectSubmissionByOtherSubmissions(SubmissionDetail submission,
                                                             SubmissionType submissionToBeCreatedType) {
    if (isLinkedDiscoveryPreventingSubmission(
        submissionToBeCreatedType, submission.getResourceId())) {
      // cannot start a prospect update or relinquishment on a prospect with a discovery submission on it
      return false;
    }

    //Gets other submissions made on the submission's resource
    var otherSubmissionsOnResource =
        submissionDetailService.getSubmissionDetailsForResource(submission.getResourceId());

    //True means the condition prevents the creation of a submission of type 'submissionToBeCreatedType',
    //false means the submission does not prevent submissionToBeCreatedType
    //and that the next submission for the resource should be checked
    return otherSubmissionsOnResource
        .stream()
        .noneMatch(submissionToBeChecked ->
          isSubmissionPreventingNewSubmission(submissionToBeChecked, submissionToBeCreatedType)
        );
  }

  /**
   *  Checks the business rules relating to other submissions on the same resource
   *  for the submission type that is being created.
   * @param submissionToBeChecked The submission to be checked whether it prevents newSubmissionType from being submitted.
   * @param newSubmissionType The type of the submission being created.
   * @return True if the old submission prevents a new submission of type newSubmissionType.
   */
  private boolean isSubmissionPreventingNewSubmission(SubmissionDetail submissionToBeChecked,
                                                      SubmissionType newSubmissionType) {
    return switch (newSubmissionType) {
      case POST_DRILL_DRY_HOLE, CREATE_DISCOVERY_FROM_PROSPECT ->
          SubmissionFilterUtil.isSubmissionPreventingDryHoleOrDiscovery(submissionToBeChecked);
      case PROSPECT_UPDATE ->
          SubmissionFilterUtil.isSubmissionPreventingProspectUpdate(submissionToBeChecked);
      case RELINQUISHMENT_PROSPECT ->
          SubmissionFilterUtil.isSubmissionPreventingProspectRelinquishment(submissionToBeChecked);
      case RELINQUISHMENT_DISCOVERY ->
          SubmissionFilterUtil.isSubmissionPreventingDiscoveryRelinquishment(submissionToBeChecked);
      case UPDATE_DISCOVERY ->
          SubmissionFilterUtil.isSubmissionPreventingDiscoveryUpdate(submissionToBeChecked);
      default -> false;
    };
  }

  /**
   * Prospect relinquishment and prospect update cannot be started on a resource with a discovery submission.
   * This method returns true if the linked discovery prevents the submission of submissionTypeToBeCreated
   */
  private boolean isLinkedDiscoveryPreventingSubmission(SubmissionType submissionTypeToBeCreated,
                                                        Resource prospect) {
    //gets discovery resources linked to the prospect
    var linkedDiscoveries = getLinkedDiscoveries(prospect);
    var linkedDiscoverySubmissions = new ArrayList<SubmissionDetail>();
    ///Gets the submissions on the discovery and filters out the deleted ones
    linkedDiscoveries.forEach(discovery ->
        linkedDiscoverySubmissions.addAll(submissionDetailService.getSubmissionDetailsForResource(discovery)
            .stream()
            .filter(submissionDetail -> submissionDetail.getStatus() != SubmissionDetailStatus.DELETED)
            .toList())
    );
    //The list will be empty if there are no non-deleted discovery submissions.
    return !linkedDiscoverySubmissions.isEmpty()
        && (submissionTypeToBeCreated == SubmissionType.RELINQUISHMENT_PROSPECT
        || submissionTypeToBeCreated == SubmissionType.PROSPECT_UPDATE);
  }

  private ResourceView getResourceViewFromGeneralDetail(GeneralDetail generalDetail) {
    var centreOfInterestBlock = trapPropertiesPersistenceService
        .getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail());
    var prospectDisplayName = generalDetail.getResourceName() + centreOfInterestBlock
        .map(item -> " (" + item + ")")
        .orElse("");
    return new ResourceView(generalDetail.getId(), prospectDisplayName);
  }
}
