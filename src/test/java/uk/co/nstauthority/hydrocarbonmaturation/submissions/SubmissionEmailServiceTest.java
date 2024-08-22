package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService.HAS_LICENCES_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService.LICENCES_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService.RECIPIENT_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService.REGULATOR_ORGANISATION_NAME_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService.RESOURCE_NAME_QUAD_BLOCK_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService.REVIEW_URL_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService.SUBMISSION_TYPE_WITH_ACRONYM_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService.SYSTEM_OF_RECORD_KEY;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.fivium.digitalnotificationlibrary.core.notification.MailMergeField;
import uk.co.fivium.digitalnotificationlibrary.core.notification.MergedTemplate;
import uk.co.fivium.digitalnotificationlibrary.core.notification.Template;
import uk.co.fivium.digitalnotificationlibrary.core.notification.TemplateType;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.email.EmailService;
import uk.co.nstauthority.hydrocarbonmaturation.email.NotifyTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.RemiAbsoluteUrlUtil;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicencePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@ExtendWith(MockitoExtension.class)
class SubmissionEmailServiceTest {

  private static final String LICENCE_REF_1 = "AL6";
  private static final String LICENCE_REF_2 = "AL9";
  private static final String RESOURCE_NAME = "Alex's resource";
  private static final String SUBMISSION_TYPE_WITH_ACRONYM = "field ... fdp";
  private static final String CUSTOMER_MNEMONIC = "Alex Brown AB";
  private static final List<EnergyPortalUserDto> ENERGY_PORTAL_USERS = List.of(
      EnergyPortalUserDtoTestUtil.Builder().build(),
      EnergyPortalUserDtoTestUtil.Builder().withForename("different").build()
  );
  private static final EnergyPortalUserDto ENERGY_PORTAL_USER = EnergyPortalUserDtoTestUtil.Builder().build();

  private final ArgumentCaptor<MergedTemplate> mergedTemplateCaptor = ArgumentCaptor.forClass(MergedTemplate.class);

  @Mock
  private EmailService emailService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  @Mock
  private SubmissionResourceService submissionResourceService;

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private LicencePersistenceService licencePersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private EnergyPortalUserService energyPortalUserService;

  @Mock
  private ServiceBrandingConfigurationProperties brandingConfigurationProperties;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @Mock
  private IndustryTeamService industryTeamService;

  @InjectMocks
  private SubmissionEmailService submissionEmailService;

  private SubmissionDetail submissionDetail;
  private final RegulatorManagedInformation regulatorManagedInformation = new RegulatorManagedInformation();

  @BeforeEach
  void setUp() {
    var request = new MockHttpServletRequest();
    request.setContextPath("/remi");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Nested
  class SendSubmissionConfirmationEmails {

    @BeforeEach
    void setUp() {
      var resource = submissionDetail.getSubmission().getResource();
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
          .thenReturn(regulatorManagedInformation);
    }

    @Test
    void whenNoResponsibleTeamUsers_noEmailsSent() {
      when(responsibleTeamService.getResponsibleTeamUsers(regulatorManagedInformation.getResponsibleTeamType()))
          .thenReturn(List.of());

      submissionEmailService.sendSubmissionConfirmationEmails(submissionDetail);

      verify(emailService, never())
          .sendEmail(any(), any(), any());
    }

    @Test
    void whenUpdateToSubmission_useUpdateConfirmationTemplate() {
      // when submission is an update
      submissionDetail.setVersionNumber(2);

      when(responsibleTeamService.getResponsibleTeamUsers(regulatorManagedInformation.getResponsibleTeamType()))
          .thenReturn(ENERGY_PORTAL_USERS);

      var updateNotifyTemplate = NotifyTemplate.SUBMISSION_UPDATE_CONFIRMATION;
      when(emailService.getTemplate(updateNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(updateNotifyTemplate)));

      mockGenericResourceInformation();
      mockGetDisplayableLicences();

      submissionEmailService.sendSubmissionConfirmationEmails(submissionDetail);

      var firstRecipient = ENERGY_PORTAL_USERS.get(0);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(firstRecipient),
              eq(submissionDetail)
          );
      var firstEmail = mergedTemplateCaptor.getValue();
      assertThat(firstEmail.getTemplate().notifyTemplateId())
          .isEqualTo(updateNotifyTemplate.getTemplateId());
      assertThat(firstEmail.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, "AL6, AL9"),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(HAS_LICENCES_KEY, true),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(firstRecipient.forename(), firstRecipient.surname()))
          );

