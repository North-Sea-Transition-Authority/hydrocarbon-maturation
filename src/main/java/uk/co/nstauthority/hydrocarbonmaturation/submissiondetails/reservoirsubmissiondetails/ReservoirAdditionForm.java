package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class ReservoirAdditionForm implements SubmissionForm {
  private ReservoirAdditionStatus reservoirAdditionStatus;

  public ReservoirAdditionForm(ReservoirAdditionStatus reservoirAdditionStatus) {
    this.reservoirAdditionStatus = reservoirAdditionStatus;
  }

  public void setReservoirAdditionStatus(ReservoirAdditionStatus reservoirAdditionStatus) {
    this.reservoirAdditionStatus = reservoirAdditionStatus;
  }

  public ReservoirAdditionStatus getReservoirAdditionStatus() {
    return reservoirAdditionStatus;
  }
}
