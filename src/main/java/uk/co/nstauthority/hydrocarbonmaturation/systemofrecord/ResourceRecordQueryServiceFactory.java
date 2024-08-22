package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Component
public class ResourceRecordQueryServiceFactory {
  private final RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;
  private final OperatorResourceRecordQueryService operatorResourceRecordQueryService;
  private final InternalTeamService internalTeamService;

  @Autowired
  public ResourceRecordQueryServiceFactory(RegulatorResourceRecordQueryService regulatorResourceRecordQueryService,
                                           OperatorResourceRecordQueryService operatorResourceRecordQueryService,
                                           InternalTeamService internalTeamService) {
    this.regulatorResourceRecordQueryService = regulatorResourceRecordQueryService;
    this.operatorResourceRecordQueryService = operatorResourceRecordQueryService;
    this.internalTeamService = internalTeamService;
  }

  public ResourceRecordQueryServiceStrategy getQueryServiceStrategy(User user) {
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      return regulatorResourceRecordQueryService;
    } else {
      return operatorResourceRecordQueryService;
    }
  }
}
