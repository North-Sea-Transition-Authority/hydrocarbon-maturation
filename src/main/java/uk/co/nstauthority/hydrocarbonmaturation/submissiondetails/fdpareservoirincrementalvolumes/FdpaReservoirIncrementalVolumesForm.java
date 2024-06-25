package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class FdpaReservoirIncrementalVolumesForm implements SubmissionForm {

  private Boolean changeToInPlaceVolumes;
  private ProbabilityDistributionForm incrementalIncreaseOil = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm incrementalIncreaseGas = new ProbabilityDistributionForm();
  private String comment;

  public Boolean getChangeToInPlaceVolumes() {
    return changeToInPlaceVolumes;
  }

  public void setChangeToInPlaceVolumes(Boolean changeToInPlaceVolumes) {
    this.changeToInPlaceVolumes = changeToInPlaceVolumes;
  }

  public ProbabilityDistributionForm getIncrementalIncreaseOil() {
    return incrementalIncreaseOil;
  }

  public void setIncrementalIncreaseOil(
      ProbabilityDistributionForm incrementalIncreaseOil) {
    this.incrementalIncreaseOil = incrementalIncreaseOil;
  }

  public ProbabilityDistributionForm getIncrementalIncreaseGas() {
    return incrementalIncreaseGas;
  }

  public void setIncrementalIncreaseGas(
      ProbabilityDistributionForm incrementalIncreaseGas) {
    this.incrementalIncreaseGas = incrementalIncreaseGas;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
