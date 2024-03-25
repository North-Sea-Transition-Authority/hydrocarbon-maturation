package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.ProjectInterceptorSecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.AbstractHandlerInterceptor;

@Component
public class ProjectHandlerInterceptor extends AbstractHandlerInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectHandlerInterceptor.class);

  private final ProjectService projectService;
  private final UserService userService;
  private final List<ProjectInterceptorSecurityRule> securityRules;

  @Autowired
  public ProjectHandlerInterceptor(ProjectService projectService,
                                   UserService userService,
                                   List<ProjectInterceptorSecurityRule> securityRules) {
    this.projectService = projectService;
    this.userService = userService;
    this.securityRules = securityRules;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean preHandle(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler
  ) throws IOException {
    var handlerMethod = (HandlerMethod) handler;
    var pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

    var annotation = findMethodOrClassAnnotation(Security.class, handlerMethod);

    if (annotation == null) {
      throw new IllegalStateException("Controllers must be annotated with @Security");
    }

    if (annotation.disable()) {
      return true;
    }

    var users = userService.getUser();
    var projectId = pathVariables.get("projectId");

    if (projectId == null) {
      LOGGER.warn("Received request with no projectId present");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    var project = projectService.getProject(UUID.fromString(projectId));
    if (project.isEmpty()) {
      LOGGER.warn("Received request with non-existent project id {}", projectId);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    for (var securityRule : securityRules) {
      var annotationObject = findMethodOrClassAnnotation(securityRule.supports(), handlerMethod);

      if (annotationObject == null) {
        continue;
      }

      var result = securityRule.check(
          annotationObject,
          request,
          response,
          users,
          project.get()
      );

      var hasRulePassed = processRedirectsAndReturnResult(result, response);
      if (!hasRulePassed) {
        return false;
      }
    }
    return true;
  }

}
