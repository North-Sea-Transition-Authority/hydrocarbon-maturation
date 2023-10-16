package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class GeneralFluidPropertyForm implements SubmissionForm {

  private DecimalInput datumDepth = new DecimalInput();

  private DecimalInput temperatureAtDatumDepth = new DecimalInput();

  private DecimalInput initialTemperature = new DecimalInput();

  private DecimalInput pressureAtDatumDepth = new DecimalInput();

  private DecimalInput initialPressure = new DecimalInput();

  private DecimalInput saturationPressureAtDatumDepth = new DecimalInput();

  private DecimalInput waterSalinity = new DecimalInput();

  public DecimalInput getDatumDepth() {
    return datumDepth;
  }

  public void setDatumDepth(DecimalInput datumDepth) {
    this.datumDepth = datumDepth;
  }

  public DecimalInput getTemperatureAtDatumDepth() {
    return temperatureAtDatumDepth;
  }

  public void setTemperatureAtDatumDepth(
      DecimalInput temperatureAtDatumDepth) {
    this.temperatureAtDatumDepth = temperatureAtDatumDepth;
  }

  public DecimalInput getInitialTemperature() {
    return initialTemperature;
  }

  public void setInitialTemperature(DecimalInput initialTemperature) {
    this.initialTemperature = initialTemperature;
  }

  public DecimalInput getPressureAtDatumDepth() {
    return pressureAtDatumDepth;
  }

  public void setPressureAtDatumDepth(DecimalInput pressureAtDatumDepth) {
    this.pressureAtDatumDepth = pressureAtDatumDepth;
  }

  public DecimalInput getInitialPressure() {
    return initialPressure;
  }

  public void setInitialPressure(DecimalInput initialPressure) {
    this.initialPressure = initialPressure;
  }

  public DecimalInput getSaturationPressureAtDatumDepth() {
    return saturationPressureAtDatumDepth;
  }

  public void setSaturationPressureAtDatumDepth(
      DecimalInput saturationPressureAtDatumDepth) {
    this.saturationPressureAtDatumDepth = saturationPressureAtDatumDepth;
  }

  public DecimalInput getWaterSalinity() {
    return waterSalinity;
  }

  public void setWaterSalinity(DecimalInput waterSalinity) {
    this.waterSalinity = waterSalinity;
  }
}
