package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import uk.co.nstauthority.hydrocarbonmaturation.numberinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class HydrocarbonCaseForm implements SubmissionForm {

  private Boolean oilOnlyCaseType;
  private DecimalInput oilOnlyProbability;
  private String oilOnlyComments;
  private Boolean gasOnlyCaseType;
  private DecimalInput gasOnlyProbability;
  private String gasOnlyComments;
  private Boolean oilAndGasCaseType;
  private DecimalInput oilAndGasProbability;
  private String oilAndGasComments;
  private DecimalInput totalProbability;

  public Boolean getOilOnlyCaseType() {
    return oilOnlyCaseType;
  }

  public void setOilOnlyCaseType(Boolean oilOnlyCaseType) {
    this.oilOnlyCaseType = oilOnlyCaseType;
  }

  public DecimalInput getOilOnlyProbability() {
    return oilOnlyProbability;
  }

  public void setOilOnlyProbability(DecimalInput oilOnlyProbability) {
    this.oilOnlyProbability = oilOnlyProbability;
  }

  public String getOilOnlyComments() {
    return oilOnlyComments;
  }

  public void setOilOnlyComments(String oilOnlyComments) {
    this.oilOnlyComments = oilOnlyComments;
  }

  public Boolean getGasOnlyCaseType() {
    return gasOnlyCaseType;
  }

  public void setGasOnlyCaseType(Boolean gasOnlyCaseType) {
    this.gasOnlyCaseType = gasOnlyCaseType;
  }

  public DecimalInput getGasOnlyProbability() {
    return gasOnlyProbability;
  }

  public void setGasOnlyProbability(DecimalInput gasOnlyProbability) {
    this.gasOnlyProbability = gasOnlyProbability;
  }

  public String getGasOnlyComments() {
    return gasOnlyComments;
  }

  public void setGasOnlyComments(String gasOnlyComments) {
    this.gasOnlyComments = gasOnlyComments;
  }

  public Boolean getOilAndGasCaseType() {
    return oilAndGasCaseType;
  }

  public void setOilAndGasCaseType(Boolean oilAndGasCaseType) {
    this.oilAndGasCaseType = oilAndGasCaseType;
  }

  public DecimalInput getOilAndGasProbability() {
    return oilAndGasProbability;
  }

  public void setOilAndGasProbability(DecimalInput oilAndGasProbability) {
    this.oilAndGasProbability = oilAndGasProbability;
  }

  public String getOilAndGasComments() {
    return oilAndGasComments;
  }

  public void setOilAndGasComments(String oilAndGasComments) {
    this.oilAndGasComments = oilAndGasComments;
  }

  public DecimalInput getTotalProbability() {
    return totalProbability;
  }

  public void setTotalProbability(DecimalInput totalProbability) {
    this.totalProbability = totalProbability;
  }
}
