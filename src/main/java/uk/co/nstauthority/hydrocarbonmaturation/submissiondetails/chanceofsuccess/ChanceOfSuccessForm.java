package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class ChanceOfSuccessForm extends SubmissionForm {

  private ChanceOfSuccessDetails playChanceOfSuccess;
  private ProspectSpecificChanceOfSuccess prospectSpecificChanceOfSuccess;
  private Boolean geophysicallyModified;
  private DecimalInput geophysicallyModifiedPercentage;
  private String geophysicallyModifiedComments;
  private Boolean relatedToSpecificWellLocation;
  private DecimalInput wellModifiedPercentage;
  private String wellModifiedComments;

  public ChanceOfSuccessDetails getPlayChanceOfSuccess() {
    return playChanceOfSuccess;
  }

  public void setPlayChanceOfSuccess(
      ChanceOfSuccessDetails playChanceOfSuccess) {
    this.playChanceOfSuccess = playChanceOfSuccess;
  }

  public ProspectSpecificChanceOfSuccess getProspectSpecificChanceOfSuccess() {
    return prospectSpecificChanceOfSuccess;
  }

  public void setProspectSpecificChanceOfSuccess(
      ProspectSpecificChanceOfSuccess prospectSpecificChanceOfSuccess) {
    this.prospectSpecificChanceOfSuccess = prospectSpecificChanceOfSuccess;
  }

  public Boolean getGeophysicallyModified() {
    return geophysicallyModified;
  }

  public void setGeophysicallyModified(Boolean geophysicallyModified) {
    this.geophysicallyModified = geophysicallyModified;
  }

  public DecimalInput getGeophysicallyModifiedPercentage() {
    return geophysicallyModifiedPercentage;
  }

  public void setGeophysicallyModifiedPercentage(
      DecimalInput geophysicallyModifiedPercentage) {
    this.geophysicallyModifiedPercentage = geophysicallyModifiedPercentage;
  }

  public String getGeophysicallyModifiedComments() {
    return geophysicallyModifiedComments;
  }

  public void setGeophysicallyModifiedComments(String geophysicallyModifiedComments) {
    this.geophysicallyModifiedComments = geophysicallyModifiedComments;
  }

  public Boolean getRelatedToSpecificWellLocation() {
    return relatedToSpecificWellLocation;
  }

  public void setRelatedToSpecificWellLocation(Boolean relatedToSpecificWellLocation) {
    this.relatedToSpecificWellLocation = relatedToSpecificWellLocation;
  }

  public DecimalInput getWellModifiedPercentage() {
    return wellModifiedPercentage;
  }

  public void setWellModifiedPercentage(
      DecimalInput wellModifiedPercentage) {
    this.wellModifiedPercentage = wellModifiedPercentage;
  }

  public String getWellModifiedComments() {
    return wellModifiedComments;
  }

  public void setWellModifiedComments(String wellModifiedComments) {
    this.wellModifiedComments = wellModifiedComments;
  }
}
