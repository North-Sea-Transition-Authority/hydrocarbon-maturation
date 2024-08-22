package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsRegulatorInResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;

@Component
public class RegulatorResponsibleTeamInterceptorRule implements SubmissionInterceptorSecurityRule {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegulatorResponsibleTeamInterceptorRule.class);

  private final TeamQueryService teamQueryService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Autowired
  public RegulatorResponsibleTeamInterceptorRule(
      TeamQueryService teamQueryService,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService) {
    this.teamQueryService = teamQueryService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
  }

  @Override
  public Class<? extends Annotation> supports() {
    return IsRegulatorInResponsibleTeam.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  User user,
                                  SubmissionDetail submissionDetail) {
    var responsibleTeam = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource());
    var teamType = responsibleTeam.getResponsibleTeamType().getTeamType();
    var role = responsibleTeam.getResponsibleTeamType().getRole();
    if (teamQueryService.userHasStaticRole(user.wuaId(), teamType, role)) {
      return SecurityRuleResult.continueAsNormal();
    } else {
      var userId = user.wuaId();
      var submissionDetailId = submissionDetail.getId();

      LOGGER.warn("User {} attempted to access submission {} " + "without expected regulator team role {}.",
          userId,
          submissionDetailId,
          responsibleTeam.getResponsibleTeamType());

      return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
    }
  }
}
