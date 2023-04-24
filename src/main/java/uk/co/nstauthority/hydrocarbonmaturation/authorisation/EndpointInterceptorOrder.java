package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.RegulatorResponsibleTeamInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.ResourceAccessInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionInterceptorSecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionStatusInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionTypeInterceptorRule;

public final class EndpointInterceptorOrder {

  private EndpointInterceptorOrder() {
    throw new IllegalStateException("EndpointInterceptorOrder is a utility class");
  }

  private static final List<Class<? extends SubmissionInterceptorSecurityRule>> SUBMISSION_SECURITY_RULES = List.of(
      SubmissionStatusInterceptorRule.class, // check the status of the submission
      SubmissionTypeInterceptorRule.class, // checks the type of the submission
      ResourceAccessInterceptorRule.class, // checks the responsible teams for the resource
      RegulatorResponsibleTeamInterceptorRule.class // checks if user is regulator responsible for the resource
  );

  public static int orderForSubmission(Class<? extends SubmissionInterceptorSecurityRule> endpointInterceptorClass) {
    return SUBMISSION_SECURITY_RULES.indexOf(endpointInterceptorClass);
  }

}
