package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface TeamMembershipInterceptorStrategy {

  SecurityRuleResult hasRequiredTeamMembershipPermissions(ServiceUserDetail userDetails,
                                                          SubmissionDetail submissionDetail);
}
