package uk.co.nstauthority.hydrocarbonmaturation.notify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.feedback.Feedback;
import uk.co.nstauthority.hydrocarbonmaturation.feedback.ServiceFeedbackRating;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ExtendWith(MockitoExtension.class)
class OldFeedbackEmailServiceTest {

  private static final String SUBMITTER_EMAIL_VALUE = "test@email.com";
  private static final String SERVICE_FEEDBACK_RATING = ServiceFeedbackRating.DISSATISFIED.name();
  private static final String TRANSACTION_REFERENCE = "transaction ref";
  private static final String TRANSACTION_LINK = ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null));
  private static final String COMMENT_VALUE = "comment";
  private static final Instant GIVEN_INSTANT = Instant.now();
  private static final String SUBMITTER_NAME = "Name";
  private static final String SERVICE_NAME_MNEMONIC = "REMI";
  private static final String SERVICE_DESK_EMAIL = "desk@email.uk";
  @Mock
  private NotifyEmailService notifyEmailService;
  @Mock
  private TechnicalSupportConfiguration technicalSupportConfiguration;
  @Mock
  private ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties;
  @InjectMocks
  private OldFeedbackEmailService underTest;

  @Captor
  private ArgumentCaptor<EmailProperties> emailPropertiesArgumentCaptor;

  @Test
  void sendFeedbackFailedToSendEmail_withTransactionAndCommentInformation() {
    var feedback = getFilledFeedback();

    when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
        .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME_MNEMONIC));
    when(technicalSupportConfiguration.getTechnicalSupportConfigurationProperties())
        .thenReturn(new TechnicalSupportConfigurationProperties("", "", SERVICE_DESK_EMAIL));

    underTest.sendFeedbackFailedToSendEmail(feedback);

    verify(notifyEmailService).sendEmail(emailPropertiesArgumentCaptor.capture(), eq(SERVICE_DESK_EMAIL));

    assertThat(emailPropertiesArgumentCaptor.getValue())
        .extracting(
            EmailProperties::getTemplate,
            EmailProperties::getEmailPersonalisations)
        .containsExactly(
            NotifyTemplate.FEEDBACK_FAILED_TO_SEND,
            Map.of(
                "SERVICE_IMPROVEMENT", "Service improvement: %s".formatted(COMMENT_VALUE),
                "TRANSACTION_DETAILS", "Transaction ID: %s\nTransaction reference: %s\nTransaction link: %s"
                    .formatted(feedback.getTransactionId(), TRANSACTION_REFERENCE, TRANSACTION_LINK),
                "DATE_TIME", DateUtil.formatInstantOrBlank(GIVEN_INSTANT),
                "SUBMITTER_EMAIL", SUBMITTER_EMAIL_VALUE,
                "SUBMITTER_NAME", SUBMITTER_NAME,
                "SERVICE_RATING", SERVICE_FEEDBACK_RATING,
                "SERVICE_NAME_MNEMONIC", SERVICE_NAME_MNEMONIC,
                "TEST_EMAIL", "no"
            )
        );
  }

  @Test
  void sendFeedbackFailedToSendEmail_withoutTransactionAndCommentInformation() {
    var feedback = getFilledFeedback();
    feedback.setTransactionLink(null);
    feedback.setTransactionId(null);
    feedback.setComment(null);
    feedback.setTransactionReference(null);
    when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
        .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME_MNEMONIC));
    when(technicalSupportConfiguration.getTechnicalSupportConfigurationProperties())
        .thenReturn(new TechnicalSupportConfigurationProperties("", "", SERVICE_DESK_EMAIL));

    underTest.sendFeedbackFailedToSendEmail(feedback);

    verify(notifyEmailService).sendEmail(emailPropertiesArgumentCaptor.capture(), eq(SERVICE_DESK_EMAIL));

    assertThat(emailPropertiesArgumentCaptor.getValue())
        .extracting(
            EmailProperties::getTemplate,
            EmailProperties::getEmailPersonalisations)
        .containsExactly(
            NotifyTemplate.FEEDBACK_FAILED_TO_SEND,
            Map.of(
                "SERVICE_IMPROVEMENT", "",
                "TRANSACTION_DETAILS", "",
                "DATE_TIME", DateUtil.formatInstantOrBlank(GIVEN_INSTANT),
                "SUBMITTER_EMAIL", SUBMITTER_EMAIL_VALUE,
                "SUBMITTER_NAME", SUBMITTER_NAME,
                "SERVICE_RATING", SERVICE_FEEDBACK_RATING,
                "SERVICE_NAME_MNEMONIC", SERVICE_NAME_MNEMONIC,
                "TEST_EMAIL", "no"
            )
        );
  }

  private Feedback getFilledFeedback() {
    var feedback = new Feedback();
    feedback.setTransactionId(UUID.randomUUID().toString());
    feedback.setTransactionReference(TRANSACTION_REFERENCE);
    feedback.setTransactionLink(TRANSACTION_LINK);
    feedback.setServiceRating(SERVICE_FEEDBACK_RATING);
    feedback.setComment(COMMENT_VALUE);
    feedback.setGivenDatetime(GIVEN_INSTANT);
    feedback.setSubmitterEmail(SUBMITTER_EMAIL_VALUE);
    feedback.setSubmitterName(SUBMITTER_NAME);
    return  feedback;
  }
}