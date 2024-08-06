package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservoirCurrentWorksDetailRepository extends CrudRepository<ReservoirCurrentWorksDetail, UUID> {
  Optional<ReservoirCurrentWorksDetail> findByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);
}
