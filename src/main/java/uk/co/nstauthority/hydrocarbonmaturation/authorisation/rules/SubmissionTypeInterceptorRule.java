package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
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
                                  ServiceUserDetail userDetails,
                                  SubmissionDetail submissionDetail) {
    var allowedSubmissionTypes = ((HasSubmissionType) annotation).allowedSubmissionTypes();

    if (Arrays.stream(allowedSubmissionTypes.getSubmissionTypesAllowed())
        .anyMatch(submissionType -> submissionType == submissionDetail.getSubmissionType())) {
      return SecurityRuleResult.continueAsNormal();
    }

    var submissionDetailId = submissionDetail.getId();

    LOGGER.warn("User {} attempted to access submission {} form page unavailable for the current submission type {} ",
        userDetails.wuaId(),
        submissionDetailId,
        submissionDetail.getSubmissionType().getDisplayName());

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
  }
}
