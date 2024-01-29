package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Component
public class SubmissionStatusInterceptorRule implements SubmissionInterceptorSecurityRule {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionStatusInterceptorRule.class);

  @Override
  public Class<? extends Annotation> supports() {
    return HasSubmissionStatus.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  User users,
                                  SubmissionDetail submissionDetail) {
    var expectedStatuses = ((HasSubmissionStatus) annotation).statuses();
    var expectedStatusList = CollectionUtils.arrayToList(expectedStatuses);
    var submissionStatus = submissionDetail.getStatus();

    if (expectedStatusList.contains(submissionStatus)) {
      return SecurityRuleResult.continueAsNormal();
    }

    var userId = users.wuaId();
    var submissionDetailId = submissionDetail.getId();

    LOGGER.warn("User {} attempted to access submission {} with status {}. Expected status(s) {}",
        userId,
        submissionDetailId,
        submissionStatus,
        expectedStatuses);

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
  }
}
