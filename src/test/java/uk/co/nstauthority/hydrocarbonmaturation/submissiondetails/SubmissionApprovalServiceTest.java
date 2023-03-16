package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
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
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @InjectMocks
  private SubmissionApprovalService underTest;

  @Nested
  class WhenSubmittingSubmission {

    @Mock
    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setup() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
    }

    @Test
    void andCanNotAutoApprove_ThenVerifyCallToSubmit() {
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);

      underTest.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
    }

    @Test
    void andCanAutoApprove_ThenVerifyCallsToSubmitAndApprove() {
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);

      underTest.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
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
      var responsibleTeam = new RegulatorManagedInformation();
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          submissionDetail.getResourceId()))
          .thenReturn(responsibleTeam);
      when(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION)).thenReturn(true);
      underTest.approveSubmissionOrThrow(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
      verify(submissionEmailService).sendSubmissionApprovalEmail(submissionDetail);
    }

    @Test
    void approveSubmission_verifyNoMethodCalls_whenCannotApprove() {
      var responsibleTeam = new RegulatorManagedInformation();
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
          submissionDetail.getResourceId()))
          .thenReturn(responsibleTeam);
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
      var responsibleTeam = new RegulatorManagedInformation();
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getResourceId()))
          .thenReturn(responsibleTeam);
      when(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION)).thenReturn(true);

      assertThat(underTest.canApproveSubmission(submissionDetail)).isTrue();
    }

    @Test
    void canApproveSubmission_whenRegulator_notInResponsibleTeam() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      var responsibleTeam = new RegulatorManagedInformation();
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getResourceId()))
          .thenReturn(responsibleTeam);
      when(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION)).thenReturn(false);

      assertThat(underTest.canApproveSubmission(submissionDetail)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ResponsibleTeamType.class, names = "EXPLORATION", mode = EnumSource.Mode.EXCLUDE)
    void canApproveSubmission_whenRegulator_responsibleTeamNotExploration(ResponsibleTeamType responsibleTeamType) {
      var responsibleTeam = new RegulatorManagedInformation();
      responsibleTeam.setResponsibleTeamType(responsibleTeamType);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getResourceId()))
          .thenReturn(responsibleTeam);

      assertThat(underTest.canApproveSubmission(submissionDetail)).isFalse();
    }
  }
}