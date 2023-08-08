package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;

class GeneralReservoirInformationForm implements SubmissionForm {
  private String reservoirName;

  private HydrocarbonsDiscovered hydrocarbonsDiscovered;

  public String getReservoirName() {
    return reservoirName;
  }

  public void setReservoirName(String reservoirName) {
    this.reservoirName = reservoirName;
  }

  public HydrocarbonsDiscovered getHydrocarbonsDiscovered() {
    return hydrocarbonsDiscovered;
  }

  public void setHydrocarbonsDiscovered(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    this.hydrocarbonsDiscovered = hydrocarbonsDiscovered;
  }
}