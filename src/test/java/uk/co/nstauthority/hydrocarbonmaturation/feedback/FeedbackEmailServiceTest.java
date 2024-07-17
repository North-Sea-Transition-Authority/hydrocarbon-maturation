package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackEmailService.DATE_TIME_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackEmailService.SERVICE_IMPROVEMENT_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackEmailService.SERVICE_RATING_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackEmailService.SUBMITTER_EMAIL_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackEmailService.SUBMITTER_NAME_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackEmailService.TRANSACTION_DETAILS_KEY;

import java.time.Instant;
import java.util.Set;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.digitalnotificationlibrary.core.notification.MailMergeField;
import uk.co.fivium.digitalnotificationlibrary.core.notification.MergedTemplate;
import uk.co.fivium.digitalnotificationlibrary.core.notification.Template;
import uk.co.fivium.digitalnotificationlibrary.core.notification.TemplateType;
import uk.co.fivium.digitalnotificationlibrary.core.notification.email.EmailRecipient;
import uk.co.nstauthority.hydrocarbonmaturation.email.EmailService;
import uk.co.nstauthority.hydrocarbonmaturation.email.NotifyTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties;

@ExtendWith(MockitoExtension.class)
class FeedbackEmailServiceTest {

  private static final String SUBMITTER_NAME = "Alex Brown";
  private static final String SUBMITTER_EMAIL = "alex@brown.co.uk";
  private static final String SERVICE_RATING = "Satisfactory";
  private static final Instant DATE_TIME_INSTANT = Instant.parse("2002-01-11T12:00:00.00Z");
  private static final String DATE_TIME_STRING = "11 Jan 2002 12:00 GMT";
  private static final String TRANSACTION_ID = "ab110102";
  private static final String TRANSACTION_REFERENCE = "REFERENCE";
  private static final String TRANSACTION_LINK = "link.com";
  private static final String COMMENT = "I love this service";
  private static final String SUPPORT_EMAIL = "support@gmail.com";

  private static final MergedTemplate.MergedTemplateBuilder MERGED_TEMPLATE_BUILDER = MergedTemplate.builder(
      new Template(
          NotifyTemplate.FEEDBACK_FAILED_TO_SEND.getTemplateId(),
          TemplateType.EMAIL,
          Set.of(),
          Template.VerificationStatus.CONFIRMED_NOTIFY_TEMPLATE)
  );

  private final ArgumentCaptor<MergedTemplate> mergedTemplateCaptor = ArgumentCaptor.forClass(MergedTemplate.class);
  private final ArgumentCaptor<EmailRecipient> emailRecipientCaptor = ArgumentCaptor.forClass(EmailRecipient.class);

  @Mock
  private EmailService emailService;

  @Mock
  private TechnicalSupportConfiguration technicalSupportConfiguration;

  @InjectMocks
  private FeedbackEmailService feedbackEmailService;

  @Test
  void sendFeedbackEmail_allFieldsPopulated() {
    mockTemplate();
    mockSupportEmail();

    var feedback = getPopulatedFeedback();
    feedbackEmailService.sendFeedbackFailedToSendEmail(feedback);

    verify(emailService, times(1))
        .sendEmail(
            mergedTemplateCaptor.capture(),
            emailRecipientCaptor.capture(),
            eq(feedback)
        );

    assertThat(emailRecipientCaptor.getValue().getEmailAddress())
        .isEqualTo(SUPPORT_EMAIL);

    var mergedTemplate = mergedTemplateCaptor.getValue();
    assertThat(mergedTemplate.getTemplate().notifyTemplateId())
        .isEqualTo(NotifyTemplate.FEEDBACK_FAILED_TO_SEND.getTemplateId());
    assertThat(mergedTemplate.getMailMergeFields())
        .extracting(MailMergeField::name, MailMergeField::value)
        .containsExactlyInAnyOrder(
            Tuple.tuple(SUBMITTER_NAME_KEY, SUBMITTER_NAME),
            Tuple.tuple(SUBMITTER_EMAIL_KEY, SUBMITTER_EMAIL),
            Tuple.tuple(SERVICE_RATING_KEY, SERVICE_RATING),
            Tuple.tuple(DATE_TIME_KEY, DATE_TIME_STRING),
            Tuple.tuple(TRANSACTION_DETAILS_KEY, getExpectedTransactionDetails()),
            Tuple.tuple(SERVICE_IMPROVEMENT_KEY, getExpectedServiceImprovement())
        );
  }

