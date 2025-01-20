package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

class SubmissionFilterUtil {

  private SubmissionFilterUtil() {
    throw new IllegalUtilClassInstantiationException(SubmissionFilterUtil.class);
  }

  /**
   * Discovery update can only be started on a discovery without another discovery update or discovery relinquishment
   * submission in draft and submitted status.
   */
  static boolean isSubmissionPreventingDiscoveryUpdate(SubmissionDetail submissionDetail) {
    return isDraftOrSubmittedUpdateDiscovery(submissionDetail)
        || isExtantDiscoveryRelinquishment(submissionDetail);
  }

  /**
   * Discovery relinquishment can be started if there are no DRAFT or SUBMITTED submissions on the resource (of any type)
   * It also cannot be started if there is another relinquishment discovery submission in any status against it.
   * This method returns true when a discovery relinquishment cannot be started based on these rules
   */
  static boolean isSubmissionPreventingDiscoveryRelinquishment(SubmissionDetail submissionDetail) {
    return submissionDetail.isDraftOrSubmittedSubmission()
        || isExtantDiscoveryRelinquishment(submissionDetail);
  }

  /**
   * Ensures the correct resource type appears on search selectors.
   */
  static boolean filterByResourceType(Resource currentResource, ResourceType resourceType) {
    return resourceType == currentResource.getType();
  }

  static boolean isExtantDiscoverySubmission(SubmissionDetail submissionDetail) {
    return submissionDetail.isDiscoveryFromProspect() && !submissionDetail.isDeletedOrWithdrawnSubmission();
  }

  static boolean isDraftOrSubmittedUpdateDiscovery(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType() == SubmissionType.UPDATE_DISCOVERY
        && submissionDetail.isDraftOrSubmittedSubmission();
  }

  static boolean isExtantDiscoveryRelinquishment(SubmissionDetail submissionDetail) {
    return submissionDetail.isDiscoveryRelinquishment()
        && submissionDetail.isDraftOrApprovedSubmission();
  }
}
