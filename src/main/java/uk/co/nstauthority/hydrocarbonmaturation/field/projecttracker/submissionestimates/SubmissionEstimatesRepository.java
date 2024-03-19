package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SubmissionEstimatesRepository extends CrudRepository<SubmissionEstimates, UUID> {
}