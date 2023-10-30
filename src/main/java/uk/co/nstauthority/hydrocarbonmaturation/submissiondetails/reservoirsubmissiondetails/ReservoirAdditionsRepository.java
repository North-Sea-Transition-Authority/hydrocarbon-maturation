package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface ReservoirAdditionsRepository extends CrudRepository<ReservoirAdditions, UUID> {
  Optional<ReservoirAdditions> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
