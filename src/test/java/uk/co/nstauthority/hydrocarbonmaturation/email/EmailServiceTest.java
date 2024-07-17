package uk.co.nstauthority.hydrocarbonmaturation.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.digitalnotificationlibrary.core.notification.MailMergeField;
import uk.co.fivium.digitalnotificationlibrary.core.notification.MergedTemplate;
import uk.co.fivium.digitalnotificationlibrary.core.notification.NotificationLibraryClient;
import uk.co.fivium.digitalnotificationlibrary.core.notification.Template;
import uk.co.fivium.digitalnotificationlibrary.core.notification.TemplateType;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  private static final NotifyTemplate NOTIFY_TEMPLATE = NotifyTemplate.SUBMISSION_CONFIRMATION_NOTIFICATION;
  private static final Template TEMPLATE = new Template(
      NOTIFY_TEMPLATE.getTemplateId(),
      TemplateType.EMAIL,
      Set.of(),
      Template.VerificationStatus.CONFIRMED_NOTIFY_TEMPLATE
  );

  @Mock
  private NotificationLibraryClient notificationLibraryClient;

  @Mock
  private ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties;

  @InjectMocks
  private EmailService emailService;

  @Nested
  class getTemplate {

    private static final String NAME = "NAME";
    private static final String MNEMONIC = "MNEMONIC";

    @BeforeEach
    void setUp() {
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties(NAME, MNEMONIC));
    }

    @Test
    void whenTestMode_thenTestPrefix() {
      when(notificationLibraryClient.isRunningTestMode())
          .thenReturn(true);
      when(notificationLibraryClient.getTemplate(NOTIFY_TEMPLATE.getTemplateId()))
          .thenReturn(TEMPLATE);

      var generatedTemplate = emailService.getTemplate(NOTIFY_TEMPLATE).merge();
      assertThat(generatedTemplate.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(EmailService.SALUTATION_KEY, "Dear"),
              Tuple.tuple(EmailService.CLOSING_REMARK_KEY, "Kind regards"),
              Tuple.tuple(EmailService.SERVICE_NAME_MNEMONIC_KEY, MNEMONIC),
              Tuple.tuple(EmailService.SUBJECT_PREFIX_KEY, "***TEST***")
          );
    }

    @Test
    void whenNotTestMode_thenNoTestPrefix() {
      when(notificationLibraryClient.isRunningTestMode())
          .thenReturn(false);
      when(notificationLibraryClient.getTemplate(NOTIFY_TEMPLATE.getTemplateId()))
          .thenReturn(TEMPLATE);

      var generatedTemplate = emailService.getTemplate(NOTIFY_TEMPLATE).merge();
      assertThat(generatedTemplate.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(EmailService.SALUTATION_KEY, "Dear"),
              Tuple.tuple(EmailService.CLOSING_REMARK_KEY, "Kind regards"),
              Tuple.tuple(EmailService.SERVICE_NAME_MNEMONIC_KEY, MNEMONIC),
              Tuple.tuple(EmailService.SUBJECT_PREFIX_KEY, "")
          );
    }
  }

  @Test
  void sendEmail() {
    var mergedTemplate = MergedTemplate.builder(TEMPLATE).merge();
    var energyPortalUserDto = EnergyPortalUserDtoTestUtil.Builder().build();
    var submissionDetail = new SubmissionDetail();

    var correlationId = "some-correlation-id-for-this-epic-test";
    CorrelationIdUtil.setCorrelationIdOnMdc(correlationId);

    emailService.sendEmail(mergedTemplate, energyPortalUserDto, submissionDetail);

    verify(notificationLibraryClient, times(1))
        .sendEmail(mergedTemplate, energyPortalUserDto, submissionDetail, correlationId);
  }
}