package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class GasFluidPropertyForm implements SubmissionForm {

  private DecimalInput gasSpecificGravity = new DecimalInput();
  private DecimalInput gasH2s = new DecimalInput();
  private DecimalInput gasCo2 = new DecimalInput();
  private DecimalInput gasN2 = new DecimalInput();
  private Boolean condensatePresent;
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

  public String getAdviseOtherGasesConcentration() {
    return adviseOtherGasesConcentration;
  }

  public void setAdviseOtherGasesConcentration(String adviseOtherGasesConcentration) {
    this.adviseOtherGasesConcentration = adviseOtherGasesConcentration;
  }
}
