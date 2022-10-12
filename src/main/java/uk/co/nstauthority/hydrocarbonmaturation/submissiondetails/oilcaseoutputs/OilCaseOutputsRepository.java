package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface OilCaseOutputsRepository extends CrudRepository<OilCaseOutput, UUID> {

  Optional<OilCaseOutput> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
