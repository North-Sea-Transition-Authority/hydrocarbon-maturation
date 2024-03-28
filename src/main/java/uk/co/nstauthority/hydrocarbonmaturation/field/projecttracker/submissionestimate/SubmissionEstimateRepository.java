package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Repository
interface SubmissionEstimateRepository extends CrudRepository<SubmissionEstimate, UUID> {
  List<SubmissionEstimate> getAllByProjectAndSubmissionEstimateType(Project project, SubmissionEstimateType type);
}