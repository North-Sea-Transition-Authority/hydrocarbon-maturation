package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public enum SecuritySubmissionTypes {
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
  CREATE_RESOURCE_SUBMISSIONS(
      SubmissionType.CREATE_PROSPECT,
      SubmissionType.CREATE_DISCOVERY,
      SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT),
  DISCOVERY_AND_PROSPECT(
      SubmissionType.CREATE_PROSPECT,
      SubmissionType.PROSPECT_UPDATE,
      SubmissionType.RELINQUISHMENT_PROSPECT,
      SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
      SubmissionType.CREATE_DISCOVERY,
      SubmissionType.UPDATE_DISCOVERY,
      SubmissionType.RELINQUISHMENT_DISCOVERY),
  DISCOVERY_AND_FIELD(
      SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
      SubmissionType.CREATE_DISCOVERY,
      SubmissionType.UPDATE_DISCOVERY,
      SubmissionType.RELINQUISHMENT_DISCOVERY,
      SubmissionType.CREATE_CSR,
      SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_COP,
      SubmissionType.CREATE_FDPA
  ),
  FIELD_SUBMISSIONS(
      SubmissionType.CREATE_COP,
      SubmissionType.CREATE_CSR,
      SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_FDPA
  ),
  NONE()
  ;
  private final SubmissionType[] submissionTypesAllowed;

  SecuritySubmissionTypes(SubmissionType... submissionTypesAllowed) {
    this.submissionTypesAllowed = submissionTypesAllowed;
  }

  public SubmissionType[] getSubmissionTypesAllowed() {
    return submissionTypesAllowed;
  }
}
