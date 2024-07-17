package uk.co.nstauthority.hydrocarbonmaturation.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.digitalnotificationlibrary.core.notification.DomainReference;
import uk.co.fivium.digitalnotificationlibrary.core.notification.MergedTemplate;
import uk.co.fivium.digitalnotificationlibrary.core.notification.NotificationLibraryClient;
import uk.co.fivium.digitalnotificationlibrary.core.notification.email.EmailNotification;
import uk.co.fivium.digitalnotificationlibrary.core.notification.email.EmailRecipient;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;

@Service
public class EmailService {

  static final String SUBJECT_PREFIX_KEY = "SUBJECT_PREFIX";
  static final String SALUTATION_KEY = "SALUTATION";
  static final String CLOSING_REMARK_KEY = "CLOSING_REMARK";
  static final String SERVICE_NAME_MNEMONIC_KEY = "SERVICE_NAME_MNEMONIC";

  private final NotificationLibraryClient notificationLibraryClient;
  private final ServiceBrandingConfigurationProperties brandingConfigurationProperties;

  @Autowired
  public EmailService(NotificationLibraryClient notificationLibraryClient,
                      ServiceBrandingConfigurationProperties brandingConfigurationProperties) {
    this.notificationLibraryClient = notificationLibraryClient;
    this.brandingConfigurationProperties = brandingConfigurationProperties;
  }

  public MergedTemplate.MergedTemplateBuilder getTemplate(NotifyTemplate notifyTemplate) {
    var serviceBranding = brandingConfigurationProperties.getServiceConfigurationProperties();

    var subjectPrefix = notificationLibraryClient.isRunningTestMode()
        ? "***TEST***"
        : "";

    return notificationLibraryClient.getTemplate(notifyTemplate.getTemplateId())
        .withMailMergeField(SUBJECT_PREFIX_KEY, subjectPrefix)
        .withMailMergeField(SALUTATION_KEY, "Dear")
        .withMailMergeField(CLOSING_REMARK_KEY, "Kind regards")
        .withMailMergeField(SERVICE_NAME_MNEMONIC_KEY, serviceBranding.mnemonic());
  }

  public EmailNotification sendEmail(MergedTemplate mergedTemplate, EmailRecipient emailRecipient,
                                     DomainReference domainReference) {
    return notificationLibraryClient.sendEmail(
        mergedTemplate,
        emailRecipient,
        domainReference,
        CorrelationIdUtil.getCorrelationIdFromMdc()
    );
  }
}
