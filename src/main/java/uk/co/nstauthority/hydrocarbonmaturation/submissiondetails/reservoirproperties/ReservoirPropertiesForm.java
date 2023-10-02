package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

class ReservoirPropertiesForm implements SubmissionForm {

  private String formationName;
  private String subUnit;
  private AgeSeries ageStageOrSeries;
  private GroupName groupName;
  private ReservoirLithology lithology;
  private String lithologyOther;
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
  private ProbabilityDistributionForm originalDepthToOilWaterContact = createCustomProbabilityDistributionForm();
  private ProbabilityDistributionForm originalDepthToGasWaterContact = createCustomProbabilityDistributionForm();
  private ProbabilityDistributionForm originalDepthToGasOilContact = createCustomProbabilityDistributionForm();
  private ProbabilityDistributionForm depthToMovedOilWaterContact = createCustomProbabilityDistributionForm();
  private ProbabilityDistributionForm depthToMovedGasWaterContact = createCustomProbabilityDistributionForm();
  private ProbabilityDistributionForm depthToMovedGasOilContact = createCustomProbabilityDistributionForm();
  private String reservoirFacies;
  private String reservoirConnectivity;

  public String getFormationName() {
    return formationName;
  }

  public void setFormationName(String formationName) {
    this.formationName = formationName;
  }

  public String getSubUnit() {
    return subUnit;
  }

  public void setSubUnit(String subUnit) {
    this.subUnit = subUnit;
  }

  public AgeSeries getAgeStageOrSeries() {
    return ageStageOrSeries;
  }

  public void setAgeStageOrSeries(
      AgeSeries ageStageOrSeries) {
    this.ageStageOrSeries = ageStageOrSeries;
  }

  public GroupName getGroupName() {
    return groupName;
  }

  public void setGroupName(
      GroupName groupName) {
    this.groupName = groupName;
  }

  public ReservoirLithology getLithology() {
    return lithology;
  }

  public void setLithology(
      ReservoirLithology lithology) {
    this.lithology = lithology;
  }

  public String getLithologyOther() {
    return lithologyOther;
  }

  public void setLithologyOther(String lithologyOther) {
    this.lithologyOther = lithologyOther;
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

  public ProbabilityDistributionForm getOriginalDepthToOilWaterContact() {
    return originalDepthToOilWaterContact;
  }

  public ReservoirPropertiesForm setOriginalDepthToOilWaterContact(
      ProbabilityDistributionForm originalDepthToOilWaterContact) {
    this.originalDepthToOilWaterContact = originalDepthToOilWaterContact;
    return this;
  }

  public ProbabilityDistributionForm getOriginalDepthToGasWaterContact() {
    return originalDepthToGasWaterContact;
  }

  public ReservoirPropertiesForm setOriginalDepthToGasWaterContact(
      ProbabilityDistributionForm originalDepthToGasWaterContact) {
    this.originalDepthToGasWaterContact = originalDepthToGasWaterContact;
    return this;
  }

  public ProbabilityDistributionForm getOriginalDepthToGasOilContact() {
    return originalDepthToGasOilContact;
  }

  public ReservoirPropertiesForm setOriginalDepthToGasOilContact(
      ProbabilityDistributionForm originalDepthToGasOilContact) {
    this.originalDepthToGasOilContact = originalDepthToGasOilContact;
    return this;
  }

  public ProbabilityDistributionForm getDepthToMovedOilWaterContact() {
    return depthToMovedOilWaterContact;
  }

  public ReservoirPropertiesForm setDepthToMovedOilWaterContact(
      ProbabilityDistributionForm depthToMovedOilWaterContact) {
    this.depthToMovedOilWaterContact = depthToMovedOilWaterContact;
    return this;
  }

  public ProbabilityDistributionForm getDepthToMovedGasWaterContact() {
    return depthToMovedGasWaterContact;
  }

  public ReservoirPropertiesForm setDepthToMovedGasWaterContact(
      ProbabilityDistributionForm depthToMovedGasWaterContact) {
    this.depthToMovedGasWaterContact = depthToMovedGasWaterContact;
    return this;
  }

  public ProbabilityDistributionForm getDepthToMovedGasOilContact() {
    return depthToMovedGasOilContact;
  }

  public ReservoirPropertiesForm setDepthToMovedGasOilContact(
      ProbabilityDistributionForm depthToMovedGasOilContact) {
    this.depthToMovedGasOilContact = depthToMovedGasOilContact;
    return this;
  }

  private ProbabilityDistributionForm createCustomProbabilityDistributionForm() {
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    return probabilityDistributionForm;
  }
}
