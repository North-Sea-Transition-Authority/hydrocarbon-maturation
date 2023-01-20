package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

class SubmissionFilterUtil {

  private SubmissionFilterUtil() {
    throw new IllegalUtilClassInstantiationException(SubmissionFilterUtil.class);
  }

  /**
   * Prospect relinquishment can be started if there are no DRAFT or SUBMITTED submissions on the resource (of any type)
   * Also that there are no discoveries and relinquishment submissions on it in any status.
   * This method returns true when a prospect relinquishment cannot be started based on these rules
   */
  static boolean isSubmissionPreventingProspectRelinquishment(SubmissionDetail submissionDetail) {
    return submissionDetail.isDraftOrSubmittedSubmission()
        || isExtantProspectRelinquishment(submissionDetail)
        || isExtantDiscoverySubmission(submissionDetail);
  }

  /**
   * Prospect update can only be started on a resource if there is no other draft or submitted prospect update on the resource.
   * Also there cannot be a post drill dry hole, a discovery or a relinquishment (in draft or approved) on the resource.
   */
  static boolean isSubmissionPreventingProspectUpdate(SubmissionDetail submissionDetail) {
    return isDraftOrSubmittedUpdateProspect(submissionDetail)
        || isExtantPostDrillDryHoleSubmission(submissionDetail)
        || isExtantDiscoverySubmission(submissionDetail)
        || isExtantProspectRelinquishment(submissionDetail);
  }

  /**
   * Dry hole and discovery submissions can only be made if there are no post drill dry hole submissions
   * and when there is no prospect update submission with draft or submitted status and that the prospect
   * does not have a relinquishment submission with draft or approved status.
   */
  static boolean isSubmissionPreventingDryHoleOrDiscovery(SubmissionDetail submissionDetail) {
    return isExtantPostDrillDryHoleSubmission(submissionDetail)
        || isDraftOrSubmittedUpdateProspect(submissionDetail)
        || isExtantProspectRelinquishment(submissionDetail);
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
    return submissionDetail.isDiscoveryFromProspect() && !submissionDetail.isDeletedSubmission();
  }

  static boolean isDraftOrSubmittedUpdateDiscovery(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType() == SubmissionType.UPDATE_DISCOVERY
        && submissionDetail.isDraftOrSubmittedSubmission();
  }

  static boolean isExtantDiscoveryRelinquishment(SubmissionDetail submissionDetail) {
    return submissionDetail.isDiscoveryRelinquishment()
        && submissionDetail.isDraftOrApprovedSubmission();
  }

  static boolean isExtantProspectRelinquishment(SubmissionDetail submissionDetail) {
    return submissionDetail.isProspectRelinquishment()
        && submissionDetail.isDraftOrApprovedSubmission();
  }

  static boolean isDraftOrSubmittedUpdateProspect(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType() == SubmissionType.PROSPECT_UPDATE
        && submissionDetail.isDraftOrSubmittedSubmission();
  }

  static boolean isExtantPostDrillDryHoleSubmission(SubmissionDetail submissionDetail) {
    return submissionDetail.isDryWellSubmission() && !submissionDetail.isDeletedSubmission();
  }
}
