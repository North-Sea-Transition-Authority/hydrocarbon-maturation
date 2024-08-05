package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.ArrayList;
import java.util.List;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class OpportunityDetailsForm implements SubmissionForm {
  private OpportunityType opportunityType;
  private String otherTypeOfOpportunity;
  private String opportunityComments;
  private String keyEvaluationWorkCarriedOut;
  private String keyWorkRequiredForFurtherMaturation;
  private List<UploadedFileForm> mapOfOpportunity = new ArrayList<>();
  private List<UploadedFileForm> geologicalCrossSection = new ArrayList<>();
  private List<UploadedFileForm> representativeSeismicSection = new ArrayList<>();

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

  public List<UploadedFileForm> getMapOfOpportunity() {
    return mapOfOpportunity;
  }

  public void setMapOfOpportunity(List<UploadedFileForm> mapOfOpportunity) {
    this.mapOfOpportunity = mapOfOpportunity;
  }

  public List<UploadedFileForm> getGeologicalCrossSection() {
    return geologicalCrossSection;
  }

  public void setGeologicalCrossSection(
      List<UploadedFileForm> geologicalCrossSection) {
    this.geologicalCrossSection = geologicalCrossSection;
  }

  public List<UploadedFileForm> getRepresentativeSeismicSection() {
    return representativeSeismicSection;
  }

  public void setRepresentativeSeismicSection(
      List<UploadedFileForm> representativeSeismicSection) {
    this.representativeSeismicSection = representativeSeismicSection;
  }
}
