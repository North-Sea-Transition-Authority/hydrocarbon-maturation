package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.fivium.feedbackmanagementservice.client.CannotSendFeedbackException;
import uk.co.fivium.feedbackmanagementservice.client.FeedbackClientService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.notify.FeedbackEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@ExtendWith(MockitoExtension.class)
@DirtiesContext
class RemiFeedbackServiceTest {

  private static final User USER = UserTestUtil.Builder().build();
  private static final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private static final Instant CURRENT_INSTANT = Instant.now();
  private static final String CONTEXT_PATH = "/remi";
  private static final String SERVICE_URL = "http://localhost" + CONTEXT_PATH;
  private static final String EXPECTED_SUBMISSION_URL = SERVICE_URL + ReverseRouter.route(on(
      TaskListController.class).getTaskList(SUBMISSION_DETAIL.getId()));
  private static final String EXPECTED_RESOURCE_NAME_REF = "resource name";

  @Mock
  private Clock clock;

  @Mock
  private FeedbackClientService feedbackClientService;

  @Mock
  private FeedbackFormValidator feedbackFormValidator;

  @Mock
  private FeedbackEmailService feedbackEmailService;

  @Mock
  private SubmissionResourceService submissionResourceService;

  @InjectMocks
  private RemiFeedbackService remiFeedbackService;

  @Captor
  private ArgumentCaptor<Feedback> feedbackArgumentCaptor;

  private FeedbackForm form;

  @BeforeAll
  static void setupStatic() {
    var request = new MockHttpServletRequest();
    request.setContextPath(CONTEXT_PATH);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @BeforeEach
  void setup() {
    form = new FeedbackForm();
    form.setServiceRating(ServiceFeedbackRating.NEITHER);
    form.setFeedback("test comments");
  }

  @Test
  void saveFeedback_CannotSendFeedbackException_AssertDoesNotThrow() throws CannotSendFeedbackException {
    when(clock.instant()).thenReturn(CURRENT_INSTANT);
    when(feedbackClientService.saveFeedback(any(Feedback.class)))
        .thenThrow(new CannotSendFeedbackException("test exception"));

    assertDoesNotThrow(() -> remiFeedbackService.saveFeedback(form, USER));

    verify(feedbackClientService).saveFeedback(feedbackArgumentCaptor.capture());

    assertThat(feedbackArgumentCaptor.getValue()).extracting(
        Feedback::getSubmitterName,
        Feedback::getSubmitterEmail,
        Feedback::getServiceRating,
        Feedback::getComment,
        Feedback::getGivenDatetime,
        Feedback::getTransactionId,
        Feedback::getTransactionReference,
        Feedback::getTransactionLink
    ).containsExactly(
        USER.displayName(),
        USER.emailAddress(),
        form.getServiceRating().getEnumName(),
        form.getFeedback(),
        CURRENT_INSTANT,
        null,
        null,
        null
    );

    verify(feedbackEmailService, times(1))
        .sendFeedbackFailedToSendEmail(feedbackArgumentCaptor.getValue());
  }

  @Test
  void saveFeedback_WithSubmissionDetail_AssertDoesNotThrow() throws CannotSendFeedbackException {
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
        .thenReturn(EXPECTED_RESOURCE_NAME_REF);
    when(clock.instant()).thenReturn(CURRENT_INSTANT);

    assertDoesNotThrow(() -> remiFeedbackService.saveFeedback(SUBMISSION_DETAIL, form, USER));

    verify(feedbackClientService).saveFeedback(feedbackArgumentCaptor.capture());

    assertThat(feedbackArgumentCaptor.getValue()).extracting(
        Feedback::getSubmitterName,
        Feedback::getSubmitterEmail,
        Feedback::getServiceRating,
        Feedback::getComment,
        Feedback::getGivenDatetime,
        Feedback::getTransactionId,
        Feedback::getTransactionReference,
        Feedback::getTransactionLink
    ).containsExactly(
        USER.displayName(),
        USER.emailAddress(),
        form.getServiceRating().getEnumName(),
        form.getFeedback(),
        CURRENT_INSTANT,
        SUBMISSION_DETAIL.getId().toString(),
        EXPECTED_RESOURCE_NAME_REF,
        EXPECTED_SUBMISSION_URL
    );

    verifyNoInteractions(feedbackEmailService);
  }

  @Test
  void validateFeedbackForm_verifyMethodCall() {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var result = remiFeedbackService.validateFeedbackForm(form, bindingResult);

    verify(feedbackFormValidator, times(1))
        .validate(form, bindingResult);

    assertThat(bindingResult).isEqualTo(result);
  }
}