package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

interface PermeabilityDetailRepository extends CrudRepository<PermeabilityDetail, UUID> {

  Optional<PermeabilityDetail> findByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);
}
