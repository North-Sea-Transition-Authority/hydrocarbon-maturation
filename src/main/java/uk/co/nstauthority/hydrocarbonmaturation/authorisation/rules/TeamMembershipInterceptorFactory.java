package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.TeamMembershipInterceptorStrategy;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
public class TeamMembershipInterceptorFactory {

  private final RegulatorTeamMembershipInterceptorRule regulatorTeamMembershipInterceptorRule;
  private final OperatorTeamMembershipInterceptorRule operatorTeamMembershipInterceptorRule;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  public TeamMembershipInterceptorFactory(RegulatorTeamMembershipInterceptorRule regulatorTeamMembershipInterceptorRule,
                                          OperatorTeamMembershipInterceptorRule operatorTeamMembershipInterceptorRule,
                                          UserDetailService userDetailService, RegulatorTeamService regulatorTeamService) {
    this.regulatorTeamMembershipInterceptorRule = regulatorTeamMembershipInterceptorRule;
    this.operatorTeamMembershipInterceptorRule = operatorTeamMembershipInterceptorRule;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  public TeamMembershipInterceptorStrategy getTeamMembershipInterceptorStrategy() {
    if (regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail())) {
      return regulatorTeamMembershipInterceptorRule;
    } else {
      return operatorTeamMembershipInterceptorRule;
    }
  }
}
