package uk.co.nstauthority.hydrocarbonmaturation.notify;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.feedback.Feedback;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class FeedbackEmailService {

  private static final String SERVICE_NAME_MNEMONIC = "SERVICE_NAME_MNEMONIC";
  private static final String SUBMITTER_NAME = "SUBMITTER_NAME";
  private static final String SUBMITTER_EMAIL = "SUBMITTER_EMAIL";
  private static final String SERVICE_RATING = "SERVICE_RATING";
  private static final String SERVICE_IMPROVEMENT = "SERVICE_IMPROVEMENT";
  private static final String DATE_TIME = "DATE_TIME";
  private static final String TRANSACTION_DETAILS = "TRANSACTION_DETAILS";

  private final NotifyEmailService notifyEmailService;
  private final TechnicalSupportConfiguration technicalSupportConfiguration;
  private final ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties;

  @Autowired
  public FeedbackEmailService(NotifyEmailService notifyEmailService,
                              TechnicalSupportConfiguration technicalSupportConfiguration,
                              ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties) {
    this.notifyEmailService = notifyEmailService;
    this.technicalSupportConfiguration = technicalSupportConfiguration;
    this.serviceBrandingConfigurationProperties = serviceBrandingConfigurationProperties;
  }

  public void sendFeedbackFailedToSendEmail(Feedback feedback) {
    var emailProperties = new EmailProperties(NotifyTemplate.FEEDBACK_FAILED_TO_SEND);
    var personalisations = emailProperties.getEmailPersonalisations();
    personalisations.put(SERVICE_NAME_MNEMONIC,
        serviceBrandingConfigurationProperties.getServiceConfigurationProperties().mnemonic());
    personalisations.put(SUBMITTER_NAME, feedback.getSubmitterName());
    personalisations.put(SUBMITTER_EMAIL, feedback.getSubmitterEmail());
    personalisations.put(SERVICE_RATING, feedback.getServiceRating());
    personalisations.put(DATE_TIME, DateUtil.formatInstantOrBlank(feedback.getGivenDatetime()));
    personalisations.put(TRANSACTION_DETAILS, getTransactionDetails(feedback));
    personalisations.put(SERVICE_IMPROVEMENT, getServiceImprovement(feedback));
    sendEmailToSupportDesk(emailProperties);
  }

  private void sendEmailToSupportDesk(EmailProperties emailProperties) {
    notifyEmailService.sendEmail(emailProperties,
        technicalSupportConfiguration.getTechnicalSupportConfigurationProperties().emailAddress());
  }

  private String getTransactionDetails(Feedback feedback) {
    if (StringUtils.isBlank(feedback.getTransactionId())) {
      return "";
    } else {
      return "Transaction ID: " + feedback.getTransactionId() +
          "\nTransaction reference: " +
          feedback.getTransactionReference() +
          "\nTransaction link: " +
          feedback.getTransactionLink();
    }
  }

  private String getServiceImprovement(Feedback feedback) {
    if (StringUtils.isBlank(feedback.getComment())) {
      return "";
    } else {
      return "Service improvement: " + feedback.getComment();
    }
  }
}