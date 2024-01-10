package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface ResourceAccessInterceptorStrategy {

  SecurityRuleResult hasRequiredResourceAccessPermissions(User users,
                                                          SubmissionDetail submissionDetail);
}
