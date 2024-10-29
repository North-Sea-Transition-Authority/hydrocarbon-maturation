package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryAssociationService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSubmissionStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionApprovalServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;
  @Mock
  private FieldSubmissionStatusService fieldSubmissionStatusService;
  @Mock
  private SubmissionEmailService submissionEmailService;
  @Mock
  private SubmissionUpdateService submissionUpdateService;
  @Mock
  private ResponsibleTeamService responsibleTeamService;
  @Mock
  private ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService;
  @Mock
  private ReservoirSubmissionSectionService reservoirSubmissionSectionService;
  @Mock
  private InternalTeamService internalTeamService;
  @Mock
  private DiscoveryAssociationService discoveryAssociationService;
  @InjectMocks
  private SubmissionApprovalService submissionApprovalService;

  @Nested
  class WhenSubmittingSubmission {

    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setup() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"}
    )
    void whenFieldSubmission_thenVerifyFieldStatusUpdate(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);
      submissionDetail.setCorrection(false);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verify(fieldSubmissionStatusService, times(1)).updateFieldStatusForSubmission(submissionDetail);
      verifyNoMoreInteractions(fieldSubmissionStatusService);
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_FIELD", "CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"},
        mode = EnumSource.Mode.EXCLUDE
    )
    void whenNotFieldSubmission_thenVerifyNoFieldStatusUpdate(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verifyNoInteractions(fieldSubmissionStatusService);
    }

    @Test
    void whenVersionNumberGreaterThanOne_thenVerifyUpdateResolved() {
      submissionDetail.setVersionNumber(2);
      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verify(reservoirTotalsCleanAndCopyService).cleanAndCopyVolumetricTotals(submissionDetail);
      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail, USER);
      verify(submissionUpdateService, times(1))
          .resolveUpdateOnSubmission(submissionDetail, USER);
      verify(submissionEmailService, times(1))
          .sendSubmissionConfirmationEmails(submissionDetail);
    }

    @Test
    void whenCorrection_thenVerifyOnlyStatusUpdated() {
      submissionDetail.setCorrection(true);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verify(reservoirTotalsCleanAndCopyService).cleanAndCopyVolumetricTotals(submissionDetail);
      var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
      verify(submissionDetailPersistenceService).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());
      assertEquals(SubmissionDetailStatus.ACCEPTED, submissionDetailArgumentCaptor.getValue().getStatus());
      verifyNoInteractions(submissionUpdateService);
      verifyNoInteractions(submissionEmailService);
    }

    @Test
    void andCanNotAutoApprove_ThenVerifyCallToSubmit() {
      submissionDetail.setVersionNumber(1);
      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verify(reservoirTotalsCleanAndCopyService).cleanAndCopyVolumetricTotals(submissionDetail);
      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail, USER);
      verifyNoInteractions(submissionUpdateService);
      verify(submissionEmailService, times(1))
          .sendSubmissionConfirmationEmails(submissionDetail);
    }

    @Test
    void andCanAutoApprove_ThenVerifyCallsToSubmitAndApprove() {
      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verify(reservoirTotalsCleanAndCopyService).cleanAndCopyVolumetricTotals(submissionDetail);
      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail, USER);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail, USER);
      verifyNoInteractions(submissionEmailService);
      verifyNoInteractions(submissionUpdateService);
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"}
    )
    void andCanAutoApprove_whenFieldSubmission_verifyFieldStatusUpdate(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);

      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verify(fieldSubmissionStatusService, times(1)).updateFieldStatusForAcceptance(submissionDetail);
      verifyNoMoreInteractions(fieldSubmissionStatusService);
      verifyNoInteractions(submissionEmailService);
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_FIELD", "CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"},
        mode = EnumSource.Mode.EXCLUDE
    )
    void andCanAutoApprove_whenNotFieldSubmission_verifyNoFieldStatusUpdate(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);

      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verifyNoInteractions(fieldSubmissionStatusService);
      verifyNoInteractions(submissionEmailService);
    }

    @ParameterizedTest
    @MethodSource("allFieldSubmissionTypes")
    void whenFieldSubmission_thenVerifyCallsToCleanReservoirData(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);

      submissionApprovalService.submitSubmission(submissionDetail, USER);

      verify(reservoirTotalsCleanAndCopyService, times(1))
          .cleanAndCopyVolumetricTotals(submissionDetail);
      verify(reservoirSubmissionSectionService, times(1))
          .cleanReservoirSubmissionSections(submissionDetail);
      verifyNoMoreInteractions(reservoirTotalsCleanAndCopyService, reservoirSubmissionSectionService);
    }

    private static Stream<Arguments> allFieldSubmissionTypes() {
      return Arrays.stream(SubmissionType.values())
          .filter(SubmissionDetailUtil::isFieldSubmission)
          .filter(submissionType -> submissionType != SubmissionType.CREATE_FIELD)
          .map(Arguments::of);
    }
  }

  @Nested
  class WhenApprovingSubmission {

    private SubmissionDetail submissionDetail;


    @BeforeEach
    void setup() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    }

    @Test
    void approveSubmission_verifyMethodCalls_whenCanApprove() {
      when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
          .thenReturn(true);
      submissionApprovalService.approveSubmissionOrThrow(submissionDetail, USER);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail, USER);
      verify(submissionEmailService).sendSubmissionApprovalEmail(submissionDetail);
    }

    @Test
    void approveSubmission_verifyNoMethodCalls_whenCannotApprove() {
      assertThatThrownBy(() -> submissionApprovalService.approveSubmissionOrThrow(submissionDetail, USER))
          .isExactlyInstanceOf(ActionNotAllowedException.class)
          .hasMessage("Submission cannot be accepted by user with ID %s"
              .formatted(USER.wuaId()));
      verifyNoInteractions(submissionDetailPersistenceService);
      verifyNoInteractions(submissionEmailService);
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"}
    )
    void approveSubmission_canApproveAndFieldSubmission_thenVerifyFieldStatusUpdate(SubmissionType submissionType) {
      when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
          .thenReturn(true);
      submissionDetail.setSubmissionType(submissionType);

      submissionApprovalService.approveSubmissionOrThrow(submissionDetail, USER);

      verify(fieldSubmissionStatusService, times(1)).updateFieldStatusForAcceptance(submissionDetail);
      verifyNoMoreInteractions(fieldSubmissionStatusService);
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_FIELD", "CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"},
        mode = EnumSource.Mode.EXCLUDE
    )
    void approveSubmission_canApproveAndNotFieldSubmission_thenVerifyNoFieldStatusUpdate(SubmissionType submissionType) {
      when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
          .thenReturn(true);

      submissionDetail.setSubmissionType(submissionType);

      submissionApprovalService.approveSubmissionOrThrow(submissionDetail, USER);

      verifyNoInteractions(fieldSubmissionStatusService);
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam() {
      when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
          .thenReturn(true);

      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail, USER)).isTrue();
    }

    @Test
    void canApproveSubmission_whenRegulator_notInResponsibleTeam() {
      when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
          .thenReturn(false);

      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail, USER)).isFalse();
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam_hasUnresolvedUpdate_assertFalse() {
      when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
          .thenReturn(true);
      when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
          .thenReturn(true);
      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail, USER)).isFalse();
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam_hasNoUnresolvedUpdate_noAssociatedStatus300Field() {
      when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
          .thenReturn(true);
      when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
          .thenReturn(false);
      when(discoveryAssociationService.associatedWithStatus300PlusField(submissionDetail.getSubmission()))
          .thenReturn(false);
      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail, USER)).isTrue();
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam_hasNoUnresolvedUpdate_hasAssociatedStatus300Field_assertFalse() {
      when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
          .thenReturn(true);
      when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
          .thenReturn(false);
      when(discoveryAssociationService.associatedWithStatus300PlusField(submissionDetail.getSubmission()))
          .thenReturn(true);
      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail, USER)).isFalse();
    }
  }
}