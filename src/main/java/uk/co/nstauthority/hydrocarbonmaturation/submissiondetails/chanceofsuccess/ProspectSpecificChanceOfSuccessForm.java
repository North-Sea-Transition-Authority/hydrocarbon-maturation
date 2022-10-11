package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

public class ProspectSpecificChanceOfSuccessForm {

  private ChanceOfSuccessDetailsForm chanceOfSuccessDetailsForm;
  private DecimalInput trapPercentage;
  private String trapComments;
  private DecimalInput migrationPercentage;
  private String migrationComments;

  public ChanceOfSuccessDetailsForm getChanceOfSuccessDetailsForm() {
    return chanceOfSuccessDetailsForm;
  }

  public void setChanceOfSuccessDetailsForm(
      ChanceOfSuccessDetailsForm chanceOfSuccessDetailsForm) {
    this.chanceOfSuccessDetailsForm = chanceOfSuccessDetailsForm;
  }

  public DecimalInput getTrapPercentage() {
    return trapPercentage;
  }

  public void setTrapPercentage(DecimalInput trapPercentage) {
    this.trapPercentage = trapPercentage;
  }

  public String getTrapComments() {
    return trapComments;
  }

  public void setTrapComments(String trapComments) {
    this.trapComments = trapComments;
  }

  public DecimalInput getMigrationPercentage() {
    return migrationPercentage;
  }

  public void setMigrationPercentage(DecimalInput migrationPercentage) {
    this.migrationPercentage = migrationPercentage;
  }

  public String getMigrationComments() {
    return migrationComments;
  }

  public void setMigrationComments(String migrationComments) {
    this.migrationComments = migrationComments;
  }
}
