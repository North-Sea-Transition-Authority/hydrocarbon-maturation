package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import java.util.Optional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

interface TaskListSectionService {

  Optional<TaskListSection> getSection(SubmissionDetail submissionDetail);
}
