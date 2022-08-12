package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface SourceAndSealRepository extends CrudRepository<SourceAndSeal, UUID> {

  Optional<SourceAndSeal> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
