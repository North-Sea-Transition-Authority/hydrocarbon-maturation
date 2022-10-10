package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import uk.co.nstauthority.hydrocarbonmaturation.numberinput.IntegerInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class OilCaseOutputsForm implements SubmissionForm {

  private ProbabilityDistributionForm oilInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedGasRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilEquivalentRecoverableResources = new ProbabilityDistributionForm();
  private IntegerInput riskedOilEquivalentRecoverableMean;

  public ProbabilityDistributionForm getOilInPlace() {
    return oilInPlace;
  }

  public void setOilInPlace(ProbabilityDistributionForm oilInPlace) {
    this.oilInPlace = oilInPlace;
  }

  public ProbabilityDistributionForm getOilRecoverableResources() {
    return oilRecoverableResources;
  }

  public void setOilRecoverableResources(
      ProbabilityDistributionForm oilRecoverableResources) {
    this.oilRecoverableResources = oilRecoverableResources;
  }

  public ProbabilityDistributionForm getAssociatedGasRecoverableResources() {
    return associatedGasRecoverableResources;
  }

  public void setAssociatedGasRecoverableResources(
      ProbabilityDistributionForm associatedGasRecoverableResources) {
    this.associatedGasRecoverableResources = associatedGasRecoverableResources;
  }

  public ProbabilityDistributionForm getOilEquivalentRecoverableResources() {
    return oilEquivalentRecoverableResources;
  }

  public void setOilEquivalentRecoverableResources(
      ProbabilityDistributionForm oilEquivalentRecoverableResources) {
    this.oilEquivalentRecoverableResources = oilEquivalentRecoverableResources;
  }

  public IntegerInput getRiskedOilEquivalentRecoverableMean() {
    return riskedOilEquivalentRecoverableMean;
  }

  public void setRiskedOilEquivalentRecoverableMean(IntegerInput riskedOilEquivalentRecoverableMean) {
    this.riskedOilEquivalentRecoverableMean = riskedOilEquivalentRecoverableMean;
  }
}
