package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface MixedCaseOutputsRepository extends CrudRepository<MixedCaseOutput, UUID> {

  Optional<MixedCaseOutput> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
