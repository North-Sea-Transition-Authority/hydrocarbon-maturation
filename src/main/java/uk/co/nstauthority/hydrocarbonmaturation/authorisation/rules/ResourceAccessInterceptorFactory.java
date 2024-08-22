package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ResourceAccessInterceptorStrategy;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Component
public class ResourceAccessInterceptorFactory {

  private final RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;
  private final OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;
  private final InternalTeamService internalTeamService;

  @Autowired
  public ResourceAccessInterceptorFactory(RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule,
                                          OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule,
                                          InternalTeamService internalTeamService) {
    this.regulatorTeamMembershipInterceptorRule = regulatorTeamMembershipInterceptorRule;
    this.operatorTeamMembershipInterceptorRule = operatorTeamMembershipInterceptorRule;
    this.internalTeamService = internalTeamService;
  }

  public ResourceAccessInterceptorStrategy getResourceAccessInterceptorStrategy(User user) {
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      return regulatorTeamMembershipInterceptorRule;
    } else {
      return operatorTeamMembershipInterceptorRule;
    }
  }
}
