package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class ChanceOfSuccessForm implements SubmissionForm {

  private PlayChanceOfSuccessDetailsForm playChanceOfSuccess;
  private ProspectSpecificChanceOfSuccessForm prospectSpecificChanceOfSuccess;
  private Boolean geophysicallyModified;
  private DecimalInput geophysicallyModifiedPercentage = new DecimalInput();
  private String geophysicallyModifiedComments;
  private Boolean relatedToSpecificWellLocation;
  private DecimalInput wellModifiedPercentage = new DecimalInput();
  private String wellModifiedComments;

  public PlayChanceOfSuccessDetailsForm getPlayChanceOfSuccess() {
    return playChanceOfSuccess;
  }

  public void setPlayChanceOfSuccess(
      PlayChanceOfSuccessDetailsForm playChanceOfSuccess) {
    this.playChanceOfSuccess = playChanceOfSuccess;
  }

  public ProspectSpecificChanceOfSuccessForm getProspectSpecificChanceOfSuccess() {
    return prospectSpecificChanceOfSuccess;
  }

  public void setProspectSpecificChanceOfSuccess(
      ProspectSpecificChanceOfSuccessForm prospectSpecificChanceOfSuccess) {
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
