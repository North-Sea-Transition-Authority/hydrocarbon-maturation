package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ResourceAccessInterceptorStrategy;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
public class ResourceAccessInterceptorFactory {

  private final RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;
  private final OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;
  private final UserService userService;
  private final RegulatorTeamService regulatorTeamService;
  private final AreaTeamService areaTeamService;

  @Autowired
  public ResourceAccessInterceptorFactory(RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule,
                                          OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule,
                                          UserService userService, RegulatorTeamService regulatorTeamService,
                                          AreaTeamService areaTeamService) {
    this.regulatorTeamMembershipInterceptorRule = regulatorTeamMembershipInterceptorRule;
    this.operatorTeamMembershipInterceptorRule = operatorTeamMembershipInterceptorRule;
    this.userService = userService;
    this.regulatorTeamService = regulatorTeamService;
    this.areaTeamService = areaTeamService;
  }

  public ResourceAccessInterceptorStrategy getResourceAccessInterceptorStrategy() {
    var user = userService.getUser();
    if (regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user)) {
      return regulatorTeamMembershipInterceptorRule;
    } else {
      return operatorTeamMembershipInterceptorRule;
    }
  }
}
