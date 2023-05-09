package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@Service
class SubmissionFilterService {

  private final SubmissionDetailService submissionDetailService;
  private final ResourceLinkService resourceLinkService;

  @Autowired
  SubmissionFilterService(SubmissionDetailService submissionDetailService,
                          ResourceLinkService resourceLinkService) {
    this.submissionDetailService = submissionDetailService;
    this.resourceLinkService = resourceLinkService;
  }

  /** Checks other submissions on the discovery to determine whether a new submissionDetail can be made.
   * @param submissionDetail Current latest approved submissionDetail being checked
   * @param submissionToBeCreatedType Submission type that is being created
   * @return if the submissionDetail should be available to be chosen
   */
  boolean filterDiscoverySubmissionByOtherSubmissions(SubmissionDetail submissionDetail,
                                                      SubmissionType submissionToBeCreatedType) {
    var submissionsOnResource = submissionDetailService
        .getExtantSubmissionDetailsForResource(submissionDetail.getSubmission().getResource());

    return submissionsOnResource
        .stream()
        .noneMatch(submissionDetailToBeChecked ->
            isSubmissionPreventingNewSubmission(submissionDetailToBeChecked, submissionToBeCreatedType)
        );
  }

  /** Checks other submissions on the prospect to determine whether a new submissionDetail can be made.
   * @param submissionDetail Current latest approved submissionDetail being checked
   * @param submissionToBeCreatedType Submission type that is being created
   * @return if the submissionDetail should be available to be chosen
   */
  boolean filterProspectSubmissionByOtherSubmissions(SubmissionDetail submissionDetail,
                                                     SubmissionType submissionToBeCreatedType) {
    if (isLinkedDiscoveryPreventingSubmission(
        submissionToBeCreatedType, submissionDetail.getSubmission().getResource())) {
      // cannot start a prospect update or relinquishment on a prospect with a discovery submissionDetail on it
      return false;
    }

    //Gets other submissions made on the submissionDetail's resource
    var submissionDetailsOnResource = submissionDetailService
        .getExtantSubmissionDetailsForResource(submissionDetail.getSubmission().getResource());

    //True means the condition prevents the creation of a submissionDetail of type 'submissionToBeCreatedType',
    //false means the submissionDetail does not prevent submissionToBeCreatedType
    //and that the next submissionDetail for the resource should be checked
    return submissionDetailsOnResource
        .stream()
        .noneMatch(submissionDetailToBeChecked ->
            isSubmissionPreventingNewSubmission(submissionDetailToBeChecked, submissionToBeCreatedType)
        );
  }

  /**
   * Prospect relinquishment and prospect update cannot be started on a resource with a discovery submission.
   * This method returns true if the linked discovery prevents the submission of submissionTypeToBeCreated
   */
  private boolean isLinkedDiscoveryPreventingSubmission(SubmissionType submissionTypeToBeCreated,
                                                        Resource prospect) {
    //gets discovery resources linked to the prospect
    var linkedDiscoveries = getLinkedDiscoveries(prospect);
    var linkedDiscoverySubmissionDetails = new ArrayList<SubmissionDetail>();
    ///Gets the submissions on the discovery and filters out the deleted ones
    linkedDiscoveries.forEach(discovery ->
        linkedDiscoverySubmissionDetails.addAll(submissionDetailService.getExtantSubmissionDetailsForResource(discovery)
            .stream()
            .toList())
    );
    //The list will be empty if there are no non-deleted discovery submissions.
    return !linkedDiscoverySubmissionDetails.isEmpty()
        && (submissionTypeToBeCreated == SubmissionType.RELINQUISHMENT_PROSPECT
        || submissionTypeToBeCreated == SubmissionType.PROSPECT_UPDATE);
  }

  /**
   *  Checks the business rules relating to other submissions on the same resource
   *  for the submission type that is being created.
   * @param submissionDetailsToBeChecked The submission detail to be checked whether
   *                                     it prevents newSubmissionType from being submitted.
   * @param newSubmissionType The type of the submission being created.
   * @return True if the old submission prevents a new submission of type newSubmissionType.
   */
  private boolean isSubmissionPreventingNewSubmission(SubmissionDetail submissionDetailsToBeChecked,
                                                      SubmissionType newSubmissionType) {
    return switch (newSubmissionType) {
      case POST_DRILL_DRY_HOLE, CREATE_DISCOVERY_FROM_PROSPECT ->
          SubmissionFilterUtil.isSubmissionPreventingDryHoleOrDiscovery(submissionDetailsToBeChecked);
      case PROSPECT_UPDATE ->
          SubmissionFilterUtil.isSubmissionPreventingProspectUpdate(submissionDetailsToBeChecked);
      case RELINQUISHMENT_PROSPECT ->
          SubmissionFilterUtil.isSubmissionPreventingProspectRelinquishment(submissionDetailsToBeChecked);
      case RELINQUISHMENT_DISCOVERY ->
          SubmissionFilterUtil.isSubmissionPreventingDiscoveryRelinquishment(submissionDetailsToBeChecked);
      case UPDATE_DISCOVERY ->
          SubmissionFilterUtil.isSubmissionPreventingDiscoveryUpdate(submissionDetailsToBeChecked);
      default -> false;
    };
  }

  /**
   * Gets the discoveries linked to a prospect.
   */
  private List<Resource> getLinkedDiscoveries(Resource prospect) {
    var resourceLinks = resourceLinkService.getResourceLinksByProspect(prospect);
    var linkedDiscoveries = new ArrayList<Resource>();
    resourceLinks.forEach(resourceLink ->
        linkedDiscoveries.add(resourceLink.getDiscoveryResource())
    );

    return linkedDiscoveries;
  }
}
