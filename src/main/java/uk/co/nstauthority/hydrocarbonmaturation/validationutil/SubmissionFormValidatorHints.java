package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public class SubmissionFormValidatorHints {

  private ValidationType validationType;

  private SubmissionType submissionType;

  public SubmissionFormValidatorHints(ValidationType validationType, SubmissionType submissionType) {
    this.validationType = validationType;
    this.submissionType = submissionType;
  }

  public ValidationType getValidationType() {
    return validationType;
  }

  public void setValidationType(ValidationType validationType) {
    this.validationType = validationType;
  }

  public SubmissionType getSubmissionType() {
    return submissionType;
  }

  public void setSubmissionType(SubmissionType submissionType) {
    this.submissionType = submissionType;
  }
}

