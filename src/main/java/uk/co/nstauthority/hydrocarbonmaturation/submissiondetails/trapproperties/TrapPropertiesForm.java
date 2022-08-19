package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class TrapPropertiesForm extends SubmissionForm {

  private String trapDescription;
  //TODO HM-129
  private TrapType trapType;
  private String otherTrapType;
  private CoordinateInput latitudeCoordinates = new CoordinateInput();
  private CoordinateInput longitudeCoordinates = new CoordinateInput();
  //TODO HM-129
  private Hemisphere longitudeHemisphere;

  public String getTrapDescription() {
    return trapDescription;
  }

  public void setTrapDescription(String trapDescription) {
    this.trapDescription = trapDescription;
  }

  public TrapType getTrapType() {
    return trapType;
  }

  public void setTrapType(TrapType trapType) {
    this.trapType = trapType;
  }

  public String getOtherTrapType() {
    return otherTrapType;
  }

  public void setOtherTrapType(String otherTrapType) {
    this.otherTrapType = otherTrapType;
  }

  public CoordinateInput getLatitudeCoordinates() {
    return latitudeCoordinates;
  }

  public void setLatitudeCoordinates(CoordinateInput latitudeCoordinates) {
    this.latitudeCoordinates = latitudeCoordinates;
  }

  public CoordinateInput getLongitudeCoordinates() {
    return longitudeCoordinates;
  }

  public void setLongitudeCoordinates(CoordinateInput longitudeCoordinates) {
    this.longitudeCoordinates = longitudeCoordinates;
  }

  public Hemisphere getLongitudeHemisphere() {
    return longitudeHemisphere;
  }

  public void setLongitudeHemisphere(Hemisphere longitudeHemisphere) {
    this.longitudeHemisphere = longitudeHemisphere;
  }
}
