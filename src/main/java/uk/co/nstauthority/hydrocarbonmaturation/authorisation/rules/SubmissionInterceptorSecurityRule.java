package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface SubmissionInterceptorSecurityRule {

  Class<? extends Annotation> supports();

  SecurityRuleResult check(Object annotation,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           User users,
                           SubmissionDetail submissionDetail);

}