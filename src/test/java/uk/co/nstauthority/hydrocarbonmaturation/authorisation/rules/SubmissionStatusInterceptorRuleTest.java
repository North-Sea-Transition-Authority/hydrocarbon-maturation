package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

class SubmissionStatusInterceptorRuleTest extends AbstractInterceptorRuleTest {

  private SubmissionStatusInterceptorRule rule;

  @BeforeEach
  void setUp() {
    rule = new SubmissionStatusInterceptorRule();
  }

  @Test
  void validate_draft() {
    when(submissionDetail.getStatus()).thenReturn(SubmissionDetailStatus.DRAFT);

    var annotation = getAnnotation(
        TestControllerDraft.class,
        HasSubmissionStatus.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @Test
  void validate_draftOrSubmitted() {
    when(submissionDetail.getStatus()).thenReturn(SubmissionDetailStatus.DRAFT);

    var annotation = getAnnotation(
        TestControllerDraftOrSubmitted.class,
        HasSubmissionStatus.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @Test
  void validate_approved() {
    when(submissionDetail.getStatus()).thenReturn(SubmissionDetailStatus.ACCEPTED);

    var annotation = getAnnotation(
        TestControllerDraftOrSubmitted.class,
        HasSubmissionStatus.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Controller
  @RequestMapping("/route1")
  @HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
  private static class TestControllerDraft {
    @GetMapping
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("/route2")
  @HasSubmissionStatus(statuses = {SubmissionDetailStatus.DRAFT, SubmissionDetailStatus.SUBMITTED})
  private static class TestControllerDraftOrSubmitted {
    @GetMapping
    String get() {
      return "some data";
    }
  }
}
