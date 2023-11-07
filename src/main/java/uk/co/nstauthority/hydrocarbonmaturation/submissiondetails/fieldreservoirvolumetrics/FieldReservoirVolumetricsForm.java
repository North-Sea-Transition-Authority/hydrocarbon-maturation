package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class FieldReservoirVolumetricsForm implements SubmissionForm {
  private ProbabilityDistributionForm oilInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasInPlace = new ProbabilityDistributionForm();

  public ProbabilityDistributionForm getOilInPlace() {
    return oilInPlace;
  }

  public void setOilInPlace(ProbabilityDistributionForm oilInPlace) {
    this.oilInPlace = oilInPlace;
  }

  public ProbabilityDistributionForm getGasInPlace() {
    return gasInPlace;
  }

  public void setGasInPlace(ProbabilityDistributionForm gasInPlace) {
    this.gasInPlace = gasInPlace;
  }
}
