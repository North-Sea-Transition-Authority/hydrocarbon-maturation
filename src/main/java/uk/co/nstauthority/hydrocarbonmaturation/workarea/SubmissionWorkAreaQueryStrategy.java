package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;

interface SubmissionWorkAreaQueryStrategy {

  List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems(User user);
}
