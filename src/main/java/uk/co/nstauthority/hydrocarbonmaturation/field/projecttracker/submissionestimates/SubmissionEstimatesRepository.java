package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Repository
interface SubmissionEstimatesRepository extends CrudRepository<SubmissionEstimates, UUID> {
  List<SubmissionEstimates> getAllByProjectAndSubmissionEstimateType(Project project, SubmissionEstimatesType type);
}