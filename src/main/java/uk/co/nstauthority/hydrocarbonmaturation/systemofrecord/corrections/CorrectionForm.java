package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import uk.co.nstauthority.hydrocarbonmaturation.mvc.ApplyFormErrorOrdering;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ErrorSummaryDisplayOrder;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ApplyFormErrorOrdering
class CorrectionForm {

  @ErrorSummaryDisplayOrder(1)
  private CorrectionType correctionType;

  @ErrorSummaryDisplayOrder(2)
  private String submissionDetailIdForMissingUpdate;

  @ErrorSummaryDisplayOrder(3)
  private SubmissionType submissionTypeForMissingUpdate;

  @ErrorSummaryDisplayOrder(4)
  private String missingUpdateDay;

  @ErrorSummaryDisplayOrder(5)
  private String missingUpdateMonth;

  @ErrorSummaryDisplayOrder(6)
  private String missingUpdateYear;

  @ErrorSummaryDisplayOrder(7)
  private String submissionDetailIdForCorrectingInformation;

  @ErrorSummaryDisplayOrder(8)
  private String submissionDetailIdForChangingApprovedDate;

  @ErrorSummaryDisplayOrder(9)
  private String approvalDateChangeDay;

  @ErrorSummaryDisplayOrder(10)
  private String approvalDateChangeMonth;

  @ErrorSummaryDisplayOrder(11)
  private String approvalDateChangeYear;

  @ErrorSummaryDisplayOrder(12)
  private String submissionDetailIdForRemoval;

  public CorrectionType getCorrectionType() {
    return correctionType;
  }

  public void setCorrectionType(
      CorrectionType correctionType) {
    this.correctionType = correctionType;
  }

  public String getSubmissionDetailIdForCorrectingInformation() {
    return submissionDetailIdForCorrectingInformation;
  }

  public void setSubmissionDetailIdForCorrectingInformation(String submissionDetailIdForCorrectingInformation) {
    this.submissionDetailIdForCorrectingInformation = submissionDetailIdForCorrectingInformation;
  }

  public String getSubmissionDetailIdForChangingApprovedDate() {
    return submissionDetailIdForChangingApprovedDate;
  }

  public void setSubmissionDetailIdForChangingApprovedDate(String submissionDetailIdForChangingApprovedDate) {
    this.submissionDetailIdForChangingApprovedDate = submissionDetailIdForChangingApprovedDate;
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

  public String getSubmissionDetailIdForMissingUpdate() {
    return submissionDetailIdForMissingUpdate;
  }

  public void setSubmissionDetailIdForMissingUpdate(String submissionDetailIdForMissingUpdate) {
    this.submissionDetailIdForMissingUpdate = submissionDetailIdForMissingUpdate;
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

  public String getSubmissionDetailIdForRemoval() {
    return submissionDetailIdForRemoval;
  }

  public void setSubmissionDetailIdForRemoval(String submissionDetailIdForRemoval) {
    this.submissionDetailIdForRemoval = submissionDetailIdForRemoval;
  }

  public SubmissionType getSubmissionTypeForMissingUpdate() {
    return submissionTypeForMissingUpdate;
  }

  public void setSubmissionTypeForMissingUpdate(SubmissionType submissionTypeForMissingUpdate) {
    this.submissionTypeForMissingUpdate = submissionTypeForMissingUpdate;
  }
}
