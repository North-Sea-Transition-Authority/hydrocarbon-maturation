package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionInterceptorSecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.AbstractHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Component
public class SubmissionHandlerInterceptor extends AbstractHandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionHandlerInterceptor.class);

  private final SubmissionDetailService submissionDetailService;
  private final UserService userService;
  private final List<SubmissionInterceptorSecurityRule> securityRules;

  @Autowired
  public SubmissionHandlerInterceptor(SubmissionDetailService submissionDetailService,
                                      UserService userService,
                                      List<SubmissionInterceptorSecurityRule> securityRules) {
    this.submissionDetailService = submissionDetailService;
    this.userService = userService;
    this.securityRules = securityRules
        .stream()
        .sorted(Comparator.comparingInt(r -> EndpointInterceptorOrder.orderForSubmission(r.getClass())))
        .toList();
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
    var submissionDetailId = pathVariables.get("submissionDetailId");

    if (submissionDetailId == null) {
      LOGGER.warn("Received request with no submissionDetailId present");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    var submissionDetail = submissionDetailService.getSubmissionDetail(UUID.fromString(submissionDetailId));
    if (submissionDetail.isEmpty()) {
      LOGGER.warn("Received request with non-existent submission detail id {}", submissionDetailId);
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
          submissionDetail.get()
      );

      var hasRulePassed = processRedirectsAndReturnResult(result, response);
      if (!hasRulePassed) {
        return false;
      }
    }
    return true;
  }

}
