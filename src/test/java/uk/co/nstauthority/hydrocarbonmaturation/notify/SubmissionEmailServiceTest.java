package uk.co.nstauthority.hydrocarbonmaturation.notify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionEmailServiceTest {

  private static final String CONTEXT_PATH = "/remi";
  private static final String SERVICE_URL = "http://localhost" + CONTEXT_PATH;
  private static final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils
      .createNewProspectSubmissionDetail();
  private static final String SERVICE_NAME = "ReMi";
  private static final String REGULATOR_ORGANISATION_NAME = "NSTA";
  private static final Integer SUBMITTER_WUA_ID = 1;

  static MockHttpServletRequest request;

  @Mock
  private NotifyEmailService notifyEmailService;

  @Mock
  private EnergyPortalUserService energyPortalUserService;

  @Mock
  private SubmissionResourceService submissionResourceService;

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private LicencePersistenceService licencePersistenceService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private SubmissionEmailService submissionEmailService;

  @Captor
  ArgumentCaptor<EmailProperties> argumentCaptor;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    request.setContextPath(CONTEXT_PATH);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    SUBMISSION_DETAIL.setSubmittedByWuaId(SUBMITTER_WUA_ID);
    SUBMISSION_DETAIL.setVersionNumber(1);
  }

  @Nested
  class WhenSendingApprovalEmail {

    @Test
    void sendRemiApprovalEmail_whenValidRecipient_assertEmailProperties() {
      var recipient = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(SUBMITTER_WUA_ID)
          .withEmailAddress("expected@email.co.uk")
          .withForename("Bob")
          .withSurname("The builder")
          .build();
      var expectedName = "resource name (25/5)";
      var expectedLicencesString = "A2, A6:";
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf(SUBMITTER_WUA_ID.toString())))
          .thenReturn(Optional.of(recipient));
      when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
          .thenReturn(expectedName);
      var generalDetail = mock(GeneralDetail.class);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(generalDetail));

      var licence = new Licence();
      licence.setLicenceReference("A2");
      var licence2 = new Licence();
      licence2.setLicenceReference("A6");
      when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
          .thenReturn(List.of(licence, licence2));

      var expectedRemiSystemOfRecordUrl = SERVICE_URL +
          ReverseRouter.route(on(ViewResourceRecordController.class)
              .showResourceRecord(SUBMISSION_DETAIL.getSubmission().getResource().getId()));

      submissionEmailService.sendSubmissionApprovalEmail(SUBMISSION_DETAIL);

      verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient.emailAddress()));

    assertThat(argumentCaptor.getValue())
        .extracting(
            EmailProperties::getTemplate,
            EmailProperties::getEmailPersonalisations)
        .containsExactly(
            NotifyTemplate.SUBMISSION_APPROVAL_NOTIFICATION,
            Map.of(
                "RESOURCE_NAME_WITH_QUAD_BLOCK", expectedName,
                "LICENCES", expectedLicencesString,
                "SUBMISSION_TYPE_CAPITALISED", SUBMISSION_DETAIL.getSubmissionType().getTypeName(),
                "SUBMISSION_TYPE", SUBMISSION_DETAIL.getSubmissionType().getTypeName().toLowerCase(),
                "SYSTEM_OF_RECORD_RESOURCE_URL", expectedRemiSystemOfRecordUrl,
                "RECIPIENT_NAME", "Bob The builder",
                "SERVICE_NAME_MNEMONIC", SERVICE_NAME,
                "TEST_EMAIL", "no"
            )
        );
  }

  @Test
  void sendRemiApprovalEmail_whenEmptyLicences_assertEmailProperties() {
    var recipient = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(SUBMITTER_WUA_ID)
        .withEmailAddress("expected@email.co.uk")
        .withForename("Jonathan")
        .withSurname("Joestar")
        .build();
    var expectedName = "other resource name (22/5)";
    var expectedLicencesString = "";
    SUBMISSION_DETAIL.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    var resourceWithRandomUUID = new Resource();
    resourceWithRandomUUID.setId(UUID.randomUUID());
    SUBMISSION_DETAIL.getSubmission().setResource(resourceWithRandomUUID);
    when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
        .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
    when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf(SUBMITTER_WUA_ID.toString())))
        .thenReturn(Optional.of(recipient));
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
        .thenReturn(expectedName);
    var generalDetail = mock(GeneralDetail.class);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
        .thenReturn(Optional.of(generalDetail));
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
        .thenReturn(List.of());
    var expectedRemiSystemOfRecordUrl = SERVICE_URL +
        ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(resourceWithRandomUUID.getId()));

    submissionEmailService.sendSubmissionApprovalEmail(SUBMISSION_DETAIL);

    verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient.emailAddress()));

    assertThat(argumentCaptor.getValue())
        .extracting(
            EmailProperties::getTemplate,
            EmailProperties::getEmailPersonalisations)
        .containsExactly(
            NotifyTemplate.SUBMISSION_APPROVAL_NOTIFICATION,
            Map.of(
                "RESOURCE_NAME_WITH_QUAD_BLOCK", expectedName,
                "LICENCES", expectedLicencesString,
                "SUBMISSION_TYPE_CAPITALISED", SUBMISSION_DETAIL.getSubmissionType().getTypeName(),
                "SUBMISSION_TYPE", SUBMISSION_DETAIL.getSubmissionType().getTypeName().toLowerCase(),
                "SYSTEM_OF_RECORD_RESOURCE_URL", expectedRemiSystemOfRecordUrl,
                "RECIPIENT_NAME", "Jonathan Joestar",
                "SERVICE_NAME_MNEMONIC", SERVICE_NAME,
                "TEST_EMAIL", "no"
            )
        );
  }

    @Test
    void sendRemiApprovalEmail_whenInvalidRecipient_assertDoesNotEmail() {
      var invalidWuaId = "22";
      SUBMISSION_DETAIL.setSubmittedByWuaId(Integer.valueOf(invalidWuaId));
      when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf(invalidWuaId)))
          .thenReturn(Optional.empty());

      submissionEmailService.sendSubmissionApprovalEmail(SUBMISSION_DETAIL);

      verifyNoInteractions(notifyEmailService);
    }
  }

  @Nested
  class WhenSendingSubmittedEmail {

    private static final Integer EXPLORATION_USER_WUA_ID_1 = 22;
    private static final Integer EXPLORATION_USER_WUA_ID_2 = 15;
    private static final Integer EXPLORATION_USER_WUA_ID_3 = 222;

    @Test
    void sendSubmittedSubmissionEmails_whenExplorationTeam_verifySentEmails() {
      var recipient1 = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(EXPLORATION_USER_WUA_ID_1)
          .withEmailAddress("expected@email.co.uk")
          .build();
      var recipient2 = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(EXPLORATION_USER_WUA_ID_2)
          .withEmailAddress("expected2@email.co.uk")
          .build();
      var recipient3 = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(EXPLORATION_USER_WUA_ID_3)
          .withEmailAddress("expected3@email.co.uk")
          .build();

      var expectedName = "resource name (25/5)";

      when(regulatorTeamService.getExplorationTeamEnergyPortalUsers())
          .thenReturn(List.of(recipient1, recipient2, recipient3));
      when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
          .thenReturn(expectedName);
      var generalDetail = mock(GeneralDetail.class);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(generalDetail));
      var responsibleTeam = new RegulatorManagedInformation();
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          SUBMISSION_DETAIL.getSubmission().getResource()))
          .thenReturn(responsibleTeam);
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      submissionEmailService.sendSubmittedSubmissionEmails(SUBMISSION_DETAIL);

      verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient1.emailAddress()));
      verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient2.emailAddress()));
      verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient3.emailAddress()));
      verifyNoMoreInteractions(notifyEmailService);
    }

    @Test
    void sendSubmittedSubmissionEmails_whenExplorationTeam_assertEmailPersonalisation() {
      var recipient1 = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(EXPLORATION_USER_WUA_ID_1)
          .withEmailAddress("expected@email.co.uk")
          .withForename("Bob")
          .withSurname("Johnson")
          .build();

      var expectedName = "resource name (25/5)";
      var expectedLicencesString = "A2, A6:";
      var expectedLicencesStringWithoutColon = "A2, A6";

      when(regulatorTeamService.getExplorationTeamEnergyPortalUsers())
          .thenReturn(List.of(recipient1));
      when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
          .thenReturn(expectedName);
      var generalDetail = mock(GeneralDetail.class);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(generalDetail));
      var responsibleTeam = new RegulatorManagedInformation();
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          SUBMISSION_DETAIL.getSubmission().getResource()))
          .thenReturn(responsibleTeam);
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      var licence = new Licence();
      licence.setLicenceReference("A2");
      var licence2 = new Licence();
      licence2.setLicenceReference("A6");
      when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
          .thenReturn(List.of(licence, licence2));
      var expectedReviewSubmissionUrl = SERVICE_URL +
          ReverseRouter.route(on(ReviewSubmissionController.class)
              .showReviewPage(SUBMISSION_DETAIL.getSubmission().getResource().getId()));
      submissionEmailService.sendSubmittedSubmissionEmails(SUBMISSION_DETAIL);

      verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient1.emailAddress()));
      verifyNoMoreInteractions(notifyEmailService);

      assertThat(argumentCaptor.getValue())
          .extracting(
              EmailProperties::getTemplate,
              EmailProperties::getEmailPersonalisations)
          .containsExactly(
              NotifyTemplate.SUBMISSION_CONFIRMATION_NOTIFICATION,
              Map.of(
                  "RESOURCE_NAME_WITH_QUAD_BLOCK", expectedName,
                  "LICENCES", expectedLicencesString,
                  "SUBMISSION_TYPE", SUBMISSION_DETAIL.getSubmissionType().getTypeName().toLowerCase(),
                  "SUBMISSION_TYPE_CAPITALISED", SUBMISSION_DETAIL.getSubmissionType().getTypeName(),
                  "REVIEW_PAGE_URL", expectedReviewSubmissionUrl,
                  "LICENCES_WITHOUT_LEADING_COLON", expectedLicencesStringWithoutColon,
                  "RECIPIENT_NAME", "Bob Johnson",
                  "SERVICE_NAME_MNEMONIC", SERVICE_NAME,
                  "TEST_EMAIL", "no")
          );
    }

    @Test
    void sendSubmittedUpdateSubmissionEmails_whenExplorationTeam_assertEmailPersonalisation() {
      SUBMISSION_DETAIL.setVersionNumber(2);

      var recipient1 = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(EXPLORATION_USER_WUA_ID_1)
          .withEmailAddress("expected@email.co.uk")
          .withForename("Bob")
          .withSurname("Johnson")
          .build();

      var expectedName = "resource name (25/5)";
      var expectedLicencesString = "A2:";
      var expectedLicencesStringWithoutColon = "A2";

      when(regulatorTeamService.getExplorationTeamEnergyPortalUsers())
          .thenReturn(List.of(recipient1));
      when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
          .thenReturn(expectedName);
      var generalDetail = mock(GeneralDetail.class);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(generalDetail));
      var responsibleTeam = new RegulatorManagedInformation();
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          SUBMISSION_DETAIL.getSubmission().getResource()))
          .thenReturn(responsibleTeam);
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      var licence = new Licence();
      licence.setLicenceReference("A2");
      when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
          .thenReturn(List.of(licence));
      var expectedReviewSubmissionUrl = SERVICE_URL +
          ReverseRouter.route(on(ReviewSubmissionController.class)
              .showReviewPage(SUBMISSION_DETAIL.getSubmission().getResource().getId()));

      submissionEmailService.sendSubmittedSubmissionEmails(SUBMISSION_DETAIL);

      verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient1.emailAddress()));
      verifyNoMoreInteractions(notifyEmailService);

      assertThat(argumentCaptor.getValue())
          .extracting(
              EmailProperties::getTemplate,
              EmailProperties::getEmailPersonalisations)
          .containsExactly(
              NotifyTemplate.SUBMISSION_UPDATE_SUBMITTED_CONFIRMATION,
              Map.of(
                  "RESOURCE_NAME_WITH_QUAD_BLOCK", expectedName,
                  "LICENCES", expectedLicencesString,
                  "SUBMISSION_TYPE", SUBMISSION_DETAIL.getSubmissionType().getTypeName().toLowerCase(),
                  "SUBMISSION_TYPE_CAPITALISED", SUBMISSION_DETAIL.getSubmissionType().getTypeName(),
                  "REVIEW_PAGE_URL", expectedReviewSubmissionUrl,
                  "LICENCES_WITHOUT_LEADING_COLON", expectedLicencesStringWithoutColon,
                  "RECIPIENT_NAME", "Bob Johnson",
                  "SERVICE_NAME_MNEMONIC", SERVICE_NAME,
                  "TEST_EMAIL", "no")
          );
    }

    @ParameterizedTest
    @EnumSource(value = ResponsibleTeamType.class, names = {"EXPLORATION"}, mode = EnumSource.Mode.EXCLUDE)
    void sendSubmittedSubmissionEmails_whenNotExploration_verifyNoSentEmails(ResponsibleTeamType responsibleTeamType) {
      var regulatorManagedInformation = new RegulatorManagedInformation();
      regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);

      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          SUBMISSION_DETAIL.getSubmission().getResource()))
          .thenReturn(regulatorManagedInformation);

      submissionEmailService.sendSubmittedSubmissionEmails(SUBMISSION_DETAIL);

      verifyNoInteractions(notifyEmailService);
    }

    @Test
    void sendSubmittedSubmissionEmails_whenNoExplorationTeamMembers_verifyNoSentEmails() {
      var regulatorManagedInformation = new RegulatorManagedInformation();
      regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);

      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          SUBMISSION_DETAIL.getSubmission().getResource()))
          .thenReturn(regulatorManagedInformation);
      when(regulatorTeamService.getExplorationTeamEnergyPortalUsers())
          .thenReturn(List.of());

      submissionEmailService.sendSubmittedSubmissionEmails(SUBMISSION_DETAIL);

      verifyNoInteractions(notifyEmailService);
    }
  }

  @Nested
  class WhenSendingWithdrawalEmail {

    @Test
    void sendSubmissionWithdrawalEmail_whenValidRecipient_assertEmailProperties() {
      var recipient = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(SUBMITTER_WUA_ID)
          .withEmailAddress("expected@email.co.uk")
          .withForename("Bob")
          .withSurname("The builder")
          .build();
      var expectedName = "resource name (25/5)";
      var expectedLicencesString = "A2, A6:";

      when(serviceBrandingConfigurationProperties.getCustomerConfigurationProperties())
          .thenReturn(new CustomerConfigurationProperties("", REGULATOR_ORGANISATION_NAME));
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf(SUBMITTER_WUA_ID.toString())))
          .thenReturn(Optional.of(recipient));
      when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
          .thenReturn(expectedName);
      var generalDetail = mock(GeneralDetail.class);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(generalDetail));
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      var licence = new Licence();
      licence.setLicenceReference("A2");
      var licence2 = new Licence();
      licence2.setLicenceReference("A6");
      when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
          .thenReturn(List.of(licence, licence2));

      submissionEmailService.sendSubmissionWithdrawalEmail(SUBMISSION_DETAIL);

      verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient.emailAddress()));

      assertThat(argumentCaptor.getValue())
          .extracting(
              EmailProperties::getTemplate,
              EmailProperties::getEmailPersonalisations)
          .containsExactly(
              NotifyTemplate.SUBMISSION_WITHDRAW_CONFIRMATION,
              Map.of(
                  "RESOURCE_NAME_WITH_QUAD_BLOCK", expectedName,
                  "LICENCES", expectedLicencesString,
                  "SUBMISSION_TYPE_CAPITALISED", SUBMISSION_DETAIL.getSubmissionType().getTypeName(),
                  "SUBMISSION_TYPE", SUBMISSION_DETAIL.getSubmissionType().getTypeName().toLowerCase(),
                  "RECIPIENT_NAME", "Bob The builder",
                  "SERVICE_NAME_MNEMONIC", SERVICE_NAME,
                  "REGULATOR_ORGANISATION_NAME", REGULATOR_ORGANISATION_NAME,
                  "TEST_EMAIL", "no"
              )
          );
    }

    @Test
    void sendSubmissionWithdrawEmail_whenEmptyLicences_assertEmailProperties() {
      var recipient = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(SUBMITTER_WUA_ID)
          .withEmailAddress("expected@email.co.uk")
          .withForename("Jonathan")
          .withSurname("Joestar")
          .build();
      var expectedName = "other resource name (22/5)";
      var expectedLicencesString = "";
      SUBMISSION_DETAIL.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
      var resourceWithRandomUUID = new Resource();
      resourceWithRandomUUID.setId(UUID.randomUUID());
      SUBMISSION_DETAIL.getSubmission().setResource(resourceWithRandomUUID);

      when(serviceBrandingConfigurationProperties.getCustomerConfigurationProperties())
          .thenReturn(new CustomerConfigurationProperties("", REGULATOR_ORGANISATION_NAME));
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf(SUBMITTER_WUA_ID.toString())))
          .thenReturn(Optional.of(recipient));
      when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
          .thenReturn(expectedName);
      var generalDetail = mock(GeneralDetail.class);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(generalDetail));
      when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
          .thenReturn(List.of());

      submissionEmailService.sendSubmissionWithdrawalEmail(SUBMISSION_DETAIL);

      verify(notifyEmailService).sendEmail(argumentCaptor.capture(), eq(recipient.emailAddress()));

      assertThat(argumentCaptor.getValue())
          .extracting(
              EmailProperties::getTemplate,
              EmailProperties::getEmailPersonalisations)
          .containsExactly(
              NotifyTemplate.SUBMISSION_WITHDRAW_CONFIRMATION,
              Map.of(
                  "RESOURCE_NAME_WITH_QUAD_BLOCK", expectedName,
                  "LICENCES", expectedLicencesString,
                  "SUBMISSION_TYPE_CAPITALISED", SUBMISSION_DETAIL.getSubmissionType().getTypeName(),
                  "SUBMISSION_TYPE", SUBMISSION_DETAIL.getSubmissionType().getTypeName().toLowerCase(),
                  "RECIPIENT_NAME", "Jonathan Joestar",
                  "SERVICE_NAME_MNEMONIC", SERVICE_NAME,
                  "REGULATOR_ORGANISATION_NAME", REGULATOR_ORGANISATION_NAME,
                  "TEST_EMAIL", "no"
              )
          );
    }

    @Test
    void sendSubmissionWithdrawEmail_whenInvalidRecipient_assertDoesNotEmail() {
      var invalidWuaId = "22";
      SUBMISSION_DETAIL.setSubmittedByWuaId(Integer.valueOf(invalidWuaId));
      when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf(invalidWuaId)))
          .thenReturn(Optional.empty());

      submissionEmailService.sendSubmissionWithdrawalEmail(
          SUBMISSION_DETAIL);

      verifyNoInteractions(notifyEmailService);
    }
  }

  @Nested
  class WhenSendingReturnedForUpdateEmail {

    private static final Integer ORGANISATION_ID = 1;
    private static final Integer ORGANISATION_GROUP_ID = 2;
    private static final Long SUBMITTER_WUA_ID_1 = 11L;
    private static final Long SUBMITTER_WUA_ID_2 = 22L;
    private static final String SUBMITTER_FORENAME_1 = "Bob";
    private static final String SUBMITTER_SURNAME_1 = "Bobsurname";
    private static final String SUBMITTER_EMAIL_1 = "bob@gmail.com";
    private static final String SUBMITTER_EMAIL_2 = "joe@gmail.com";

    private ResourceDetail resourceDetail;

    @BeforeEach
    void setUp() {
      resourceDetail = new ResourceDetail();
      resourceDetail.setOrganisationId(ORGANISATION_ID);
      SUBMISSION_DETAIL.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    }

    @Test
    void sendReturnedForUpdateEmail_whenSubmitterPresent_whenNotInitialSubmission_assertSentEmailPersonalisations() {
      var latestApprovedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      when(submissionDetailService.getLatestApprovedSubmissionForSubmission(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(latestApprovedSubmissionDetail));
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestApprovedSubmissionDetail))
          .thenReturn(Optional.of(resourceDetail));
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_ID))
          .thenReturn(List.of(ORGANISATION_GROUP_ID));
      when(industryTeamService.getWuaIdsOfUsersWithRoleForOrganisationGroupIds(List.of(ORGANISATION_GROUP_ID), Set.of(
          IndustryTeamRole.SUBMITTER)))
          .thenReturn(List.of(SUBMITTER_WUA_ID_1));
      var submitterPortalUser1 = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(SUBMITTER_WUA_ID).withEmailAddress("")
          .withForename(SUBMITTER_FORENAME_1)
          .withSurname(SUBMITTER_SURNAME_1)
          .withEmailAddress(SUBMITTER_EMAIL_1)
          .build();

      when(energyPortalUserService.findByWuaIds(List.of(
          WebUserAccountId.valueOf(SUBMITTER_WUA_ID_1.toString()))))
          .thenReturn(List.of(submitterPortalUser1));

      var expectedName = "other resource name (22/5)";
      var expectedLicencesString = "";
      SUBMISSION_DETAIL.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
      var resourceWithRandomUUID = new Resource();
      resourceWithRandomUUID.setId(UUID.randomUUID());
      SUBMISSION_DETAIL.getSubmission().setResource(resourceWithRandomUUID);
      var expectedReviewSubmissionUrl = SERVICE_URL +
          ReverseRouter.route(on(ReviewSubmissionController.class)
              .showReviewPage(SUBMISSION_DETAIL.getSubmission().getResource().getId()));
      when(serviceBrandingConfigurationProperties.getCustomerConfigurationProperties())
          .thenReturn(new CustomerConfigurationProperties("", REGULATOR_ORGANISATION_NAME));
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
          .thenReturn(expectedName);
      var generalDetail = mock(GeneralDetail.class);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(generalDetail));
      when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
          .thenReturn(List.of());

      submissionEmailService.sendReturnedForUpdateEmails(
          SUBMISSION_DETAIL);

      verify(notifyEmailService, times(1))
          .sendEmail(argumentCaptor.capture(), eq(SUBMITTER_EMAIL_1));


      assertThat(argumentCaptor.getValue())
          .extracting(
              EmailProperties::getTemplate,
              EmailProperties::getEmailPersonalisations)
          .containsExactly(
              NotifyTemplate.SUBMISSION_RETURNED_FOR_UPDATE,
              Map.of(
                  "LICENCES", expectedLicencesString,
                  "LICENCES_WITHOUT_LEADING_COLON", expectedLicencesString,
                  "RECIPIENT_NAME", "%s %s".formatted(SUBMITTER_FORENAME_1, SUBMITTER_SURNAME_1),
                  "RESOURCE_NAME_WITH_QUAD_BLOCK", expectedName,
                  "REVIEW_PAGE_URL", expectedReviewSubmissionUrl,
                  "SUBMISSION_TYPE_CAPITALISED", SUBMISSION_DETAIL.getSubmissionType().getTypeName(),
                  "SUBMISSION_TYPE", SUBMISSION_DETAIL.getSubmissionType().getTypeName().toLowerCase(),
                  "SERVICE_NAME_MNEMONIC", SERVICE_NAME,
                  "REGULATOR_ORGANISATION_NAME", REGULATOR_ORGANISATION_NAME,
                  "TEST_EMAIL", "no"
              )
          );
    }

    @Test
    void sendReturnedForUpdateEmail_whenTwoSubmittersPresent_assertSentTwoEmails() {
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(resourceDetail));
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_ID))
          .thenReturn(List.of(ORGANISATION_GROUP_ID));
      when(industryTeamService.getWuaIdsOfUsersWithRoleForOrganisationGroupIds(List.of(ORGANISATION_GROUP_ID), Set.of(
          IndustryTeamRole.SUBMITTER)))
          .thenReturn(List.of(SUBMITTER_WUA_ID_1, SUBMITTER_WUA_ID_2));
      var submitterPortalUser1 = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(SUBMITTER_WUA_ID)
          .withEmailAddress(SUBMITTER_EMAIL_1)
          .build();
      var submitterPortalUser2 = EnergyPortalUserDtoTestUtil.Builder()
          .withWebUserAccountId(SUBMITTER_WUA_ID_2)
          .withEmailAddress(SUBMITTER_EMAIL_2)
          .build();

      when(energyPortalUserService.findByWuaIds(List.of(
          WebUserAccountId.valueOf(SUBMITTER_WUA_ID_1.toString()),
          WebUserAccountId.valueOf(SUBMITTER_WUA_ID_2.toString()))))
          .thenReturn(List.of(submitterPortalUser1, submitterPortalUser2));
      when(serviceBrandingConfigurationProperties.getCustomerConfigurationProperties())
          .thenReturn(new CustomerConfigurationProperties("", REGULATOR_ORGANISATION_NAME));
      when(serviceBrandingConfigurationProperties.getServiceConfigurationProperties())
          .thenReturn(new ServiceConfigurationProperties("", SERVICE_NAME));
      when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(SUBMISSION_DETAIL))
          .thenReturn("");
      var generalDetail = mock(GeneralDetail.class);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(generalDetail));
      when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
          .thenReturn(List.of());

      submissionEmailService.sendReturnedForUpdateEmails(
          SUBMISSION_DETAIL);

      verify(notifyEmailService, times(1))
          .sendEmail(any(), eq(SUBMITTER_EMAIL_1));
      verify(notifyEmailService, times(1))
          .sendEmail(any(), eq(SUBMITTER_EMAIL_2));
    }

    @Test
    void sendReturnedForUpdateEmail_whenNoResourceGroupId_assertDoesNotSend() {
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(resourceDetail));
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_ID))
          .thenReturn(List.of());
      submissionEmailService.sendReturnedForUpdateEmails(
          SUBMISSION_DETAIL);

      verifyNoInteractions(notifyEmailService);
    }

    @Test
    void sendReturnedForUpdateEmail_whenNoResourceDetails_assertDoesNotSend() {
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.empty());

      submissionEmailService.sendReturnedForUpdateEmails(
          SUBMISSION_DETAIL);

      verifyNoInteractions(notifyEmailService);
    }
  }
}