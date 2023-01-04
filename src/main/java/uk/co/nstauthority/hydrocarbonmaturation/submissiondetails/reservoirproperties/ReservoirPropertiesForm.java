package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class ReservoirPropertiesForm implements SubmissionForm {

  private String formationName;
  private String ageStageOrSeries;
  private String lithology;
  private ProbabilityDistributionForm averageNetPayThickness = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm grossReservoirThickness = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm grossRockVolume = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm percentOfGrvOilBearing = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm netToGross = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm netPorosity = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm depthToHydrocarbonContact = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm depthToOilWaterContact = createCustomProbabilityDistributionForm();
  private ProbabilityDistributionForm depthToGasWaterContact = createCustomProbabilityDistributionForm();
  private ProbabilityDistributionForm depthToGasOilContact = createCustomProbabilityDistributionForm();
  private String reservoirFacies;
  private String reservoirConnectivity;

  public String getFormationName() {
    return formationName;
  }

  public void setFormationName(String formationName) {
    this.formationName = formationName;
  }

  public String getAgeStageOrSeries() {
    return ageStageOrSeries;
  }

  public void setAgeStageOrSeries(String ageStageOrSeries) {
    this.ageStageOrSeries = ageStageOrSeries;
  }

  public String getLithology() {
    return lithology;
  }

  public void setLithology(String lithology) {
    this.lithology = lithology;
  }

  public ProbabilityDistributionForm getAverageNetPayThickness() {
    return averageNetPayThickness;
  }

  public void setAverageNetPayThickness(
      ProbabilityDistributionForm averageNetPayThickness) {
    this.averageNetPayThickness = averageNetPayThickness;
  }

  public ProbabilityDistributionForm getGrossReservoirThickness() {
    return grossReservoirThickness;
  }

  public void setGrossReservoirThickness(
      ProbabilityDistributionForm grossReservoirThickness) {
    this.grossReservoirThickness = grossReservoirThickness;
  }

  public ProbabilityDistributionForm getGrossRockVolume() {
    return grossRockVolume;
  }

  public void setGrossRockVolume(
      ProbabilityDistributionForm grossRockVolume) {
    this.grossRockVolume = grossRockVolume;
  }

  public ProbabilityDistributionForm getPercentOfGrvOilBearing() {
    return percentOfGrvOilBearing;
  }

  public void setPercentOfGrvOilBearing(
      ProbabilityDistributionForm percentOfGrvOilBearing) {
    this.percentOfGrvOilBearing = percentOfGrvOilBearing;
  }

  public ProbabilityDistributionForm getNetToGross() {
    return netToGross;
  }

  public void setNetToGross(
      ProbabilityDistributionForm netToGross) {
    this.netToGross = netToGross;
  }

  public ProbabilityDistributionForm getNetPorosity() {
    return netPorosity;
  }

  public void setNetPorosity(
      ProbabilityDistributionForm netPorosity) {
    this.netPorosity = netPorosity;
  }

  public ProbabilityDistributionForm getDepthToHydrocarbonContact() {
    return depthToHydrocarbonContact;
  }

  public void setDepthToHydrocarbonContact(
      ProbabilityDistributionForm depthToHydrocarbonContact) {
    this.depthToHydrocarbonContact = depthToHydrocarbonContact;
  }

  public String getReservoirFacies() {
    return reservoirFacies;
  }

  public void setReservoirFacies(String reservoirFacies) {
    this.reservoirFacies = reservoirFacies;
  }

  public String getReservoirConnectivity() {
    return reservoirConnectivity;
  }

  public void setReservoirConnectivity(String reservoirConnectivity) {
    this.reservoirConnectivity = reservoirConnectivity;
  }

  public ProbabilityDistributionForm getDepthToOilWaterContact() {
    return depthToOilWaterContact;
  }

  public void setDepthToOilWaterContact(
      ProbabilityDistributionForm depthToOilWaterContact) {
    this.depthToOilWaterContact = depthToOilWaterContact;
  }

  public ProbabilityDistributionForm getDepthToGasWaterContact() {
    return depthToGasWaterContact;
  }

  public void setDepthToGasWaterContact(
      ProbabilityDistributionForm depthToGasWaterContact) {
    this.depthToGasWaterContact = depthToGasWaterContact;
  }

  public ProbabilityDistributionForm getDepthToGasOilContact() {
    return depthToGasOilContact;
  }

  public void setDepthToGasOilContact(
      ProbabilityDistributionForm depthToGasOilContact) {
    this.depthToGasOilContact = depthToGasOilContact;
  }

  private ProbabilityDistributionForm createCustomProbabilityDistributionForm() {
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    return probabilityDistributionForm;
  }
}
