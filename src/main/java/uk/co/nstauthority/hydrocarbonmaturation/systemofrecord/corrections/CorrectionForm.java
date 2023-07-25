package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import uk.co.nstauthority.hydrocarbonmaturation.mvc.ApplyFormErrorOrdering;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ErrorSummaryDisplayOrder;

@ApplyFormErrorOrdering
class CorrectionForm {

  @ErrorSummaryDisplayOrder(1)
  private CorrectionType correctionType;

  @ErrorSummaryDisplayOrder(2)
  private String submissionIdForMissingUpdate;

  @ErrorSummaryDisplayOrder(3)
  private String missingUpdateDay;

  @ErrorSummaryDisplayOrder(4)
  private String missingUpdateMonth;

  @ErrorSummaryDisplayOrder(5)
  private String missingUpdateYear;

  @ErrorSummaryDisplayOrder(6)
  private String submissionIdForCorrectingInformation;

  @ErrorSummaryDisplayOrder(7)
  private String submissionIdForChangingApprovedDate;

  @ErrorSummaryDisplayOrder(8)
  private String approvalDateChangeDay;

  @ErrorSummaryDisplayOrder(9)
  private String approvalDateChangeMonth;

  @ErrorSummaryDisplayOrder(10)
  private String approvalDateChangeYear;

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
