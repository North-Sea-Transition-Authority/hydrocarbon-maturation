package uk.co.nstauthority.hydrocarbonmaturation.notify;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicencePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ExtendWith(MockitoExtension.class)
class SubmissionEmailServiceTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();
  private static final String CONTEXT_PATH = "/remi";
  private static final String SERVICE_URL = "http://localhost" + CONTEXT_PATH;
  private static final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils
      .createNewProspectSubmissionDetail();

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
  }

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
            .showResourceRecord(SUBMISSION_DETAIL.getResourceId().getId()));

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
    SUBMISSION_DETAIL.setResourceId(resourceWithRandomUUID);

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
                "TEST_EMAIL", "no"
            )
        );
  }

  @Test
  void sendRemiApprovalEmail_whenInvalidRecipient_assertThrows() {
    var invalidWuaId = "22";
    SUBMISSION_DETAIL.setSubmittedByWuaId(Integer.valueOf(invalidWuaId));
    when(energyPortalUserService.findByWuaId(WebUserAccountId.valueOf(invalidWuaId)))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> submissionEmailService.sendSubmissionApprovalEmail(SUBMISSION_DETAIL))
        .isExactlyInstanceOf(ActionNotAllowedException.class)
        .hasMessage("Could not find a user with id %s".formatted(invalidWuaId));

    verifyNoInteractions(notifyEmailService);
  }
}