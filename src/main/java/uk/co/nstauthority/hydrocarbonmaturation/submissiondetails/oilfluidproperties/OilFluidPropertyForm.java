package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class OilFluidPropertyForm implements SubmissionForm {

  private DecimalInput oilGravity = new DecimalInput();
  private DecimalInput oilViscosity = new DecimalInput();
  private DecimalInput oilSulphur = new DecimalInput();
  private DecimalInput oilWax = new DecimalInput();
  private DecimalInput oilOtherWaxAppearanceTemperature = new DecimalInput();

  public DecimalInput getOilGravity() {
    return oilGravity;
  }

  public void setOilGravity(DecimalInput oilGravity) {
    this.oilGravity = oilGravity;
  }

  public DecimalInput getOilViscosity() {
    return oilViscosity;
  }

  public void setOilViscosity(DecimalInput oilViscosity) {
    this.oilViscosity = oilViscosity;
  }

  public DecimalInput getOilSulphur() {
    return oilSulphur;
  }

  public void setOilSulphur(DecimalInput oilSulphur) {
    this.oilSulphur = oilSulphur;
  }

  public DecimalInput getOilWax() {
    return oilWax;
  }

  public void setOilWax(DecimalInput oilWax) {
    this.oilWax = oilWax;
  }

  public DecimalInput getOilOtherWaxAppearanceTemperature() {
    return oilOtherWaxAppearanceTemperature;
  }

  public void setOilOtherWaxAppearanceTemperature(
      DecimalInput oilOtherWaxAppearanceTemperature) {
    this.oilOtherWaxAppearanceTemperature = oilOtherWaxAppearanceTemperature;
  }
}
