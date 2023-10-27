package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

class SourceAndSealForm implements SubmissionForm {

  private String sourceFormationName;
  private AgeSeries sourceAgeStage;
  private GroupName sourceGroupName;
  private StandardLithology sourceLithology;
  private String sourceLithologyOther;
  private String topSealFormationName;
  private AgeSeries topSealAgeStage;
  private GroupName topSealGroupName;
  private StandardLithology topSealLithology;
  private String topSealLithologyOther;

  public String getSourceFormationName() {
    return sourceFormationName;
  }

  public void setSourceFormationName(String sourceFormationName) {
    this.sourceFormationName = sourceFormationName;
  }

  public AgeSeries getSourceAgeStage() {
    return sourceAgeStage;
  }

  public void setSourceAgeStage(
      AgeSeries sourceAgeStage) {
    this.sourceAgeStage = sourceAgeStage;
  }

  public GroupName getSourceGroupName() {
    return sourceGroupName;
  }

  public void setSourceGroupName(
      GroupName sourceGroupName) {
    this.sourceGroupName = sourceGroupName;
  }

  public StandardLithology getSourceLithology() {
    return sourceLithology;
  }

  public void setSourceLithology(StandardLithology sourceLithology) {
    this.sourceLithology = sourceLithology;
  }

  public String getSourceLithologyOther() {
    return sourceLithologyOther;
  }

  public void setSourceLithologyOther(String sourceLithologyOther) {
    this.sourceLithologyOther = sourceLithologyOther;
  }

  public String getTopSealFormationName() {
    return topSealFormationName;
  }

  public void setTopSealFormationName(String topSealFormationName) {
    this.topSealFormationName = topSealFormationName;
  }

  public AgeSeries getTopSealAgeStage() {
    return topSealAgeStage;
  }

  public void setTopSealAgeStage(
      AgeSeries topSealAgeStage) {
    this.topSealAgeStage = topSealAgeStage;
  }

  public GroupName getTopSealGroupName() {
    return topSealGroupName;
  }

  public void setTopSealGroupName(
      GroupName topSealGroupName) {
    this.topSealGroupName = topSealGroupName;
  }

  public StandardLithology getTopSealLithology() {
    return topSealLithology;
  }

  public void setTopSealLithology(
      StandardLithology topSealLithology) {
    this.topSealLithology = topSealLithology;
  }

  public String getTopSealLithologyOther() {
    return topSealLithologyOther;
  }

  public void setTopSealLithologyOther(String topSealLithologyOther) {
    this.topSealLithologyOther = topSealLithologyOther;
  }
}
