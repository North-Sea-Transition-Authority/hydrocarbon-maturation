package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
class ResourceRecordQueryServiceFactory {
  private final RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;
  private final OperatorResourceRecordQueryService operatorResourceRecordQueryService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  ResourceRecordQueryServiceFactory(RegulatorResourceRecordQueryService regulatorResourceRecordQueryService,
                                    OperatorResourceRecordQueryService operatorResourceRecordQueryService,
                                    UserDetailService userDetailService, RegulatorTeamService regulatorTeamService) {
    this.regulatorResourceRecordQueryService = regulatorResourceRecordQueryService;
    this.operatorResourceRecordQueryService = operatorResourceRecordQueryService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  ResourceRecordQueryServiceStrategy getQueryServiceStrategy() {
    if (regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail())) {
      return regulatorResourceRecordQueryService;
    } else {
      return operatorResourceRecordQueryService;
    }
  }
}
