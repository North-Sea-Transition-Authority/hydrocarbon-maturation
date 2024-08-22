package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ResourceAccessInterceptorStrategy;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;

@Component
public class RegulatorResourceAccessInterceptorRule implements ResourceAccessInterceptorStrategy {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegulatorResourceAccessInterceptorRule.class);
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final TeamQueryService teamQueryService;

  @Autowired
  RegulatorResourceAccessInterceptorRule(
          RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
          TeamQueryService teamQueryService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.teamQueryService = teamQueryService;
  }

  @Override
  public SecurityRuleResult hasRequiredResourceAccessPermissions(User user,
                                                                 SubmissionDetail submissionDetail) {

    var responsibleTeamType = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource())
        .getResponsibleTeamType();
    var teamType = responsibleTeamType.getTeamType();
    var role = responsibleTeamType.getRole();

    if (isNotDraftIndustrySubmission(submissionDetail)
        && teamQueryService.userHasStaticRole(user.wuaId(), teamType, role)) {
      return SecurityRuleResult.continueAsNormal();
    }

    var userId = user.wuaId();
    var submissionDetailId = submissionDetail.getId();

    LOGGER.warn("User {} attempted to access submission {} with expected regulator role {}.",
        userId,
        submissionDetailId,
        responsibleTeamType);

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
  }

  private boolean isNotDraftIndustrySubmission(SubmissionDetail submissionDetail) {
    return !(submissionDetail.getStatus() == SubmissionDetailStatus.DRAFT
        && submissionDetail.getSubmitterType() == SubmitterType.INDUSTRY);
  }
}
