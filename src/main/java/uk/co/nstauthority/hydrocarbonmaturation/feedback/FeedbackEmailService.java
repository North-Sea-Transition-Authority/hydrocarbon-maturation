package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.digitalnotificationlibrary.core.notification.email.EmailRecipient;
import uk.co.nstauthority.hydrocarbonmaturation.email.EmailService;
import uk.co.nstauthority.hydrocarbonmaturation.email.NotifyTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class FeedbackEmailService {

  static final String SUBMITTER_NAME_KEY = "SUBMITTER_NAME";
  static final String SUBMITTER_EMAIL_KEY = "SUBMITTER_EMAIL";
  static final String SERVICE_RATING_KEY = "SERVICE_RATING";
  static final String SERVICE_IMPROVEMENT_KEY = "SERVICE_IMPROVEMENT";
  static final String DATE_TIME_KEY = "DATE_TIME";
  static final String TRANSACTION_DETAILS_KEY = "TRANSACTION_DETAILS";

  private final EmailService emailService;
  private final TechnicalSupportConfiguration technicalSupportConfiguration;

  @Autowired
  public FeedbackEmailService(EmailService emailService, TechnicalSupportConfiguration technicalSupportConfiguration) {
    this.emailService = emailService;
    this.technicalSupportConfiguration = technicalSupportConfiguration;
  }

  public void sendFeedbackFailedToSendEmail(Feedback feedback) {
    var mergedTemplate = emailService.getTemplate(NotifyTemplate.FEEDBACK_FAILED_TO_SEND)
        .withMailMergeField(SUBMITTER_NAME_KEY, feedback.getSubmitterName())
        .withMailMergeField(SUBMITTER_EMAIL_KEY, feedback.getSubmitterEmail())
        .withMailMergeField(SERVICE_RATING_KEY, feedback.getServiceRating())
        .withMailMergeField(DATE_TIME_KEY, DateUtil.formatInstantOrBlank(feedback.getGivenDatetime()))
        .withMailMergeField(TRANSACTION_DETAILS_KEY, getTransactionDetails(feedback))
        .withMailMergeField(SERVICE_IMPROVEMENT_KEY, getServiceImprovement(feedback))
        .merge();

    emailService.sendEmail(
        mergedTemplate,
        EmailRecipient.directEmailAddress(getSupportEmail()),
        feedback
    );
  }

  private String getSupportEmail() {
    return technicalSupportConfiguration.getTechnicalSupportConfigurationProperties().emailAddress();
  }

  private String getTransactionDetails(Feedback feedback) {
    if (StringUtils.isBlank(feedback.getTransactionId())) {
      return "";
    }

    return """
        Transaction ID: %s
        Transaction reference: %s
        Transaction link: %s
        """.formatted(
        feedback.getTransactionId(),
        feedback.getTransactionReference(),
        feedback.getTransactionLink());

  }

  private String getServiceImprovement(Feedback feedback) {
    if (StringUtils.isBlank(feedback.getComment())) {
      return "";
    }
    return "Service improvement: " + feedback.getComment();
  }
}
