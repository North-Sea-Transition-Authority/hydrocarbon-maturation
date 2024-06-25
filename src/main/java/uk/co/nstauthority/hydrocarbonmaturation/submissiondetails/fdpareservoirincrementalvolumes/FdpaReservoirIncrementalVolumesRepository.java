package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Repository
public interface FdpaReservoirIncrementalVolumesRepository
    extends CrudRepository<FdpaReservoirIncrementalVolume, UUID> {
  Optional<FdpaReservoirIncrementalVolume> findByReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail);
}
