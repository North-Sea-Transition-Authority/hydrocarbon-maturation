package uk.co.nstauthority.hydrocarbonmaturation.coordinateinput;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

public class CoordinateInputForm {

  private CoordinateInput latitudeCoordinates = new CoordinateInput();
  private CoordinateInput longitudeCoordinates = new CoordinateInput();
  private Hemisphere longitudeHemisphere;

  public CoordinateInput getLatitudeCoordinates() {
    return latitudeCoordinates;
  }

  public void setLatitudeCoordinates(
      CoordinateInput latitudeCoordinates) {
    this.latitudeCoordinates = latitudeCoordinates;
  }

  public CoordinateInput getLongitudeCoordinates() {
    return longitudeCoordinates;
  }

  public void setLongitudeCoordinates(
      CoordinateInput longitudeCoordinates) {
    this.longitudeCoordinates = longitudeCoordinates;
  }

  public Hemisphere getLongitudeHemisphere() {
    return longitudeHemisphere;
  }

  public void setLongitudeHemisphere(
      Hemisphere longitudeHemisphere) {
    this.longitudeHemisphere = longitudeHemisphere;
  }
}
