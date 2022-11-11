package uk.co.nstauthority.hydrocarbonmaturation.coordinateinput;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

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

  public static CoordinateInput createCoordinateInputForForm(BigDecimal degrees, BigDecimal minutes, BigDecimal seconds) {
    var coordinateInput = new CoordinateInput();

    var degreesCoordinate = degrees == null ? new DecimalInput() : new DecimalInput(degrees.toString());
    var minutesCoordinate = minutes == null ? new DecimalInput() : new DecimalInput(minutes.toString());
    var secondsCoordinate = seconds == null ? new DecimalInput() : new DecimalInput(seconds.toString());

    coordinateInput.setDegrees(degreesCoordinate);
    coordinateInput.setMinutes(minutesCoordinate);
    coordinateInput.setSeconds(secondsCoordinate);

    return coordinateInput;
  }
}
