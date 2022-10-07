package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class MixedCaseVolumetricsForm implements SubmissionForm {

  private ProbabilityDistributionForm formationVolumeFactor = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilHydrocarbonSaturation = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasHydrocarbonSaturation = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedGasOilRatioLifeOfField = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasExpansionFactor = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm condensateGasRatioLifeOfField = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilRecoveryFactor = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasRecoveryFactor = new ProbabilityDistributionForm();

  public ProbabilityDistributionForm getFormationVolumeFactor() {
    return formationVolumeFactor;
  }

  public void setFormationVolumeFactor(
      ProbabilityDistributionForm formationVolumeFactor) {
    this.formationVolumeFactor = formationVolumeFactor;
  }

  public ProbabilityDistributionForm getOilHydrocarbonSaturation() {
    return oilHydrocarbonSaturation;
  }

  public void setOilHydrocarbonSaturation(
      ProbabilityDistributionForm oilHydrocarbonSaturation) {
    this.oilHydrocarbonSaturation = oilHydrocarbonSaturation;
  }

  public ProbabilityDistributionForm getGasHydrocarbonSaturation() {
    return gasHydrocarbonSaturation;
  }

  public void setGasHydrocarbonSaturation(
      ProbabilityDistributionForm gasHydrocarbonSaturation) {
    this.gasHydrocarbonSaturation = gasHydrocarbonSaturation;
  }

  public ProbabilityDistributionForm getAssociatedGasOilRatioLifeOfField() {
    return associatedGasOilRatioLifeOfField;
  }

  public void setAssociatedGasOilRatioLifeOfField(
      ProbabilityDistributionForm associatedGasOilRatioLifeOfField) {
    this.associatedGasOilRatioLifeOfField = associatedGasOilRatioLifeOfField;
  }

  public ProbabilityDistributionForm getGasExpansionFactor() {
    return gasExpansionFactor;
  }

  public void setGasExpansionFactor(
      ProbabilityDistributionForm gasExpansionFactor) {
    this.gasExpansionFactor = gasExpansionFactor;
  }

  public ProbabilityDistributionForm getCondensateGasRatioLifeOfField() {
    return condensateGasRatioLifeOfField;
  }

  public void setCondensateGasRatioLifeOfField(
      ProbabilityDistributionForm condensateGasRatioLifeOfField) {
    this.condensateGasRatioLifeOfField = condensateGasRatioLifeOfField;
  }

  public ProbabilityDistributionForm getOilRecoveryFactor() {
    return oilRecoveryFactor;
  }

  public void setOilRecoveryFactor(
      ProbabilityDistributionForm oilRecoveryFactor) {
    this.oilRecoveryFactor = oilRecoveryFactor;
  }

  public ProbabilityDistributionForm getGasRecoveryFactor() {
    return gasRecoveryFactor;
  }

  public void setGasRecoveryFactor(
      ProbabilityDistributionForm gasRecoveryFactor) {
    this.gasRecoveryFactor = gasRecoveryFactor;
  }
}
