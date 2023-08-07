package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
class SubmissionWorkAreaServiceFactory {

  private final ResponsibleTeamSubmissionWorkAreaQueryService responsibleTeamSubmissionWorkAreaQueryService;
  private final OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;
  private final RegulatorTeamService regulatorTeamService;
  private final AreaTeamService areaTeamService;

  @Autowired
  SubmissionWorkAreaServiceFactory(ResponsibleTeamSubmissionWorkAreaQueryService responsibleTeamSubmissionWorkAreaQueryService,
                                   OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService,
                                   RegulatorTeamService regulatorTeamService,
                                   AreaTeamService areaTeamService) {
    this.responsibleTeamSubmissionWorkAreaQueryService = responsibleTeamSubmissionWorkAreaQueryService;
    this.operatorSubmissionWorkAreaQueryService = operatorSubmissionWorkAreaQueryService;
    this.regulatorTeamService = regulatorTeamService;
    this.areaTeamService = areaTeamService;
  }

  SubmissionWorkAreaQueryStrategy getWorkAreaServiceStrategy(ServiceUserDetail user) {
    if (regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user)) {
      return responsibleTeamSubmissionWorkAreaQueryService;
    } else {
      return operatorSubmissionWorkAreaQueryService;
    }
  }
}
