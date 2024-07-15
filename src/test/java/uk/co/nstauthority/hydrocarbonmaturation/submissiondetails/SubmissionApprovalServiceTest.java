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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSubmissionStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.OldSubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;

@ExtendWith(MockitoExtension.class)
class SubmissionApprovalServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;
  @Mock
  private FieldSubmissionStatusService fieldSubmissionStatusService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private UserService userService;
  @Mock
  private OldSubmissionEmailService oldSubmissionEmailService;
  @Mock
  private SubmissionUpdateService submissionUpdateService;
  @Mock
  private ResponsibleTeamService responsibleTeamService;
  @Mock
  private AreaTeamService areaTeamService;
  @Mock
  private ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService;
  @Mock
  private ReservoirSubmissionSectionService reservoirSubmissionSectionService;
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

      submissionApprovalService.submitSubmission(submissionDetail);

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

      submissionApprovalService.submitSubmission(submissionDetail);

      verifyNoInteractions(fieldSubmissionStatusService);
    }

    @Test
    void whenVersionNumberGreaterThanOne_thenVerifyUpdateResolved() {
      submissionDetail.setVersionNumber(2);
      when(userService.getUser()).thenReturn(USER);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
      when(areaTeamService.isAreaUser(USER)).thenReturn(false);

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(reservoirTotalsCleanAndCopyService).cleanAndCopyVolumetricTotals(submissionDetail);
      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verify(submissionUpdateService, times(1))
          .resolveUpdateOnSubmission(submissionDetail);
      verify(oldSubmissionEmailService, times(1))
          .sendSubmittedSubmissionEmails(submissionDetail);
    }

    @Test
    void whenCorrection_thenVerifyOnlyStatusUpdated() {
      submissionDetail.setCorrection(true);

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(reservoirTotalsCleanAndCopyService).cleanAndCopyVolumetricTotals(submissionDetail);
      var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
      verify(submissionDetailPersistenceService).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());
      assertEquals(SubmissionDetailStatus.ACCEPTED, submissionDetailArgumentCaptor.getValue().getStatus());
      verifyNoInteractions(submissionUpdateService);
      verifyNoInteractions(oldSubmissionEmailService);
    }

    @Test
    void andCanNotAutoApprove_ThenVerifyCallToSubmit() {
      submissionDetail.setVersionNumber(1);
      when(userService.getUser()).thenReturn(USER);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
      when(areaTeamService.isAreaUser(USER)).thenReturn(false);

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(reservoirTotalsCleanAndCopyService).cleanAndCopyVolumetricTotals(submissionDetail);
      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verifyNoInteractions(submissionUpdateService);
      verify(oldSubmissionEmailService, times(1))
          .sendSubmittedSubmissionEmails(submissionDetail);
    }

    @ParameterizedTest
    @CsvSource(value = { "true, true",
                         "true, false",
                         "false, true"})
    void andCanAutoApprove_ThenVerifyCallsToSubmitAndApprove(boolean isRegulatorUser, boolean isAreaUser) {
      when(userService.getUser()).thenReturn(USER);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(isRegulatorUser);
      if (!isRegulatorUser) {
        when(areaTeamService.isAreaUser(USER)).thenReturn(isAreaUser);
      }

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(reservoirTotalsCleanAndCopyService).cleanAndCopyVolumetricTotals(submissionDetail);
      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
      verifyNoInteractions(oldSubmissionEmailService);
      verifyNoInteractions(submissionUpdateService);
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"}
    )
    void andCanAutoApprove_whenFieldSubmission_verifyFieldStatusUpdate(SubmissionType submissionType) {
      var isRegulatorUser = true;

      submissionDetail.setSubmissionType(submissionType);

      when(userService.getUser()).thenReturn(USER);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(isRegulatorUser);

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(fieldSubmissionStatusService, times(1)).updateFieldStatusForAcceptance(submissionDetail);
      verifyNoMoreInteractions(fieldSubmissionStatusService);
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_FIELD", "CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"},
        mode = EnumSource.Mode.EXCLUDE
    )
    void andCanAutoApprove_whenNotFieldSubmission_verifyNoFieldStatusUpdate(SubmissionType submissionType) {
      var isRegulatorUser = true;

      submissionDetail.setSubmissionType(submissionType);

      when(userService.getUser()).thenReturn(USER);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(isRegulatorUser);

      submissionApprovalService.submitSubmission(submissionDetail);

      verifyNoInteractions(fieldSubmissionStatusService);
    }

    @ParameterizedTest
    @MethodSource("allFieldSubmissionTypes")
    void whenFieldSubmission_thenVerifyCallsToCleanReservoirData(SubmissionType submissionType) {
      submissionDetail.setSubmissionType(submissionType);

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(reservoirTotalsCleanAndCopyService, times(1)).cleanAndCopyVolumetricTotals(submissionDetail);
      verify(reservoirSubmissionSectionService, times(1)).cleanReservoirSubmissionSections(submissionDetail);
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
      when(userService.getUser()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
          .thenReturn(true);
      submissionApprovalService.approveSubmissionOrThrow(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
      verify(oldSubmissionEmailService).sendSubmissionApprovalEmail(submissionDetail);
    }

    @Test
    void approveSubmission_verifyNoMethodCalls_whenCannotApprove() {
      when(userService.getUserWuaId()).thenReturn(1);
      assertThatThrownBy(() -> submissionApprovalService.approveSubmissionOrThrow(submissionDetail))
          .isExactlyInstanceOf(ActionNotAllowedException.class)
          .hasMessage("Submission cannot be accepted by user with ID %s"
              .formatted(USER.wuaId()));
      verifyNoInteractions(submissionDetailPersistenceService);
      verifyNoInteractions(oldSubmissionEmailService);
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"}
    )
    void approveSubmission_canApproveAndFieldSubmission_thenVerifyFieldStatusUpdate(SubmissionType submissionType) {
      when(userService.getUser()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
          .thenReturn(true);
      submissionDetail.setSubmissionType(submissionType);

      submissionApprovalService.approveSubmissionOrThrow(submissionDetail);

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
      when(userService.getUser()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
          .thenReturn(true);

      submissionDetail.setSubmissionType(submissionType);

      submissionApprovalService.approveSubmissionOrThrow(submissionDetail);

      verifyNoInteractions(fieldSubmissionStatusService);
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam() {
      when(userService.getUser()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
          .thenReturn(true);

      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail)).isTrue();
    }

    @Test
    void canApproveSubmission_whenRegulator_notInResponsibleTeam() {
      when(userService.getUser()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
          .thenReturn(false);

      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail)).isFalse();
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam_hasUnresolvedUpdate_assertFalse() {
      when(userService.getUser()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
          .thenReturn(true);
      when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
          .thenReturn(true);
      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail)).isFalse();
    }
  }
}