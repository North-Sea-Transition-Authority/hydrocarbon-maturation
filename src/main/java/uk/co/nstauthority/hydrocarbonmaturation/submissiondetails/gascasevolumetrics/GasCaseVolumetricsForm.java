package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class GasCaseVolumetricsForm implements SubmissionForm {

  private ProbabilityDistributionForm gasHydrocarbonSaturation = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm condensateGasRatioLifeOfField = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasRecoveryFactor = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasFormationVolumeFactor = new ProbabilityDistributionForm();

  public ProbabilityDistributionForm getGasHydrocarbonSaturation() {
    return gasHydrocarbonSaturation;
  }

  public void setGasHydrocarbonSaturation(
      ProbabilityDistributionForm gasHydrocarbonSaturation) {
    this.gasHydrocarbonSaturation = gasHydrocarbonSaturation;
  }

  public ProbabilityDistributionForm getCondensateGasRatioLifeOfField() {
    return condensateGasRatioLifeOfField;
  }

  public void setCondensateGasRatioLifeOfField(
      ProbabilityDistributionForm condensateGasRatioLifeOfField) {
    this.condensateGasRatioLifeOfField = condensateGasRatioLifeOfField;
  }

  public ProbabilityDistributionForm getGasRecoveryFactor() {
    return gasRecoveryFactor;
  }

  public void setGasRecoveryFactor(
      ProbabilityDistributionForm gasRecoveryFactor) {
    this.gasRecoveryFactor = gasRecoveryFactor;
  }

  public ProbabilityDistributionForm getGasFormationVolumeFactor() {
    return gasFormationVolumeFactor;
  }

  public void setGasFormationVolumeFactor(
      ProbabilityDistributionForm gasFormationVolumeFactor) {
    this.gasFormationVolumeFactor = gasFormationVolumeFactor;
  }
}
