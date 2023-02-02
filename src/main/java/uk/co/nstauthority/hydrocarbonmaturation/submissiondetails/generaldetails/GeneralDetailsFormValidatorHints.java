package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Optional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class GeneralDetailsFormValidatorHints extends SubmissionFormValidatorHints {

  private final Optional<GeneralDetail> latestApprovedGeneralDetail;
  private final boolean isLicencePresent;

  public GeneralDetailsFormValidatorHints(
      ValidationType validationType,
      SubmissionDetail submissionDetail,
      Optional<GeneralDetail> latestApprovedGeneralDetail,
      boolean isLicencePresent) {
    super(validationType, submissionDetail);
    this.latestApprovedGeneralDetail = latestApprovedGeneralDetail;
    this.isLicencePresent = isLicencePresent;
  }

  public Optional<GeneralDetail> getLatestApprovedGeneralDetail() {
    return latestApprovedGeneralDetail;
  }

  public boolean getLicencePresent() {
    return isLicencePresent;
  }
}
