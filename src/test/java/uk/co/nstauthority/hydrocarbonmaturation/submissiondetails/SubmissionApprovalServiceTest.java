package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionApprovalServiceTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private SubmissionEmailService submissionEmailService;
  @Mock
  private SubmissionUpdateService submissionUpdateService;
  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @Mock
  private ResponsibleTeamService responsibleTeamService;
  @Mock
  private AreaTeamService areaTeamService;
  @InjectMocks
  private SubmissionApprovalService underTest;

  @Nested
  class WhenSubmittingSubmission {

    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setup() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    }

    @Test
    void whenVersionNumberGreaterThanOne_thenVerifyUpdateResolved() {
      submissionDetail.setVersionNumber(2);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
      when(areaTeamService.isAreaUser(USER)).thenReturn(false);

      underTest.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verify(submissionUpdateService, times(1))
          .resolveUpdateOnSubmission(submissionDetail);
      verify(submissionEmailService, times(1))
          .sendSubmittedSubmissionEmails(submissionDetail);
    }

    @Test
    void andCanNotAutoApprove_ThenVerifyCallToSubmit() {
      submissionDetail.setVersionNumber(1);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
      when(areaTeamService.isAreaUser(USER)).thenReturn(false);

      underTest.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verifyNoInteractions(submissionUpdateService);
      verify(submissionEmailService, times(1))
          .sendSubmittedSubmissionEmails(submissionDetail);
    }

    @ParameterizedTest
    @CsvSource(value = { "true, true",
                         "true, false",
                         "false, true"})
    void andCanAutoApprove_ThenVerifyCallsToSubmitAndApprove(boolean isRegulatorUser, boolean isAreaUser) {
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(isRegulatorUser);
      if (!isRegulatorUser) {
        when(areaTeamService.isAreaUser(USER)).thenReturn(isAreaUser);
      }

      underTest.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
      verifyNoInteractions(submissionEmailService);
      verifyNoInteractions(submissionUpdateService);
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
      var regulatorManagedInformation = new RegulatorManagedInformation();
      regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          submissionDetail.getSubmission().getResource()))
          .thenReturn(regulatorManagedInformation);
      when(responsibleTeamService
          .isInResponsibleTeam(userDetailService.getUserDetail(), regulatorManagedInformation.getResponsibleTeamType()))
          .thenReturn(true);
      underTest.approveSubmissionOrThrow(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
      verify(submissionEmailService).sendSubmissionApprovalEmail(submissionDetail);
    }

    @Test
    void approveSubmission_verifyNoMethodCalls_whenCannotApprove() {
      var regulatorManagedInformation = new RegulatorManagedInformation();
      regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          submissionDetail.getSubmission().getResource()))
          .thenReturn(regulatorManagedInformation);
      when(userDetailService.getUserWuaId()).thenReturn(1);
      assertThatThrownBy(() -> underTest.approveSubmissionOrThrow(submissionDetail))
          .isExactlyInstanceOf(ActionNotAllowedException.class)
          .hasMessage("Submission cannot be approved by user with ID %s"
              .formatted(USER.wuaId()));
      verifyNoInteractions(submissionDetailPersistenceService);
      verifyNoInteractions(submissionEmailService);
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      var regulatorManagedInformation = new RegulatorManagedInformation();
      regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          submissionDetail.getSubmission().getResource()))
          .thenReturn(regulatorManagedInformation);
      when(responsibleTeamService
          .isInResponsibleTeam(userDetailService.getUserDetail(), regulatorManagedInformation.getResponsibleTeamType()))
          .thenReturn(true);

      assertThat(underTest.canApproveSubmission(submissionDetail)).isTrue();
    }

    @Test
    void canApproveSubmission_whenRegulator_notInResponsibleTeam() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      var regulatorManagedInformation = new RegulatorManagedInformation();
      regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          submissionDetail.getSubmission().getResource()))
          .thenReturn(regulatorManagedInformation);
      when(responsibleTeamService
          .isInResponsibleTeam(userDetailService.getUserDetail(), regulatorManagedInformation.getResponsibleTeamType()))
          .thenReturn(false);

      assertThat(underTest.canApproveSubmission(submissionDetail)).isFalse();
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam_hasUnresolvedUpdate_assertFalse() {
      when(userDetailService.getUserDetail()).thenReturn(USER);

      var regulatorManagedInformation = new RegulatorManagedInformation();
      regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);

      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource()))
          .thenReturn(regulatorManagedInformation);
      when(responsibleTeamService
          .isInResponsibleTeam(userDetailService.getUserDetail(), regulatorManagedInformation.getResponsibleTeamType()))
          .thenReturn(true);
      when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
          .thenReturn(true);
      assertThat(underTest.canApproveSubmission(submissionDetail)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ResponsibleTeamType.class, names = "EXPLORATION", mode = EnumSource.Mode.EXCLUDE)
    void canApproveSubmission_whenRegulator_responsibleTeamNotExploration(ResponsibleTeamType responsibleTeamType) {
      var regulatorManagedInformation = new RegulatorManagedInformation();
      regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          submissionDetail.getSubmission().getResource()))
          .thenReturn(regulatorManagedInformation);

      assertThat(underTest.canApproveSubmission(submissionDetail)).isFalse();
    }
  }
}