package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class SubmissionSummaryForm implements SubmissionForm {

  private Boolean completeWithoutMandatoryInformation;

  public Boolean getCompleteWithoutMandatoryInformation() {
    return completeWithoutMandatoryInformation;
  }

  public void setCompleteWithoutMandatoryInformation(Boolean completeWithoutMandatoryInformation) {
    this.completeWithoutMandatoryInformation = completeWithoutMandatoryInformation;
  }
}
