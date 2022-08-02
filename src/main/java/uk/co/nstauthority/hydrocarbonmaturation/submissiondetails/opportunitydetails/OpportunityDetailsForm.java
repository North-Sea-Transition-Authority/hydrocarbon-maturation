package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

class OpportunityDetailsForm {

  private OpportunityType opportunityType;
  private String otherTypeOfOpportunity;
  private String opportunityComments;
  private String keyEvaluationWorkCarriedOut;
  private String keyWorkRequiredForFurtherMaturation;

  OpportunityType getOpportunityType() {
    return opportunityType;
  }

  void setOpportunityType(OpportunityType opportunityType) {
    this.opportunityType = opportunityType;
  }

  String getOtherTypeOfOpportunity() {
    return otherTypeOfOpportunity;
  }

  void setOtherTypeOfOpportunity(String otherTypeOfOpportunity) {
    this.otherTypeOfOpportunity = otherTypeOfOpportunity;
  }

  String getOpportunityComments() {
    return opportunityComments;
  }

  void setOpportunityComments(String opportunityComments) {
    this.opportunityComments = opportunityComments;
  }

  String getKeyEvaluationWorkCarriedOut() {
    return keyEvaluationWorkCarriedOut;
  }

  void setKeyEvaluationWorkCarriedOut(String keyEvaluationWorkCarriedOut) {
    this.keyEvaluationWorkCarriedOut = keyEvaluationWorkCarriedOut;
  }

  String getKeyWorkRequiredForFurtherMaturation() {
    return keyWorkRequiredForFurtherMaturation;
  }

  void setKeyWorkRequiredForFurtherMaturation(String keyWorkRequiredForFurtherMaturation) {
    this.keyWorkRequiredForFurtherMaturation = keyWorkRequiredForFurtherMaturation;
  }
}
