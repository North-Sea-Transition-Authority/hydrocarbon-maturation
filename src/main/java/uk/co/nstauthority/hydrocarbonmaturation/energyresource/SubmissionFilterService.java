package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
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

  /** Checks other submissions on the discovery to determine whether a new submission can be made.
   * @param submission Current latest approved submission being checked
   * @param submissionToBeCreatedType Submission type that is being created
   * @return if the submission should be available to be chosen
   */
  boolean filterDiscoverySubmissionByOtherSubmissions(SubmissionDetail submission,
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
  boolean filterProspectSubmissionByOtherSubmissions(SubmissionDetail submission,
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
