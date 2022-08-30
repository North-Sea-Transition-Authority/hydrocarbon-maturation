package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface ChanceOfSuccessRepository extends CrudRepository<ChanceOfSuccess, UUID> {

  Optional<ChanceOfSuccess> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
