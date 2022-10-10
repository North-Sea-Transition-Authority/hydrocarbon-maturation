package uk.co.nstauthority.hydrocarbonmaturation.coordinateinput;

import uk.co.nstauthority.hydrocarbonmaturation.numberinput.DecimalInput;

public class CoordinateInput {

  private DecimalInput degrees = new DecimalInput();
  private DecimalInput minutes = new DecimalInput();
  private DecimalInput seconds = new DecimalInput();

  public DecimalInput getDegrees() {
    return degrees;
  }

  public void setDegrees(DecimalInput degrees) {
    this.degrees = degrees;
  }

  public DecimalInput getMinutes() {
    return minutes;
  }

  public void setMinutes(DecimalInput minutes) {
    this.minutes = minutes;
  }

  public DecimalInput getSeconds() {
    return seconds;
  }

  public void setSeconds(DecimalInput seconds) {
    this.seconds = seconds;
  }
}
