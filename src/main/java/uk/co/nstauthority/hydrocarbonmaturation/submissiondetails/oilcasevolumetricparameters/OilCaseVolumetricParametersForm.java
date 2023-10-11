package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class OilCaseVolumetricParametersForm implements SubmissionForm {

  private ProbabilityDistributionForm oilHydrocarbonSaturation = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedGasOilRatioLifeOfField = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilFormationVolumeFactor = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilRecoveryFactor = new ProbabilityDistributionForm();

  public ProbabilityDistributionForm getOilHydrocarbonSaturation() {
    return oilHydrocarbonSaturation;
  }

  public void setOilHydrocarbonSaturation(
      ProbabilityDistributionForm oilHydrocarbonSaturation) {
    this.oilHydrocarbonSaturation = oilHydrocarbonSaturation;
  }

  public ProbabilityDistributionForm getAssociatedGasOilRatioLifeOfField() {
    return associatedGasOilRatioLifeOfField;
  }

  public void setAssociatedGasOilRatioLifeOfField(
      ProbabilityDistributionForm associatedGasOilRatioLifeOfField) {
    this.associatedGasOilRatioLifeOfField = associatedGasOilRatioLifeOfField;
  }

  public ProbabilityDistributionForm getOilFormationVolumeFactor() {
    return oilFormationVolumeFactor;
  }

  public void setOilFormationVolumeFactor(
      ProbabilityDistributionForm oilFormationVolumeFactor) {
    this.oilFormationVolumeFactor = oilFormationVolumeFactor;
  }

  public ProbabilityDistributionForm getOilRecoveryFactor() {
    return oilRecoveryFactor;
  }

  public void setOilRecoveryFactor(
      ProbabilityDistributionForm oilRecoveryFactor) {
    this.oilRecoveryFactor = oilRecoveryFactor;
  }
}
