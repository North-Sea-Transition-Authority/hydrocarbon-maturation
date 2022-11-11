package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class DryWellDetailsForm implements SubmissionForm {

  private String dryWell;
  private CoordinateInputForm coordinateInputForm = new CoordinateInputForm();
  private ReservoirDatum reservoirDatum;
  private String totalDepthFormation;
  private String totalDepthFormationComments;
  private String dataAcquisitionProgramme;
  private String geophysicalUpdate;
  private DryHoleType dryHoleType;
  private String dryHoleDescription;
  private String dryHoleScope;
  private String dryHoleComments;
  private DecimalInput dryHoleCost = new DecimalInput();

  public String getTotalDepthFormationComments() {
    return totalDepthFormationComments;
  }

  public void setTotalDepthFormationComments(String totalDepthFormationComments) {
    this.totalDepthFormationComments = totalDepthFormationComments;
  }

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

  public DryHoleType getDryHoleType() {
    return dryHoleType;
  }

  public void setDryHoleType(
      DryHoleType dryHoleType) {
    this.dryHoleType = dryHoleType;
  }

  public String getDryHoleDescription() {
    return dryHoleDescription;
  }

  public void setDryHoleDescription(String dryHoleDescription) {
    this.dryHoleDescription = dryHoleDescription;
  }

  public String getDryHoleScope() {
    return dryHoleScope;
  }

  public void setDryHoleScope(String dryHoleScope) {
    this.dryHoleScope = dryHoleScope;
  }

  public String getDryHoleComments() {
    return dryHoleComments;
  }

  public void setDryHoleComments(String dryHoleComments) {
    this.dryHoleComments = dryHoleComments;
  }

  public DecimalInput getDryHoleCost() {
    return dryHoleCost;
  }

  public void setDryHoleCost(DecimalInput dryHoleCost) {
    this.dryHoleCost = dryHoleCost;
  }
}
