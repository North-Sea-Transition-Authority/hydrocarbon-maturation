package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import jakarta.transaction.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

public interface ReservoirSectionDuplicationService {

  @Transactional
  void duplicateReservoirSection(
      ReservoirSubmissionDetail oldReservoirSubmissionDetail, ReservoirSubmissionDetail newReservoirSubmissionDetail);
}
