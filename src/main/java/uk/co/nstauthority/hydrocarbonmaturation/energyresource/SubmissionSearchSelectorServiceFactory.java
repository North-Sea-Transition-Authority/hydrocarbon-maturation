package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Component
class SubmissionSearchSelectorServiceFactory {

  private final RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService;
  private final OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService;
  private final InternalTeamService internalTeamService;

  @Autowired
  SubmissionSearchSelectorServiceFactory(RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService,
                                         OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService,
                                         InternalTeamService internalTeamService) {
    this.regulatorSubmissionSearchSelectorService = regulatorSubmissionSearchSelectorService;
    this.operatorSubmissionSearchSelectorService = operatorSubmissionSearchSelectorService;
    this.internalTeamService = internalTeamService;
  }

  SubmissionSearchSelectorStrategy getSubmissionSearchSelectorStrategy(User user) {
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      return regulatorSubmissionSearchSelectorService;
    } else {
      return operatorSubmissionSearchSelectorService;
    }
  }
}
