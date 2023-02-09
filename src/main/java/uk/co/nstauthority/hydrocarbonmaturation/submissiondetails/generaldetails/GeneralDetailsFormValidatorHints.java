package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import javax.annotation.Nullable;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class GeneralDetailsFormValidatorHints extends SubmissionFormValidatorHints {

  private final GeneralDetail latestApprovedGeneralDetail;
  private final boolean isLicencePresent;

  public GeneralDetailsFormValidatorHints(
      ValidationType validationType,
      SubmissionDetail submissionDetail,
      @Nullable GeneralDetail latestApprovedGeneralDetail,
      boolean isLicencePresent) {
    super(validationType, submissionDetail);
    this.latestApprovedGeneralDetail = latestApprovedGeneralDetail;
    this.isLicencePresent = isLicencePresent;
  }

  public GeneralDetail getLatestApprovedGeneralDetail() {
    return latestApprovedGeneralDetail;
  }

  public boolean getLicencePresent() {
    return isLicencePresent;
  }
}
