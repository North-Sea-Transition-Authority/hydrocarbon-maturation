package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class WithdrawSubmissionServiceTest {

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
  @Mock
  private WithdrawSubmissionFormValidator withdrawSubmissionFormValidator;
  @InjectMocks
  private WithdrawSubmissionService underTest;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
  }

  @Test
  void withdrawSubmission_verifyMethodCalls_whenCanWithdraw() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);

    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getResourceId()))
        .thenReturn(regulatorManagedInformation);
    when(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION)).thenReturn(true);
    var form = new WithdrawSubmissionForm();
    form.setWithdrawReason("theReason");
    underTest.withdrawSubmissionOrThrow(submissionDetail, form);

    verify(submissionDetailPersistenceService).withdrawSubmission(submissionDetail, form.getWithdrawReason());
    verify(submissionEmailService).sendSubmissionWithdrawalEmail(submissionDetail, form.getWithdrawReason());
  }

  @Test
  void withdrawSubmission_verifyNoMethodCalls_whenCannotwithdraw() {
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(userDetailService.getUserWuaId()).thenReturn(1);
    var form = new WithdrawSubmissionForm();

    assertThatThrownBy(() -> underTest.withdrawSubmissionOrThrow(submissionDetail, form))
        .isExactlyInstanceOf(ActionNotAllowedException.class)
        .hasMessage("Submission cannot be withdrawn by user with ID %s"
            .formatted(USER.wuaId()));

    verifyNoInteractions(submissionDetailPersistenceService);
    verifyNoInteractions(submissionEmailService);
  }

  @Test
  void canWithdrawSubmission_whenRegulator_inResponsibleTeam() {
    when(userDetailService.getUserDetail()).thenReturn(USER);

    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION)).thenReturn(true);

    assertThat(underTest.canWithdrawSubmission(submissionDetail)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"SUBMITTED"}, mode = EnumSource.Mode.EXCLUDE)
  void canWithdrawSubmission_whenNotSubmitted_assertFalse(SubmissionDetailStatus status) {
    submissionDetail.setStatus(status);

    assertThat(underTest.canWithdrawSubmission(submissionDetail)).isFalse();
  }

  @Test
  void canWithdrawSubmission_whenRegulator_notInResponsibleTeam() {
    when(userDetailService.getUserDetail()).thenReturn(USER);

    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION)).thenReturn(false);

    assertThat(underTest.canWithdrawSubmission(submissionDetail)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(value = ResponsibleTeamType.class, names = "EXPLORATION", mode = EnumSource.Mode.EXCLUDE)
  void canWithdrawSubmission_whenRegulator_responsibleTeamNotExploration(ResponsibleTeamType responsibleTeamType) {
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(responsibleTeamType);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);

    assertThat(underTest.canWithdrawSubmission(submissionDetail)).isFalse();
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new WithdrawSubmissionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(bindingResult, form);

    verify(withdrawSubmissionFormValidator).validate(form, bindingResult);
  }
}