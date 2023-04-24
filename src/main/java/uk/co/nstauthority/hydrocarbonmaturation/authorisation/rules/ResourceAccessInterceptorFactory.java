package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ResourceAccessInterceptorStrategy;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
public class ResourceAccessInterceptorFactory {

  private final RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;
  private final OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  public ResourceAccessInterceptorFactory(RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule,
                                          OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule,
                                          UserDetailService userDetailService, RegulatorTeamService regulatorTeamService) {
    this.regulatorTeamMembershipInterceptorRule = regulatorTeamMembershipInterceptorRule;
    this.operatorTeamMembershipInterceptorRule = operatorTeamMembershipInterceptorRule;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  public ResourceAccessInterceptorStrategy getResourceAccessInterceptorStrategy() {
    if (regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail())) {
      return regulatorTeamMembershipInterceptorRule;
    } else {
      return operatorTeamMembershipInterceptorRule;
    }
  }
}
