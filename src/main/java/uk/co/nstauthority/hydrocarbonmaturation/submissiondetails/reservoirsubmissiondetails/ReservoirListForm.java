package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class ReservoirListForm implements SubmissionForm {

  private List<String> reservoirs = new ArrayList<>();

  public List<String> getReservoirs() {
    return reservoirs;
  }

  public void setReservoirs(List<String> reservoirs) {
    this.reservoirs = reservoirs;
  }
}
