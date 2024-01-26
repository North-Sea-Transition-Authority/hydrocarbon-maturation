package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;

class TrapPropertiesForm implements SubmissionForm {

  private String trapDescription;

  private TrapType trapType;

  private String otherTrapType;

  private CoordinateInputForm coordinateInputForm = new CoordinateInputForm();

  private ReservoirDatum datum;

  private String centreOfInterestQuadrantBlock;

  private ProbabilityDistributionForm depthToCrest = new ProbabilityDistributionForm();

  private ProbabilityDistributionForm depthToSpillPoint = new ProbabilityDistributionForm();

  private ProbabilityDistributionForm productiveArea = new ProbabilityDistributionForm();

  private List<FileUploadForm> prospectDepthMap = new ArrayList<>();

  public ReservoirDatum getDatum() {
    return datum;
  }

  public void setDatum(ReservoirDatum datum) {
    this.datum = datum;
  }

  public String getCentreOfInterestQuadrantBlock() {
    return centreOfInterestQuadrantBlock;
  }

  public void setCentreOfInterestQuadrantBlock(String centreOfInterestQuadrantBlock) {
    this.centreOfInterestQuadrantBlock = centreOfInterestQuadrantBlock;
  }

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

  public ProbabilityDistributionForm getDepthToCrest() {
    return depthToCrest;
  }

  public void setDepthToCrest(
      ProbabilityDistributionForm depthToCrest) {
    this.depthToCrest = depthToCrest;
  }

  public ProbabilityDistributionForm getDepthToSpillPoint() {
    return depthToSpillPoint;
  }

  public void setDepthToSpillPoint(
      ProbabilityDistributionForm depthToSpillPoint) {
    this.depthToSpillPoint = depthToSpillPoint;
  }

  public ProbabilityDistributionForm getProductiveArea() {
    return productiveArea;
  }

  public void setProductiveArea(
      ProbabilityDistributionForm productiveArea) {
    this.productiveArea = productiveArea;
  }

  public List<FileUploadForm> getProspectDepthMap() {
    return prospectDepthMap;
  }

  public void setProspectDepthMap(
      List<FileUploadForm> prospectDepthMap) {
    this.prospectDepthMap = prospectDepthMap;
  }

  public CoordinateInputForm getCoordinateInputForm() {
    return coordinateInputForm;
  }

  public void setCoordinateInputForm(
      CoordinateInputForm coordinateInputForm) {
    this.coordinateInputForm = coordinateInputForm;
  }
}
