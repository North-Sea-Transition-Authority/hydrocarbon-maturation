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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsRegulatorForProjectField;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Component
public class RegulatorForProjectFieldInterceptorRule implements ProjectInterceptorSecurityRule {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegulatorForProjectFieldInterceptorRule.class);

  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final TeamQueryService teamQueryService;

  @Autowired
  RegulatorForProjectFieldInterceptorRule(
          RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
          TeamQueryService teamQueryService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.teamQueryService = teamQueryService;
  }

  @Override
  public Class<? extends Annotation> supports() {
    return IsRegulatorForProjectField.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  User user,
                                  Project project) {
    if (teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)) {
      return SecurityRuleResult.continueAsNormal();
    }

    var fieldResource = project.getField().getResource();
    var responsibleTeamType = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(fieldResource)
        .getResponsibleTeamType();

    var teamType = responsibleTeamType.getTeamType();
    var role = responsibleTeamType.getRole();
    if (teamQueryService.userHasStaticRole(user.wuaId(), teamType, role)) {
      return SecurityRuleResult.continueAsNormal();
    }

    LOGGER.warn("User {} attempted to access project {} without being a project analyst or member of the {}.",
        user.wuaId(),
        project.getId(),
        responsibleTeamType.getDisplayName());

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
  }
}
