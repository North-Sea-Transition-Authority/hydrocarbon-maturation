package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
class SubmissionWorkAreaServiceFactory {

  private final RegulatorSubmissionWorkAreaQueryService regulatorSubmissionWorkAreaQueryService;
  private final OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  SubmissionWorkAreaServiceFactory(RegulatorSubmissionWorkAreaQueryService regulatorSubmissionWorkAreaQueryService,
                                   OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService,
                                   UserDetailService userDetailService, RegulatorTeamService regulatorTeamService) {
    this.regulatorSubmissionWorkAreaQueryService = regulatorSubmissionWorkAreaQueryService;
    this.operatorSubmissionWorkAreaQueryService = operatorSubmissionWorkAreaQueryService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  SubmissionWorkAreaQueryStrategy getWorkAreaServiceStrategy() {
    if (regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail())) {
      return regulatorSubmissionWorkAreaQueryService;
    } else {
      return operatorSubmissionWorkAreaQueryService;
    }
  }
}
