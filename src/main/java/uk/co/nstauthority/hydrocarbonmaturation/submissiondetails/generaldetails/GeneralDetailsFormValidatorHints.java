package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import jakarta.annotation.Nullable;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class GeneralDetailsFormValidatorHints extends SubmissionFormValidatorHints {

  private final GeneralDetail latestApprovedGeneralDetail;
  private final LicensePresent licensePresent;
  private final OnshoreOffshore onshoreOffshore;

  public GeneralDetailsFormValidatorHints(
      ValidationType validationType,
      SubmissionDetail submissionDetail,
      @Nullable GeneralDetail latestApprovedGeneralDetail,
      LicensePresent licensePresent,
      OnshoreOffshore onshoreOffshore) {
    super(validationType, submissionDetail);
    this.latestApprovedGeneralDetail = latestApprovedGeneralDetail;
    this.licensePresent = licensePresent;
    this.onshoreOffshore = onshoreOffshore;
  }

  public GeneralDetail getLatestApprovedGeneralDetail() {
    return latestApprovedGeneralDetail;
  }

  public LicensePresent getLicencePresent() {
    return licensePresent;
  }

  public OnshoreOffshore getOffshore() {
    return onshoreOffshore;
  }
}
