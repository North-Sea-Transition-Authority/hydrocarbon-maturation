package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsLatestExtantSubmissionDetailVersion;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Component
public class SubmissionDetailVersionInterceptorRule implements SubmissionInterceptorSecurityRule {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionDetailVersionInterceptorRule.class);

  private final SubmissionDetailService submissionDetailService;

  @Autowired
  SubmissionDetailVersionInterceptorRule(SubmissionDetailService submissionDetailService) {
    this.submissionDetailService = submissionDetailService;
  }

  @Override
  public Class<? extends Annotation> supports() {
    return IsLatestExtantSubmissionDetailVersion.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation, HttpServletRequest request, HttpServletResponse response,
                                  ServiceUserDetail userDetails, SubmissionDetail submissionDetail) {
    if (submissionDetail.getVersionNumber() == 1 && submissionDetail.getStatus() == SubmissionDetailStatus.DRAFT) {
      // can always access the initial draft version (as long as other rules pass)
      return SecurityRuleResult.continueAsNormal();
    } else if (BooleanUtils.isTrue(submissionDetail.getSubmission().isWithdrawn())) {
      LOGGER.warn("User {} attempted to access submission detail {} which is for a withdrawn submission {}",
          userDetails.wuaId(), submissionDetail.getId(), submissionDetail.getSubmission().getId());
      return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
    }

    if (isLatestExtantSubmissionDetailOnSubmission(submissionDetail)) {
      return SecurityRuleResult.continueAsNormal();
    } else {
      LOGGER.warn("User {} attempted to access submission detail {} which is not the latest extant version of the submission {}",
          userDetails.wuaId(), submissionDetail.getId(), submissionDetail.getSubmission().getId());
      return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
    }
  }

  private boolean isLatestExtantSubmissionDetailOnSubmission(SubmissionDetail submissionDetail) {
    //ensures the submission detail is the latest accessible version on the submission for its status
    return switch (submissionDetail.getStatus()) {
      case ACCEPTED, SUBMITTED -> submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
          submissionDetail,
          Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED));
      case DRAFT -> submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
          submissionDetail,
          Set.of(SubmissionDetailStatus.DRAFT, SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED)
      );
      case DELETED -> false;
    };
  }
}
