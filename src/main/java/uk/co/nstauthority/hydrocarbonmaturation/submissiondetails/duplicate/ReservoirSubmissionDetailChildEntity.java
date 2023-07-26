package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

public interface ReservoirSubmissionDetailChildEntity {
  void setId(UUID id);

  void setReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);
}
