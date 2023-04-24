package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Repository
interface SubmissionUpdateRepository extends CrudRepository<SubmissionUpdate, UUID> {

  Optional<SubmissionUpdate> findFirstBySubmissionOrderByRequestedTimestampDesc(Submission submission);
}
