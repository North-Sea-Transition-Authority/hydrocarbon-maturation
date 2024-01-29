package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Component
public class SubmissionTypeInterceptorRule implements SubmissionInterceptorSecurityRule {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionTypeInterceptorRule.class);

  @Override
  public Class<? extends Annotation> supports() {
    return HasSubmissionType.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  User users,
                                  SubmissionDetail submissionDetail) {
    var hasSubmissionType = (HasSubmissionType) annotation;
    var submissionTypes = hasSubmissionType.submissionTypes();
    var mode = hasSubmissionType.mode();

    var actualSubmissionTypes = Arrays.stream(submissionTypes)
        .map(SecuritySubmissionTypes::getSubmissionTypesAllowed).toList()
        .stream()
        .flatMap(Arrays::stream).toList();

    if ((mode == HasSubmissionType.Mode.INCLUDE && actualSubmissionTypes.contains(submissionDetail.getSubmissionType()))
        ||
        (mode == HasSubmissionType.Mode.EXCLUDE && !actualSubmissionTypes.contains(submissionDetail.getSubmissionType()))) {
      return SecurityRuleResult.continueAsNormal();
    }

    var submissionDetailId = submissionDetail.getId();

    LOGGER.warn("User {} attempted to access submission {} form page unavailable for the current submission type: {} ",
        users.wuaId(),
        submissionDetailId,
        submissionDetail.getSubmissionType().getTypeName());

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
  }
}
