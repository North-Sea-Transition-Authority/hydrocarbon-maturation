package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class SourceAndSealForm extends SubmissionForm {

  private String sourceFormationName;
  private String sourceAgeStage;
  private String sourceLithology;
  private String topSealFormationName;
  private String topSealAgeStage;
  private String topSealLithology;

  public String getSourceFormationName() {
    return sourceFormationName;
  }

  public void setSourceFormationName(String sourceFormationName) {
    this.sourceFormationName = sourceFormationName;
  }

  public String getSourceAgeStage() {
    return sourceAgeStage;
  }

  public void setSourceAgeStage(String sourceAgeStage) {
    this.sourceAgeStage = sourceAgeStage;
  }

  public String getSourceLithology() {
    return sourceLithology;
  }

  public void setSourceLithology(String sourceLithology) {
    this.sourceLithology = sourceLithology;
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

  public void setTopSealAgeStage(String topSealAgeStage) {
    this.topSealAgeStage = topSealAgeStage;
  }

  public String getTopSealLithology() {
    return topSealLithology;
  }

  public void setTopSealLithology(String topSealLithology) {
    this.topSealLithology = topSealLithology;
  }
}
