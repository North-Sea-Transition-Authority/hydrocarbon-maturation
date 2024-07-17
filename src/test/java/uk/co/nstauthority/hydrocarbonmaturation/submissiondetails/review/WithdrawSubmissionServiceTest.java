package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;

@ExtendWith(MockitoExtension.class)
class WithdrawSubmissionServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionPersistenceService submissionPersistenceService;
  @Mock
  private UserService userService;
  @Mock
  private SubmissionEmailService submissionEmailService;
  @Mock
  private WithdrawSubmissionFormValidator withdrawSubmissionFormValidator;
  @Mock
  private SubmissionUpdateService submissionUpdateService;
  @Mock
  private ResponsibleTeamService responsibleTeamService;
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
    when(userService.getUser()).thenReturn(USER);
    when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
        .thenReturn(true);
    when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(false);
    var form = new WithdrawSubmissionForm();
    form.setWithdrawReason("theReason");
    underTest.withdrawSubmissionOrThrow(submissionDetail, form);

    verify(submissionPersistenceService).withdrawSubmission(submissionDetail.getSubmission(), form.getWithdrawReason());
    verify(submissionEmailService).sendSubmissionWithdrawalEmail(submissionDetail);
  }

  @Test
  void withdrawSubmission_verifyNoMethodCalls_whenCannotwithdraw() {
    when(userService.getUserWuaId()).thenReturn(1);
    var form = new WithdrawSubmissionForm();

    assertThatThrownBy(() -> underTest.withdrawSubmissionOrThrow(submissionDetail, form))
        .isExactlyInstanceOf(ActionNotAllowedException.class)
        .hasMessage("Submission cannot be withdrawn by user with ID %s"
            .formatted(USER.wuaId()));

    verifyNoInteractions(submissionPersistenceService);
    verifyNoInteractions(submissionEmailService);
  }

  @Test
  void canWithdrawSubmission_whenRegulator_inResponsibleTeam() {
    when(userService.getUser()).thenReturn(USER);
    when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
        .thenReturn(true);
    when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(false);
    assertThat(underTest.canWithdrawSubmission(submissionDetail)).isTrue();
  }

  @Test
  void canWithdrawSubmission_whenRegulator_inResponsibleTeam_hasUnresolvedUpdate_assertFalse() {
    when(userService.getUser()).thenReturn(USER);
    when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
        .thenReturn(true);
    when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(true);
    assertThat(underTest.canWithdrawSubmission(submissionDetail)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"SUBMITTED"}, mode = EnumSource.Mode.EXCLUDE)
  void canWithdrawSubmission_whenNotSubmitted_assertFalse(SubmissionDetailStatus status) {
    submissionDetail.setStatus(status);

    assertThat(underTest.canWithdrawSubmission(submissionDetail)).isFalse();
  }

  @Test
  void canWithdrawSubmission_whenRegulator_notInResponsibleTeam() {
    when(userService.getUser()).thenReturn(USER);
    when(responsibleTeamService.isInResponsibleTeam(userService.getUser(), submissionDetail))
        .thenReturn(false);

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