package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;

interface SubmissionWorkAreaQueryStrategy {

  List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems(ServiceUserDetail user);
}
