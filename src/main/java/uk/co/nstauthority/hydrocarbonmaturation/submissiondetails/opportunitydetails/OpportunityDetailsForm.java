package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class OpportunityDetailsForm implements SubmissionForm {
  //TODO HM-129
  private OpportunityType opportunityType;
  private String otherTypeOfOpportunity;
  private String opportunityComments;
  private String keyEvaluationWorkCarriedOut;
  private String keyWorkRequiredForFurtherMaturation;

  private List<FileUploadDetail> mapOfOpportunity;

  public OpportunityType getOpportunityType() {
    return opportunityType;
  }

  public void setOpportunityType(
      OpportunityType opportunityType) {
    this.opportunityType = opportunityType;
  }

  public String getOtherTypeOfOpportunity() {
    return otherTypeOfOpportunity;
  }

  public void setOtherTypeOfOpportunity(String otherTypeOfOpportunity) {
    this.otherTypeOfOpportunity = otherTypeOfOpportunity;
  }

  public String getOpportunityComments() {
    return opportunityComments;
  }

  public void setOpportunityComments(String opportunityComments) {
    this.opportunityComments = opportunityComments;
  }

  public String getKeyEvaluationWorkCarriedOut() {
    return keyEvaluationWorkCarriedOut;
  }

  public void setKeyEvaluationWorkCarriedOut(String keyEvaluationWorkCarriedOut) {
    this.keyEvaluationWorkCarriedOut = keyEvaluationWorkCarriedOut;
  }

  public String getKeyWorkRequiredForFurtherMaturation() {
    return keyWorkRequiredForFurtherMaturation;
  }

  public void setKeyWorkRequiredForFurtherMaturation(String keyWorkRequiredForFurtherMaturation) {
    this.keyWorkRequiredForFurtherMaturation = keyWorkRequiredForFurtherMaturation;
  }

  public List<FileUploadDetail> getMapOfOpportunity() {
    return mapOfOpportunity;
  }

  public void setMapOfOpportunity(List<FileUploadDetail> mapOfOpportunity) {
    this.mapOfOpportunity = mapOfOpportunity;
  }
}
