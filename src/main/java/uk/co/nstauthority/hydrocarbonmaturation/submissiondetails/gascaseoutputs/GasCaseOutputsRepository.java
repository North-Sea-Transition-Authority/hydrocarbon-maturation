package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Repository
interface GasCaseOutputsRepository extends CrudRepository<GasCaseOutput, UUID> {

  Optional<GasCaseOutput> findByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);

  void deleteByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);
}
