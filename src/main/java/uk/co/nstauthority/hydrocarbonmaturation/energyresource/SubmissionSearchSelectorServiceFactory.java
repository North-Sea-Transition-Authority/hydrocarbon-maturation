package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
class SubmissionSearchSelectorServiceFactory {

  private final RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService;
  private final OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  SubmissionSearchSelectorServiceFactory(RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService,
                                         OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService,
                                         UserDetailService userDetailService, RegulatorTeamService regulatorTeamService) {
    this.regulatorSubmissionSearchSelectorService = regulatorSubmissionSearchSelectorService;
    this.operatorSubmissionSearchSelectorService = operatorSubmissionSearchSelectorService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  SubmissionSearchSelectorStrategy getSubmissionSearchSelectorStrategy() {
    if (regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail())) {
      return regulatorSubmissionSearchSelectorService;
    } else {
      return operatorSubmissionSearchSelectorService;
    }
  }
}
