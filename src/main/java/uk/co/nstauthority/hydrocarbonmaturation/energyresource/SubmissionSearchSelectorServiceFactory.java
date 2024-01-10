package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
class SubmissionSearchSelectorServiceFactory {

  private final RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService;
  private final OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService;
  private final UserService userService;
  private final RegulatorTeamService regulatorTeamService;
  private final AreaTeamService areaTeamService;

  @Autowired
  SubmissionSearchSelectorServiceFactory(RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService,
                                         OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService,
                                         UserService userService,
                                         RegulatorTeamService regulatorTeamService,
                                         AreaTeamService areaTeamService) {
    this.regulatorSubmissionSearchSelectorService = regulatorSubmissionSearchSelectorService;
    this.operatorSubmissionSearchSelectorService = operatorSubmissionSearchSelectorService;
    this.userService = userService;
    this.regulatorTeamService = regulatorTeamService;
    this.areaTeamService = areaTeamService;
  }

  SubmissionSearchSelectorStrategy getSubmissionSearchSelectorStrategy() {
    var user = userService.getUser();
    if (regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user)) {
      return regulatorSubmissionSearchSelectorService;
    } else {
      return operatorSubmissionSearchSelectorService;
    }
  }
}
