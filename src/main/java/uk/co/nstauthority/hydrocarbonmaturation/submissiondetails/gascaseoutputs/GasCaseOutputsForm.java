package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class GasCaseOutputsForm implements SubmissionForm {

  private ProbabilityDistributionForm gasInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasRecoverableResources = new ProbabilityDistributionForm();
  private Boolean hasAssociatedCondensateRecoverableResources;
  private ProbabilityDistributionForm associatedCondensateRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilEquivalentRecoverableResources = new ProbabilityDistributionForm();

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

  public Boolean getHasAssociatedCondensateRecoverableResources() {
    return hasAssociatedCondensateRecoverableResources;
  }

  public void setHasAssociatedCondensateRecoverableResources(Boolean hasAssociatedCondensateRecoverableResources) {
    this.hasAssociatedCondensateRecoverableResources = hasAssociatedCondensateRecoverableResources;
  }
}
