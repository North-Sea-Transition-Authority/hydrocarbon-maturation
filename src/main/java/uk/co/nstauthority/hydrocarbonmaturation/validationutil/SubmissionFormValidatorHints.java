package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public class SubmissionFormValidatorHints {

  private ValidationType validationType;
  private SubmissionDetail submissionDetail;

  public SubmissionFormValidatorHints(ValidationType validationType, SubmissionDetail submissionDetail) {
    this.validationType = validationType;
    this.submissionDetail = submissionDetail;
  }

  public ValidationType getValidationType() {
    return validationType;
  }

  public void setValidationType(ValidationType validationType) {
    this.validationType = validationType;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }
}

