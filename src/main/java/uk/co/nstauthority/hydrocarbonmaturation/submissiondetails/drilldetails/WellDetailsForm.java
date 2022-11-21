package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails;

import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;

public class WellDetailsForm {

  private String dryWell;
  private CoordinateInputForm coordinateInputForm = new CoordinateInputForm();
  private ReservoirDatum reservoirDatum;
  private String totalDepthFormation;
  private String totalDepthFormationComments;
  private String dataAcquisitionProgramme;
  private String geophysicalUpdate;

  public String getDryWell() {
    return dryWell;
  }

  public void setDryWell(String dryWell) {
    this.dryWell = dryWell;
  }

  public CoordinateInputForm getCoordinateInputForm() {
    return coordinateInputForm;
  }

  public void setCoordinateInputForm(
      CoordinateInputForm coordinateInputForm) {
    this.coordinateInputForm = coordinateInputForm;
  }

  public ReservoirDatum getReservoirDatum() {
    return reservoirDatum;
  }

  public void setReservoirDatum(
      ReservoirDatum reservoirDatum) {
    this.reservoirDatum = reservoirDatum;
  }

  public String getTotalDepthFormation() {
    return totalDepthFormation;
  }

  public void setTotalDepthFormation(String totalDepthFormation) {
    this.totalDepthFormation = totalDepthFormation;
  }

  public String getTotalDepthFormationComments() {
    return totalDepthFormationComments;
  }

  public void setTotalDepthFormationComments(String totalDepthFormationComments) {
    this.totalDepthFormationComments = totalDepthFormationComments;
  }

  public String getDataAcquisitionProgramme() {
    return dataAcquisitionProgramme;
  }

  public void setDataAcquisitionProgramme(String dataAcquisitionProgramme) {
    this.dataAcquisitionProgramme = dataAcquisitionProgramme;
  }

  public String getGeophysicalUpdate() {
    return geophysicalUpdate;
  }

  public void setGeophysicalUpdate(String geophysicalUpdate) {
    this.geophysicalUpdate = geophysicalUpdate;
  }
}
