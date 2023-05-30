package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
class ResourceRecordQueryServiceFactory {
  private final RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;
  private final OperatorResourceRecordQueryService operatorResourceRecordQueryService;
  private final AreaTeamService areaTeamService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  ResourceRecordQueryServiceFactory(RegulatorResourceRecordQueryService regulatorResourceRecordQueryService,
                                    OperatorResourceRecordQueryService operatorResourceRecordQueryService,
                                    AreaTeamService areaTeamService,
                                    UserDetailService userDetailService,
                                    RegulatorTeamService regulatorTeamService) {
    this.regulatorResourceRecordQueryService = regulatorResourceRecordQueryService;
    this.operatorResourceRecordQueryService = operatorResourceRecordQueryService;
    this.areaTeamService = areaTeamService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  ResourceRecordQueryServiceStrategy getQueryServiceStrategy() {
    var user = userDetailService.getUserDetail();
    if (regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user)) {
      return regulatorResourceRecordQueryService;
    } else {
      return operatorResourceRecordQueryService;
    }
  }
}
