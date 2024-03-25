package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsExtantProject;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;

@Component
public class IsProjectExtantInterceptorRule implements ProjectInterceptorSecurityRule {
  private static final Logger LOGGER = LoggerFactory.getLogger(IsProjectExtantInterceptorRule.class);

  @Override
  public Class<? extends Annotation> supports() {
    return IsExtantProject.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  User user,
                                  Project project) {
    if (ProjectStatus.EXTANT.equals(project.getProjectStatus())) {
      return SecurityRuleResult.continueAsNormal();
    }

    LOGGER.warn("User {} attempted to access deleted project with id {}",
        user.wuaId(),
        project.getId());

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.NOT_FOUND);
  }
}
