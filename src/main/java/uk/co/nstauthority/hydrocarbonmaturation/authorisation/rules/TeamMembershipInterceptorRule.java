package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Component
public class TeamMembershipInterceptorRule implements SubmissionInterceptorSecurityRule {

  private final TeamMembershipInterceptorFactory teamMembershipInterceptorFactory;


  @Autowired
  public TeamMembershipInterceptorRule(TeamMembershipInterceptorFactory teamMembershipInterceptorFactory) {
    this.teamMembershipInterceptorFactory = teamMembershipInterceptorFactory;
  }

  @Override
  public Class<? extends Annotation> supports() {
    return IsFromResponsibleTeam.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  ServiceUserDetail userDetails,
                                  SubmissionDetail submissionDetail) {
    return teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy()
        .hasRequiredTeamMembershipPermissions(userDetails, submissionDetail);
  }
}
