package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Component
public class ResourceAccessInterceptorRule implements SubmissionInterceptorSecurityRule {

  private final ResourceAccessInterceptorFactory resourceAccessInterceptorFactory;


  @Autowired
  public ResourceAccessInterceptorRule(ResourceAccessInterceptorFactory resourceAccessInterceptorFactory) {
    this.resourceAccessInterceptorFactory = resourceAccessInterceptorFactory;
  }

  @Override
  public Class<? extends Annotation> supports() {
    return IsFromResponsibleTeam.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  User users,
                                  SubmissionDetail submissionDetail) {
    return resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy()
        .hasRequiredResourceAccessPermissions(users, submissionDetail);
  }
}
