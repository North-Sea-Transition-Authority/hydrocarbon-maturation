package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@Service
class SubmissionFilterService {

  private final SubmissionDetailService submissionDetailService;

  @Autowired
  SubmissionFilterService(SubmissionDetailService submissionDetailService) {
    this.submissionDetailService = submissionDetailService;
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
      case RELINQUISHMENT_DISCOVERY ->
          SubmissionFilterUtil.isSubmissionPreventingDiscoveryRelinquishment(submissionDetailsToBeChecked);
      case UPDATE_DISCOVERY ->
          SubmissionFilterUtil.isSubmissionPreventingDiscoveryUpdate(submissionDetailsToBeChecked);
      default -> false;
    };
  }
}
