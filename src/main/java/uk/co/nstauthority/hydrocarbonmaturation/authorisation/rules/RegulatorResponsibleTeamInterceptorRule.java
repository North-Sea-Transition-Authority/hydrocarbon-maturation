package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsRegulatorInResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Component
public class RegulatorResponsibleTeamInterceptorRule implements SubmissionInterceptorSecurityRule {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegulatorResponsibleTeamInterceptorRule.class);

  private final PermissionService permissionService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Autowired
  public RegulatorResponsibleTeamInterceptorRule(
      PermissionService permissionService,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService) {
    this.permissionService = permissionService;
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
                                  User users,
                                  SubmissionDetail submissionDetail) {
    var responsibleTeam = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource());
    var permissionsRequired = responsibleTeam.getResponsibleTeamType().getRolePermissions();
    if (permissionService.hasPermission(users, permissionsRequired)) {
      return SecurityRuleResult.continueAsNormal();
    } else {
      var userId = users.wuaId();
      var submissionDetailId = submissionDetail.getId();

      LOGGER.warn("User {} attempted to access submission {} " + "without expected regulator team role {}.",
          userId,
          submissionDetailId,
          responsibleTeam.getResponsibleTeamType());

      return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
    }
  }
}
