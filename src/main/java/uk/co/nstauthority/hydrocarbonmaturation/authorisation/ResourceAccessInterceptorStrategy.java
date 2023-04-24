package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface ResourceAccessInterceptorStrategy {

  SecurityRuleResult hasRequiredResourceAccessPermissions(ServiceUserDetail userDetails,
                                                          SubmissionDetail submissionDetail);
}
