package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Component
class SubmissionWorkAreaServiceFactory {

  private final ResponsibleTeamSubmissionWorkAreaQueryService responsibleTeamSubmissionWorkAreaQueryService;
  private final OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;
  private final InternalTeamService internalTeamService;

  @Autowired
  SubmissionWorkAreaServiceFactory(ResponsibleTeamSubmissionWorkAreaQueryService responsibleTeamSubmissionWorkAreaQueryService,
                                   OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService,
                                   InternalTeamService internalTeamService) {
    this.responsibleTeamSubmissionWorkAreaQueryService = responsibleTeamSubmissionWorkAreaQueryService;
    this.operatorSubmissionWorkAreaQueryService = operatorSubmissionWorkAreaQueryService;
    this.internalTeamService = internalTeamService;
  }

  SubmissionWorkAreaQueryStrategy getWorkAreaServiceStrategy(User user) {
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      return responsibleTeamSubmissionWorkAreaQueryService;
    } else {
      return operatorSubmissionWorkAreaQueryService;
    }
  }
}
