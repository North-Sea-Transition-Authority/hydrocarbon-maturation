package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class TrapPropertiesForm implements SubmissionForm {

  private String trapDescription;
  //TODO HM-129
  private TrapType trapType;
  private String otherTrapType;
  private CoordinateInput latitudeCoordinates = new CoordinateInput();
  private CoordinateInput longitudeCoordinates = new CoordinateInput();
  //TODO HM-129
  private Hemisphere longitudeHemisphere;

  private ProbabilityDistributionForm depthToCrest = new ProbabilityDistributionForm();

  private ProbabilityDistributionForm depthToSpillPoint = new ProbabilityDistributionForm();

  private ProbabilityDistributionForm productiveArea = new ProbabilityDistributionForm();

  private List<FileUploadDetail> prospectDepthMap = new ArrayList<>();

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

  public List<FileUploadDetail> getProspectDepthMap() {
    return prospectDepthMap;
  }

  public void setProspectDepthMap(
      List<FileUploadDetail> prospectDepthMap) {
    this.prospectDepthMap = prospectDepthMap;
  }
}