  @Test
  void sendFeedbackEmail_noTransactionDetails_noServiceImprovement() {
    mockTemplate();
    mockSupportEmail();

    var feedback = getFeedbackWithoutTransactionOrServiceImprovement();
    feedbackEmailService.sendFeedbackFailedToSendEmail(feedback);

    verify(emailService, times(1))
        .sendEmail(
            mergedTemplateCaptor.capture(),
            emailRecipientCaptor.capture(),
            eq(feedback)
        );

    assertThat(emailRecipientCaptor.getValue().getEmailAddress())
        .isEqualTo(SUPPORT_EMAIL);

    var mergedTemplate = mergedTemplateCaptor.getValue();
    assertThat(mergedTemplate.getTemplate().notifyTemplateId())
        .isEqualTo(NotifyTemplate.FEEDBACK_FAILED_TO_SEND.getTemplateId());
    assertThat(mergedTemplate.getMailMergeFields())
        .extracting(MailMergeField::name, MailMergeField::value)
        .containsExactlyInAnyOrder(
            Tuple.tuple(SUBMITTER_NAME_KEY, SUBMITTER_NAME),
            Tuple.tuple(SUBMITTER_EMAIL_KEY, SUBMITTER_EMAIL),
            Tuple.tuple(SERVICE_RATING_KEY, SERVICE_RATING),
            Tuple.tuple(DATE_TIME_KEY, DATE_TIME_STRING),
            Tuple.tuple(TRANSACTION_DETAILS_KEY, ""),
            Tuple.tuple(SERVICE_IMPROVEMENT_KEY, "")
        );
  }

  private void mockSupportEmail() {
    when(technicalSupportConfiguration.getTechnicalSupportConfigurationProperties())
        .thenReturn(new TechnicalSupportConfigurationProperties("", "", SUPPORT_EMAIL));
  }

  private void mockTemplate() {
    when(emailService.getTemplate(NotifyTemplate.FEEDBACK_FAILED_TO_SEND))
        .thenReturn(MERGED_TEMPLATE_BUILDER);
  }

  private Feedback getPopulatedFeedback() {
    var feedback = new Feedback();
    feedback.setSubmitterName(SUBMITTER_NAME);
    feedback.setSubmitterEmail(SUBMITTER_EMAIL);
    feedback.setServiceRating(SERVICE_RATING);
    feedback.setComment(COMMENT);
    feedback.setTransactionId(TRANSACTION_ID);
    feedback.setTransactionReference(TRANSACTION_REFERENCE);
    feedback.setTransactionLink(TRANSACTION_LINK);
    feedback.setGivenDatetime(DATE_TIME_INSTANT);
    return feedback;
  }

  private Feedback getFeedbackWithoutTransactionOrServiceImprovement() {
    var feedback = new Feedback();
    feedback.setSubmitterName(SUBMITTER_NAME);
    feedback.setSubmitterEmail(SUBMITTER_EMAIL);
    feedback.setServiceRating(SERVICE_RATING);
    feedback.setGivenDatetime(DATE_TIME_INSTANT);
    return feedback;
  }

  private String getExpectedTransactionDetails() {
    return """
        Transaction ID: %s
        Transaction reference: %s
        Transaction link: %s
        """.formatted(TRANSACTION_ID, TRANSACTION_REFERENCE, TRANSACTION_LINK);
  }

  private String getExpectedServiceImprovement() {
    return "Service improvement: %s".formatted(COMMENT);
  }
}