      var secondRecipient = ENERGY_PORTAL_USERS.get(1);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(secondRecipient),
              eq(submissionDetail)
          );
      var secondEmail = mergedTemplateCaptor.getValue();
      assertThat(secondEmail.getTemplate().notifyTemplateId())
          .isEqualTo(updateNotifyTemplate.getTemplateId());
      assertThat(secondEmail.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, "AL6, AL9"),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(HAS_LICENCES_KEY, true),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(secondRecipient.forename(), secondRecipient.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }

    @Test
    void whenNewSubmission_useSubmissionConfirmationTemplate() {
      // when submission is a new submission
      submissionDetail.setVersionNumber(1);

      when(responsibleTeamService.getResponsibleTeamUsers(regulatorManagedInformation.getResponsibleTeamType()))
          .thenReturn(ENERGY_PORTAL_USERS);

      var confirmationNotifyTemplate = NotifyTemplate.SUBMISSION_CONFIRMATION_NOTIFICATION;
      when(emailService.getTemplate(confirmationNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(confirmationNotifyTemplate)));

      mockGenericResourceInformation();
      mockGetDisplayableLicences();

      submissionEmailService.sendSubmissionConfirmationEmails(submissionDetail);

      var firstRecipient = ENERGY_PORTAL_USERS.get(0);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(firstRecipient),
              eq(submissionDetail)
          );
      var firstEmail = mergedTemplateCaptor.getValue();
      assertThat(firstEmail.getTemplate().notifyTemplateId())
          .isEqualTo(confirmationNotifyTemplate.getTemplateId());
      assertThat(firstEmail.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, "AL6, AL9"),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(HAS_LICENCES_KEY, true),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(firstRecipient.forename(), firstRecipient.surname()))
          );

      var secondRecipient = ENERGY_PORTAL_USERS.get(1);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(secondRecipient),
              eq(submissionDetail)
          );
      var secondEmail = mergedTemplateCaptor.getValue();
      assertThat(secondEmail.getTemplate().notifyTemplateId())
          .isEqualTo(confirmationNotifyTemplate.getTemplateId());
      assertThat(secondEmail.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, "AL6, AL9"),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(HAS_LICENCES_KEY, true),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(secondRecipient.forename(), secondRecipient.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }

    @Test
    void whenNoLicences() {
      when(responsibleTeamService.getResponsibleTeamUsers(regulatorManagedInformation.getResponsibleTeamType()))
          .thenReturn(List.of(ENERGY_PORTAL_USERS.get(0)));

      var confirmationNotifyTemplate = NotifyTemplate.SUBMISSION_CONFIRMATION_NOTIFICATION;
      when(emailService.getTemplate(confirmationNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(confirmationNotifyTemplate)));

      mockGenericResourceInformation();
      mockGetDisplayableLicences_noLicences();

      submissionEmailService.sendSubmissionConfirmationEmails(submissionDetail);

      var recipient = ENERGY_PORTAL_USERS.get(0);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(recipient),
              eq(submissionDetail)
          );

      var email = mergedTemplateCaptor.getValue();
      assertThat(email.getTemplate().notifyTemplateId())
          .isEqualTo(confirmationNotifyTemplate.getTemplateId());
      assertThat(email.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(HAS_LICENCES_KEY, false),
              Tuple.tuple(LICENCES_KEY, ""),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(recipient.forename(), recipient.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }
  }

  @Nested
  class SendSubmissionApprovalEmail {

    @Test
    void whenNoUserFound_noEmailSent() {
      mockSubmissionUser_doesNotExist();

      submissionEmailService.sendSubmissionApprovalEmail(submissionDetail);

      verify(emailService, never())
          .sendEmail(any(), any(), any());
    }

    @Test
    void sendsEmail_hasLicences() {
      mockSubmissionUser();

      var approvalNotifyTemplate = NotifyTemplate.SUBMISSION_APPROVAL_NOTIFICATION;
      when(emailService.getTemplate(approvalNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(approvalNotifyTemplate)));

      mockGenericResourceInformation();
      mockGetDisplayableLicences();

      submissionEmailService.sendSubmissionApprovalEmail(submissionDetail);

      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(ENERGY_PORTAL_USER),
              eq(submissionDetail)
          );

      var email = mergedTemplateCaptor.getValue();
      assertThat(email.getTemplate().notifyTemplateId())
          .isEqualTo(approvalNotifyTemplate.getTemplateId());
      assertThat(email.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, "AL6, AL9"),
              Tuple.tuple(HAS_LICENCES_KEY, true),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(SYSTEM_OF_RECORD_KEY, RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(ENERGY_PORTAL_USER.forename(), ENERGY_PORTAL_USER.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }

    @Test
    void sendsEmail_noLicences() {
      mockSubmissionUser();

      var approvalNotifyTemplate = NotifyTemplate.SUBMISSION_APPROVAL_NOTIFICATION;
      when(emailService.getTemplate(approvalNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(approvalNotifyTemplate)));

      mockGenericResourceInformation();
      mockGetDisplayableLicences_noLicences();

      submissionEmailService.sendSubmissionApprovalEmail(submissionDetail);

      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(ENERGY_PORTAL_USER),
              eq(submissionDetail)
          );

      var email = mergedTemplateCaptor.getValue();
      assertThat(email.getTemplate().notifyTemplateId())
          .isEqualTo(approvalNotifyTemplate.getTemplateId());
      assertThat(email.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, ""),
              Tuple.tuple(HAS_LICENCES_KEY, false),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(SYSTEM_OF_RECORD_KEY, RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(ENERGY_PORTAL_USER.forename(), ENERGY_PORTAL_USER.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }
  }

  @Nested
  class SendSubmissionWithdrawalEmail {

    @Test
    void whenNoUserFound_noEmailSent() {
      mockSubmissionUser_doesNotExist();

      submissionEmailService.sendSubmissionWithdrawalEmail(submissionDetail);

      verify(emailService, never())
          .sendEmail(any(), any(), any());
    }

    @Test
    void sendsEmail_hasLicences() {
      mockSubmissionUser();

      var withdrawNotifyTemplate = NotifyTemplate.SUBMISSION_WITHDRAW_CONFIRMATION;
      when(emailService.getTemplate(withdrawNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(withdrawNotifyTemplate)));

      mockGenericResourceInformation();
      mockGetDisplayableLicences();
      mockCustomerMnemonic();

      submissionEmailService.sendSubmissionWithdrawalEmail(submissionDetail);

      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(ENERGY_PORTAL_USER),
              eq(submissionDetail)
          );

      var email = mergedTemplateCaptor.getValue();
      assertThat(email.getTemplate().notifyTemplateId())
          .isEqualTo(withdrawNotifyTemplate.getTemplateId());
      assertThat(email.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, "AL6, AL9"),
              Tuple.tuple(HAS_LICENCES_KEY, true),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(REGULATOR_ORGANISATION_NAME_KEY, CUSTOMER_MNEMONIC),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(ENERGY_PORTAL_USER.forename(), ENERGY_PORTAL_USER.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }

    @Test
    void sendsEmail_noLicences() {
      mockSubmissionUser();

      var withdrawNotifyTemplate = NotifyTemplate.SUBMISSION_WITHDRAW_CONFIRMATION;
      when(emailService.getTemplate(withdrawNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(withdrawNotifyTemplate)));

      mockGenericResourceInformation();
      mockGetDisplayableLicences_noLicences();
      mockCustomerMnemonic();

      submissionEmailService.sendSubmissionWithdrawalEmail(submissionDetail);

      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(ENERGY_PORTAL_USER),
              eq(submissionDetail)
          );

      var email = mergedTemplateCaptor.getValue();
      assertThat(email.getTemplate().notifyTemplateId())
          .isEqualTo(withdrawNotifyTemplate.getTemplateId());
      assertThat(email.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, ""),
              Tuple.tuple(HAS_LICENCES_KEY, false),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(REGULATOR_ORGANISATION_NAME_KEY, CUSTOMER_MNEMONIC),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(ENERGY_PORTAL_USER.forename(), ENERGY_PORTAL_USER.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }
  }

  @Nested
  class SendSubmissionSentForUpdateEmails {

    @ParameterizedTest
    @EnumSource(value = SubmissionType.class, names = {"CREATE_DISCOVERY", "CREATE_PROSPECT"})
    void whenInitialSubmission_andNoOrganisationUnitId_thenNoEmailsSent(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.empty());

      submissionEmailService.sendReturnedForUpdateEmails(submissionDetail);

      verify(emailService, never())
          .sendEmail(any(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_DISCOVERY", "CREATE_PROSPECT"},
        mode = EnumSource.Mode.EXCLUDE
    )
    void whenLatestApprovedSubmission_andNoOrganisationUnitId_thenNoEmailsSent(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);
      var latestApprovedSubmissionDetail = new SubmissionDetail();
      when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
          .thenReturn(Optional.of(latestApprovedSubmissionDetail));
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestApprovedSubmissionDetail))
          .thenReturn(Optional.empty());

      submissionEmailService.sendReturnedForUpdateEmails(submissionDetail);

      verify(emailService, never())
          .sendEmail(any(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_DISCOVERY", "CREATE_PROSPECT"},
        mode = EnumSource.Mode.EXCLUDE
    )
    void whenLatestApprovedSubmission_noLatestApprovedSubmissionDetail_thenNoEmailsSent(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);
      when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
          .thenReturn(Optional.empty());

      submissionEmailService.sendReturnedForUpdateEmails(submissionDetail);

      verify(emailService, never())
          .sendEmail(any(), any(), any());
    }

    @Test
    void whenNoOrganisationGroupIdsForOrganisationUnit_thenNoEmailsSent() {
      submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
      var resourceDetail = new ResourceDetail();
      resourceDetail.setOrganisationId(1);
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(resourceDetail));
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(1))
          .thenReturn(List.of());

      submissionEmailService.sendReturnedForUpdateEmails(submissionDetail);

      verify(emailService, never())
          .sendEmail(any(), any(), any());
    }

    @Test
    void whenEmailsSent_withLicences() {
      mockSubmitterRecipients();
      mockGenericResourceInformation();
      mockGetDisplayableLicences();
      mockCustomerMnemonic();

      var returnedForUpdateNotifyTemplate = NotifyTemplate.SUBMISSION_RETURNED_FOR_UPDATE;
      when(emailService.getTemplate(returnedForUpdateNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(returnedForUpdateNotifyTemplate)));

      submissionEmailService.sendReturnedForUpdateEmails(submissionDetail);

      var firstRecipient = ENERGY_PORTAL_USERS.get(0);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(firstRecipient),
              eq(submissionDetail)
          );
      var firstEmail = mergedTemplateCaptor.getValue();
      assertThat(firstEmail.getTemplate().notifyTemplateId())
          .isEqualTo(returnedForUpdateNotifyTemplate.getTemplateId());
      assertThat(firstEmail.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, "AL6, AL9"),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(HAS_LICENCES_KEY, true),
              Tuple.tuple(REGULATOR_ORGANISATION_NAME_KEY, CUSTOMER_MNEMONIC),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(firstRecipient.forename(), firstRecipient.surname()))
          );

      var secondRecipient = ENERGY_PORTAL_USERS.get(1);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(secondRecipient),
              eq(submissionDetail)
          );
      var secondEmail = mergedTemplateCaptor.getValue();
      assertThat(secondEmail.getTemplate().notifyTemplateId())
          .isEqualTo(returnedForUpdateNotifyTemplate.getTemplateId());
      assertThat(secondEmail.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, "AL6, AL9"),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(HAS_LICENCES_KEY, true),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(REGULATOR_ORGANISATION_NAME_KEY, CUSTOMER_MNEMONIC),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(secondRecipient.forename(), secondRecipient.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }

    @Test
    void whenEmailsSent_noLicences() {
      mockSubmitterRecipients();
      mockGenericResourceInformation();
      mockGetDisplayableLicences_noLicences();
      mockCustomerMnemonic();

      var returnedForUpdateNotifyTemplate = NotifyTemplate.SUBMISSION_RETURNED_FOR_UPDATE;
      when(emailService.getTemplate(returnedForUpdateNotifyTemplate))
          .thenReturn(MergedTemplate.builder(getTemplate(returnedForUpdateNotifyTemplate)));

      submissionEmailService.sendReturnedForUpdateEmails(submissionDetail);

      var firstRecipient = ENERGY_PORTAL_USERS.get(0);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(firstRecipient),
              eq(submissionDetail)
          );
      var firstEmail = mergedTemplateCaptor.getValue();
      assertThat(firstEmail.getTemplate().notifyTemplateId())
          .isEqualTo(returnedForUpdateNotifyTemplate.getTemplateId());
      assertThat(firstEmail.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, ""),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(HAS_LICENCES_KEY, false),
              Tuple.tuple(REGULATOR_ORGANISATION_NAME_KEY, CUSTOMER_MNEMONIC),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(firstRecipient.forename(), firstRecipient.surname()))
          );

      var secondRecipient = ENERGY_PORTAL_USERS.get(1);
      verify(emailService, times(1))
          .sendEmail(
              mergedTemplateCaptor.capture(),
              eq(secondRecipient),
              eq(submissionDetail)
          );
      var secondEmail = mergedTemplateCaptor.getValue();
      assertThat(secondEmail.getTemplate().notifyTemplateId())
          .isEqualTo(returnedForUpdateNotifyTemplate.getTemplateId());
      assertThat(secondEmail.getMailMergeFields())
          .extracting(MailMergeField::name, MailMergeField::value)
          .containsExactlyInAnyOrder(
              Tuple.tuple(RESOURCE_NAME_QUAD_BLOCK_KEY, RESOURCE_NAME),
              Tuple.tuple(LICENCES_KEY, ""),
              Tuple.tuple(SUBMISSION_TYPE_WITH_ACRONYM_KEY, SUBMISSION_TYPE_WITH_ACRONYM),
              Tuple.tuple(HAS_LICENCES_KEY, false),
              Tuple.tuple(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail)),
              Tuple.tuple(REGULATOR_ORGANISATION_NAME_KEY, CUSTOMER_MNEMONIC),
              Tuple.tuple(RECIPIENT_KEY, "%s %s".formatted(secondRecipient.forename(), secondRecipient.surname()))
          );

      verifyNoMoreInteractions(emailService);
    }

    private void mockSubmitterRecipients() {
      submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);

      var resourceDetail = new ResourceDetail();
      resourceDetail.setOrganisationId(1);
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(resourceDetail));

      var organisationGroupIds = List.of(1, 2);
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(1))
          .thenReturn(organisationGroupIds);
      when(industryTeamService.getWuaIdsOfUsersWithRoleForOrganisationGroupIds(
              organisationGroupIds,
              Set.of(Role.INDUSTRY_SUBMIT)
          )).thenReturn(ENERGY_PORTAL_USERS);
    }
  }

  private void mockCustomerMnemonic() {
    when(brandingConfigurationProperties.getCustomerConfigurationProperties())
        .thenReturn(new CustomerConfigurationProperties("", CUSTOMER_MNEMONIC));
  }

  private void mockSubmissionUser() {
    var submittedByWuaId = 1;
    submissionDetail.setSubmittedByWuaId(submittedByWuaId);
    when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf("1")))
        .thenReturn(Optional.of(ENERGY_PORTAL_USER));
  }

  private void mockSubmissionUser_doesNotExist() {
    var submittedByWuaId = 1;
    submissionDetail.setSubmittedByWuaId(submittedByWuaId);
    when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf("1")))
        .thenReturn(Optional.empty());
  }

  private void mockGenericResourceInformation() {
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
        submissionDetail))
        .thenReturn(RESOURCE_NAME);
    when(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail))
        .thenReturn(SUBMISSION_TYPE_WITH_ACRONYM);
  }

  private void mockGetDisplayableLicences() {
    var generalDetail = new GeneralDetail();
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var licence1 = new Licence();
    licence1.setLicenceReference(LICENCE_REF_1);
    var licence2 = new Licence();
    licence2.setLicenceReference(LICENCE_REF_2);

    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
        .thenReturn(List.of(licence1, licence2));
  }

  private void mockGetDisplayableLicences_noLicences() {
    var generalDetail = new GeneralDetail();
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
        .thenReturn(List.of());
  }

  private Template getTemplate(NotifyTemplate notifyTemplate) {
    return new Template(
        notifyTemplate.getTemplateId(),
        TemplateType.EMAIL,
        Set.of(),
        Template.VerificationStatus.CONFIRMED_NOTIFY_TEMPLATE
    );
  }
}