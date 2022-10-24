package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class MixedCaseOutputsForm implements SubmissionForm {

  private ProbabilityDistributionForm oilInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedGasRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedCondensateRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilEquivalentRecoverableResources = new ProbabilityDistributionForm();
  private DecimalInput riskedOilEquivalentRecoverableMean;
  private Boolean hasAssociatedGasRecoverableResources;

  public Boolean getHasAssociatedGasRecoverableResources() {
    return hasAssociatedGasRecoverableResources;
  }

  public void setHasAssociatedGasRecoverableResources(Boolean hasAssociatedGasRecoverableResources) {
    this.hasAssociatedGasRecoverableResources = hasAssociatedGasRecoverableResources;
  }

  public ProbabilityDistributionForm getOilInPlace() {
    return oilInPlace;
  }

  public void setOilInPlace(
      ProbabilityDistributionForm oilInPlace) {
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

  public ProbabilityDistributionForm getGasInPlace() {
    return gasInPlace;
  }

  public void setGasInPlace(
      ProbabilityDistributionForm gasInPlace) {
    this.gasInPlace = gasInPlace;
  }

  public ProbabilityDistributionForm getGasRecoverableResources() {
    return gasRecoverableResources;
  }

  public void setGasRecoverableResources(
      ProbabilityDistributionForm gasRecoverableResources) {
    this.gasRecoverableResources = gasRecoverableResources;
  }

  public ProbabilityDistributionForm getAssociatedCondensateRecoverableResources() {
    return associatedCondensateRecoverableResources;
  }

  public void setAssociatedCondensateRecoverableResources(
      ProbabilityDistributionForm associatedCondensateRecoverableResources) {
    this.associatedCondensateRecoverableResources = associatedCondensateRecoverableResources;
  }

  public ProbabilityDistributionForm getOilEquivalentRecoverableResources() {
    return oilEquivalentRecoverableResources;
  }

  public void setOilEquivalentRecoverableResources(
      ProbabilityDistributionForm oilEquivalentRecoverableResources) {
    this.oilEquivalentRecoverableResources = oilEquivalentRecoverableResources;
  }

  public DecimalInput getRiskedOilEquivalentRecoverableMean() {
    return riskedOilEquivalentRecoverableMean;
  }

  public void setRiskedOilEquivalentRecoverableMean(
      DecimalInput riskedOilEquivalentRecoverableMean) {
    this.riskedOilEquivalentRecoverableMean = riskedOilEquivalentRecoverableMean;
  }
}
