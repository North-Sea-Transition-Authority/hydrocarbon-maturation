package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;

@Component
public class ResourceRecordQueryServiceFactory {
  private final RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;
  private final OperatorResourceRecordQueryService operatorResourceRecordQueryService;
  private final AreaTeamService areaTeamService;
  private final UserService userService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  public ResourceRecordQueryServiceFactory(RegulatorResourceRecordQueryService regulatorResourceRecordQueryService,
                                    OperatorResourceRecordQueryService operatorResourceRecordQueryService,
                                    AreaTeamService areaTeamService,
                                    UserService userService,
                                    RegulatorTeamService regulatorTeamService) {
    this.regulatorResourceRecordQueryService = regulatorResourceRecordQueryService;
    this.operatorResourceRecordQueryService = operatorResourceRecordQueryService;
    this.areaTeamService = areaTeamService;
    this.userService = userService;
    this.regulatorTeamService = regulatorTeamService;
  }

  public ResourceRecordQueryServiceStrategy getQueryServiceStrategy() {
    var user = userService.getUser();
    if (regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user)) {
      return regulatorResourceRecordQueryService;
    } else {
      return operatorResourceRecordQueryService;
    }
  }
}
