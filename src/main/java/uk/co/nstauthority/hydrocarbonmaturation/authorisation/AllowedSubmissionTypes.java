package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public enum AllowedSubmissionTypes {
  PROSPECT_TYPES(
      SubmissionType.CREATE_PROSPECT,
      SubmissionType.PROSPECT_UPDATE,
      SubmissionType.RELINQUISHMENT_PROSPECT),
  DISCOVERY_TYPES(
      SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
      SubmissionType.CREATE_DISCOVERY,
      SubmissionType.UPDATE_DISCOVERY,
      SubmissionType.RELINQUISHMENT_DISCOVERY),
  POST_DRILL_DRY_HOLE(
      SubmissionType.POST_DRILL_DRY_HOLE),
  DISCOVERY_AND_POST_DRILL(
      SubmissionType.POST_DRILL_DRY_HOLE,
      SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
      SubmissionType.CREATE_DISCOVERY,
      SubmissionType.UPDATE_DISCOVERY,
      SubmissionType.RELINQUISHMENT_DISCOVERY),
  CREATE_PROSPECT(
      SubmissionType.CREATE_PROSPECT),
  DISCOVERY_AND_PROSPECT(
      SubmissionType.CREATE_PROSPECT,
      SubmissionType.PROSPECT_UPDATE,
      SubmissionType.RELINQUISHMENT_PROSPECT,
      SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
      SubmissionType.CREATE_DISCOVERY,
      SubmissionType.UPDATE_DISCOVERY,
      SubmissionType.RELINQUISHMENT_DISCOVERY),
  NONE()
  ;
  private final SubmissionType[] submissionTypesAllowed;

  AllowedSubmissionTypes(SubmissionType... submissionTypesAllowed) {
    this.submissionTypesAllowed = submissionTypesAllowed;
  }

  public SubmissionType[] getSubmissionTypesAllowed() {
    return submissionTypesAllowed;
  }
}
