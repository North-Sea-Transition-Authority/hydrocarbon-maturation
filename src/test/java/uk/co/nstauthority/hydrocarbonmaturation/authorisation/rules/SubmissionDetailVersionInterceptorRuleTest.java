package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsLatestExtantSubmissionDetailVersion;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailVersionInterceptorRuleTest extends AbstractInterceptorRuleTest{
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private SubmissionDetailVersionInterceptorRule submissionDetailVersionInterceptorRule;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_assertsAnnotation() {
    assertThat(submissionDetailVersionInterceptorRule.supports()).isEqualTo(IsLatestExtantSubmissionDetailVersion.class);
  }

  @Test
  void check_whenInitialDraft_assertPassed() {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setVersionNumber(1);

    var interceptorResult = submissionDetailVersionInterceptorRule.check(
        IsLatestExtantSubmissionDetailVersion.class,
        request,
        response,
        UserTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @Test
  void check_whenIsSubmitted_andWithdrawnParent_assertFailed() {
    submissionDetail.getSubmission().setWithdrawn(true);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setVersionNumber(1);

    var interceptorResult = submissionDetailVersionInterceptorRule.check(
        IsLatestExtantSubmissionDetailVersion.class,
        request,
        response,
        UserTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void check_whenIsLatestExtant_assertPassed() {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail.setVersionNumber(4);
    when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(submissionDetail,
        Set.of(SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.ACCEPTED)))
        .thenReturn(true);

    var interceptorResult = submissionDetailVersionInterceptorRule.check(
        IsLatestExtantSubmissionDetailVersion.class,
        request,
        response,
        UserTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @Test
  void check_whenIsNotLatestExtant_assertFailed() {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setVersionNumber(4);
    when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(submissionDetail,
        Set.of(SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.ACCEPTED)))
        .thenReturn(false);

    var interceptorResult = submissionDetailVersionInterceptorRule.check(
        IsLatestExtantSubmissionDetailVersion.class,
        request,
        response,
        UserTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void check_whenIsNotLatestExtantDraft_assertFailed() {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setVersionNumber(4);
    when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(submissionDetail,
        Set.of(SubmissionDetailStatus.DRAFT, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.ACCEPTED)))
        .thenReturn(false);

    var interceptorResult = submissionDetailVersionInterceptorRule.check(
        IsLatestExtantSubmissionDetailVersion.class,
        request,
        response,
        UserTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void check_whenIsDeleted_assertFailed() {
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    submissionDetail.setVersionNumber(4);

    var interceptorResult = submissionDetailVersionInterceptorRule.check(
        IsLatestExtantSubmissionDetailVersion.class,
        request,
        response,
        UserTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void check_whenIsLatestExtantSubmitted_assertPassed() {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setVersionNumber(4);
    when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(submissionDetail,
        Set.of(SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.ACCEPTED)))
        .thenReturn(true);

    var interceptorResult = submissionDetailVersionInterceptorRule.check(
        IsLatestExtantSubmissionDetailVersion.class,
        request,
        response,
        UserTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

}