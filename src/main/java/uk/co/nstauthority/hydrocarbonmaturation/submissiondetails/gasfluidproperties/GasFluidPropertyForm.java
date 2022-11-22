package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class GasFluidPropertyForm implements SubmissionForm {

  private DecimalInput gasSpecificGravity = new DecimalInput();
  private DecimalInput gasH2s = new DecimalInput();
  private DecimalInput gasCo2 = new DecimalInput();
  private DecimalInput gasN2 = new DecimalInput();
  private Boolean condensatePresent;
  private DecimalInput condensateGravity = new DecimalInput();
  private DecimalInput condensateViscosity = new DecimalInput();
  private DecimalInput condensateSulphur = new DecimalInput();
  private DecimalInput condensateWax = new DecimalInput();
  private DecimalInput condensateOtherInclWax = new DecimalInput();
  private String adviseOtherGasesConcentration;

  public DecimalInput getGasSpecificGravity() {
    return gasSpecificGravity;
  }

  public void setGasSpecificGravity(DecimalInput gasSpecificGravity) {
    this.gasSpecificGravity = gasSpecificGravity;
  }

  public DecimalInput getGasH2s() {
    return gasH2s;
  }

  public void setGasH2s(DecimalInput gasH2s) {
    this.gasH2s = gasH2s;
  }

  public DecimalInput getGasCo2() {
    return gasCo2;
  }

  public void setGasCo2(DecimalInput gasCo2) {
    this.gasCo2 = gasCo2;
  }

  public DecimalInput getGasN2() {
    return gasN2;
  }

  public void setGasN2(DecimalInput gasN2) {
    this.gasN2 = gasN2;
  }

  public Boolean getCondensatePresent() {
    return condensatePresent;
  }

  public void setCondensatePresent(Boolean condensatePresent) {
    this.condensatePresent = condensatePresent;
  }

  public DecimalInput getCondensateGravity() {
    return condensateGravity;
  }

  public void setCondensateGravity(DecimalInput condensateGravity) {
    this.condensateGravity = condensateGravity;
  }

  public DecimalInput getCondensateViscosity() {
    return condensateViscosity;
  }

  public void setCondensateViscosity(DecimalInput condensateViscosity) {
    this.condensateViscosity = condensateViscosity;
  }

  public DecimalInput getCondensateSulphur() {
    return condensateSulphur;
  }

  public void setCondensateSulphur(DecimalInput condensateSulphur) {
    this.condensateSulphur = condensateSulphur;
  }

  public DecimalInput getCondensateWax() {
    return condensateWax;
  }

  public void setCondensateWax(DecimalInput condensateWax) {
    this.condensateWax = condensateWax;
  }

  public DecimalInput getCondensateOtherInclWax() {
    return condensateOtherInclWax;
  }

  public void setCondensateOtherInclWax(
      DecimalInput condensateOtherInclWax) {
    this.condensateOtherInclWax = condensateOtherInclWax;
  }

  public String getAdviseOtherGasesConcentration() {
    return adviseOtherGasesConcentration;
  }

  public void setAdviseOtherGasesConcentration(String adviseOtherGasesConcentration) {
    this.adviseOtherGasesConcentration = adviseOtherGasesConcentration;
  }
}
