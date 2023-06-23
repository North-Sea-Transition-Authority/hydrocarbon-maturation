package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

class CorrectionForm {

  private CorrectionType correctionType;
  private String submissionIdForCorrectingInformation;

  public CorrectionType getCorrectionType() {
    return correctionType;
  }

  public void setCorrectionType(
      CorrectionType correctionType) {
    this.correctionType = correctionType;
  }

  public String getSubmissionIdForCorrectingInformation() {
    return submissionIdForCorrectingInformation;
  }

  public void setSubmissionIdForCorrectingInformation(String submissionIdForCorrectingInformation) {
    this.submissionIdForCorrectingInformation = submissionIdForCorrectingInformation;
  }
}
