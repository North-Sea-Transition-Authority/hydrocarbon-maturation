package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionInterceptorSecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionStatusInterceptorRule;

public final class EndpointInterceptorOrder {

  private EndpointInterceptorOrder() {
    throw new IllegalStateException("EndpointInterceptorOrder is a utility class");
  }

  private static final List<Class<? extends SubmissionInterceptorSecurityRule>> SUBMISSION_SECURITY_RULES = List.of(
      SubmissionStatusInterceptorRule.class // check the status of the application
  );

  public static int orderForSubmission(Class<? extends SubmissionInterceptorSecurityRule> endpointInterceptorClass) {
    return SUBMISSION_SECURITY_RULES.indexOf(endpointInterceptorClass);
  }

}
