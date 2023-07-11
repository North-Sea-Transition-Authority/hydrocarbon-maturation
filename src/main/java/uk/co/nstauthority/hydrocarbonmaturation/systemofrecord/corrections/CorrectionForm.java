package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

class CorrectionForm {

  private CorrectionType correctionType;
  private String submissionIdForCorrectingInformation;
  private String submissionIdForChangingApprovedDate;
  private String approvalDateChangeDay;
  private String approvalDateChangeMonth;
  private String approvalDateChangeYear;
  private String submissionIdForMissingUpdate;
  private String missingUpdateDay;
  private String missingUpdateMonth;
  private String missingUpdateYear;

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

  public String getSubmissionIdForChangingApprovedDate() {
    return submissionIdForChangingApprovedDate;
  }

  public void setSubmissionIdForChangingApprovedDate(String submissionIdForChangingApprovedDate) {
    this.submissionIdForChangingApprovedDate = submissionIdForChangingApprovedDate;
  }

  public String getApprovalDateChangeDay() {
    return approvalDateChangeDay;
  }

  public void setApprovalDateChangeDay(String approvalDateChangeDay) {
    this.approvalDateChangeDay = approvalDateChangeDay;
  }

  public String getApprovalDateChangeMonth() {
    return approvalDateChangeMonth;
  }

  public void setApprovalDateChangeMonth(String approvalDateChangeMonth) {
    this.approvalDateChangeMonth = approvalDateChangeMonth;
  }

  public String getApprovalDateChangeYear() {
    return approvalDateChangeYear;
  }

  public void setApprovalDateChangeYear(String approvalDateChangeYear) {
    this.approvalDateChangeYear = approvalDateChangeYear;
  }

  public String getSubmissionIdForMissingUpdate() {
    return submissionIdForMissingUpdate;
  }

  public void setSubmissionIdForMissingUpdate(String submissionIdForMissingUpdate) {
    this.submissionIdForMissingUpdate = submissionIdForMissingUpdate;
  }

  public String getMissingUpdateDay() {
    return missingUpdateDay;
  }

  public void setMissingUpdateDay(String missingUpdateDay) {
    this.missingUpdateDay = missingUpdateDay;
  }

  public String getMissingUpdateMonth() {
    return missingUpdateMonth;
  }

  public void setMissingUpdateMonth(String missingUpdateMonth) {
    this.missingUpdateMonth = missingUpdateMonth;
  }

  public String getMissingUpdateYear() {
    return missingUpdateYear;
  }

  public void setMissingUpdateYear(String missingUpdateYear) {
    this.missingUpdateYear = missingUpdateYear;
  }
}
