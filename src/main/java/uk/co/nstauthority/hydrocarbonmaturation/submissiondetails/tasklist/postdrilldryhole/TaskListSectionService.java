package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import java.util.Optional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface TaskListSectionService {

  Optional<TaskListSection> getSection(SubmissionDetail submissionDetail);
}
