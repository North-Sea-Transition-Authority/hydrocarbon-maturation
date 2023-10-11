package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class MixedCaseVolumetricParametersForm implements SubmissionForm {

  private ProbabilityDistributionForm oilFormationVolumeFactor = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasFormationVolumeFactor = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilHydrocarbonSaturation = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasHydrocarbonSaturation = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedGasOilRatioLifeOfField = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm condensateGasRatioLifeOfField = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilRecoveryFactor = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasRecoveryFactor = new ProbabilityDistributionForm();

  public ProbabilityDistributionForm getOilFormationVolumeFactor() {
    return oilFormationVolumeFactor;
  }

  public void setOilFormationVolumeFactor(
      ProbabilityDistributionForm oilFormationVolumeFactor) {
    this.oilFormationVolumeFactor = oilFormationVolumeFactor;
  }

  public ProbabilityDistributionForm getGasFormationVolumeFactor() {
    return gasFormationVolumeFactor;
  }

  public void setGasFormationVolumeFactor(
      ProbabilityDistributionForm gasFormationVolumeFactor) {
    this.gasFormationVolumeFactor = gasFormationVolumeFactor;
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
