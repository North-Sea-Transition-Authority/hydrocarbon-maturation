package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class OilCaseVolumetricsForm implements SubmissionForm {

  private ProbabilityDistributionForm oilHydrocarbonSaturation = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedGasOilRatioLifeOfField = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm formationVolumeFactor = new ProbabilityDistributionForm();
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

  public ProbabilityDistributionForm getFormationVolumeFactor() {
    return formationVolumeFactor;
  }

  public void setFormationVolumeFactor(
      ProbabilityDistributionForm formationVolumeFactor) {
    this.formationVolumeFactor = formationVolumeFactor;
  }

  public ProbabilityDistributionForm getOilRecoveryFactor() {
    return oilRecoveryFactor;
  }

  public void setOilRecoveryFactor(
      ProbabilityDistributionForm oilRecoveryFactor) {
    this.oilRecoveryFactor = oilRecoveryFactor;
  }
}
