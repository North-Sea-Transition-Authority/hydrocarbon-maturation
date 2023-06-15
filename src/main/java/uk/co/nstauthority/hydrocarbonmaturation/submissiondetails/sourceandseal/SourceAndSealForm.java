package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

class SourceAndSealForm implements SubmissionForm {

  private String sourceFormationName;
  private String sourceAgeStage;
  private GroupName sourceGroupName;
  private SourceLithology sourceLithology;
  private String sourceLithologyOther;
  private String topSealFormationName;
  private String topSealAgeStage;
  private GroupName topSealGroupName;
  private TopSealLithology topSealLithology;
  private String topSealLithologyOther;

  public String getSourceFormationName() {
    return sourceFormationName;
  }

  public void setSourceFormationName(String sourceFormationName) {
    this.sourceFormationName = sourceFormationName;
  }

  public String getSourceAgeStage() {
    return sourceAgeStage;
  }

  public void setSourceAgeStage(
      String sourceAgeStage) {
    this.sourceAgeStage = sourceAgeStage;
  }

  public GroupName getSourceGroupName() {
    return sourceGroupName;
  }

  public void setSourceGroupName(
      GroupName sourceGroupName) {
    this.sourceGroupName = sourceGroupName;
  }

  public SourceLithology getSourceLithology() {
    return sourceLithology;
  }

  public void setSourceLithology(SourceLithology sourceLithology) {
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

  public String getTopSealAgeStage() {
    return topSealAgeStage;
  }

  public void setTopSealAgeStage(
      String topSealAgeStage) {
    this.topSealAgeStage = topSealAgeStage;
  }

  public GroupName getTopSealGroupName() {
    return topSealGroupName;
  }

  public void setTopSealGroupName(
      GroupName topSealGroupName) {
    this.topSealGroupName = topSealGroupName;
  }

  public TopSealLithology getTopSealLithology() {
    return topSealLithology;
  }

  public void setTopSealLithology(
      TopSealLithology topSealLithology) {
    this.topSealLithology = topSealLithology;
  }

  public String getTopSealLithologyOther() {
    return topSealLithologyOther;
  }

  public void setTopSealLithologyOther(String topSealLithologyOther) {
    this.topSealLithologyOther = topSealLithologyOther;
  }
}
