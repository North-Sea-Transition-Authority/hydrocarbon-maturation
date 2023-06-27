package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionSummaryFormServiceTest {

  @Mock
  private SubmissionReadyService submissionReadyService;

  @Mock
  private SubmissionSummaryFormValidator submissionSummaryFormValidator;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private AreaTeamService areaTeamService;

  @InjectMocks
  private SubmissionSummaryFormService submissionSummaryFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void validate_verifyMethodCall(Boolean submissionSubmittable) {
    var submissionSummaryForm = new SubmissionSummaryForm();
    var bindingResult = new BeanPropertyBindingResult(submissionSummaryForm, "form");
    var hintsCaptor = ArgumentCaptor.forClass(SubmitValidatorHints.class);

    when(submissionReadyService.isSubmissionSubmittable(submissionDetail)).thenReturn(submissionSubmittable);
    when(regulatorTeamService.isRegulatorUser(any())).thenReturn(false);

    submissionSummaryFormService.validate(submissionSummaryForm, bindingResult, submissionDetail);

    verify(submissionSummaryFormValidator, times(1))
        .validate(
            eq(submissionSummaryForm), eq(bindingResult), hintsCaptor.capture());

    assertThat(hintsCaptor.getValue())
        .extracting(
            SubmitValidatorHints::isCompleted,
            SubmitValidatorHints::isRegulatorOrAreaTeamUser)
        .containsExactly(
            submissionSubmittable,
            false
        );
  }

  @ParameterizedTest
  @CsvSource(value = { "true, true",
      "true, false",
      "false, true"})
  void isRegulator_verifyMethodCall(boolean isRegulatorUser, boolean isAreaUser) {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(userDetailService.getUserDetail()).thenReturn(user);
    when(regulatorTeamService.isRegulatorUser(user)).thenReturn(isRegulatorUser);
    if (!isRegulatorUser) {
      when(areaTeamService.isAreaUser(user)).thenReturn(isAreaUser);
    }
    var result = submissionSummaryFormService.isRegulatorOrAreaTeamUser();

    if (isRegulatorUser || isAreaUser) {
      assertThat(result).isTrue();
    } else {
      assertThat(result).isFalse();
    }

    verify(regulatorTeamService).isRegulatorUser(user);
    if (!isRegulatorUser) {
      verify(areaTeamService).isAreaUser(user);
    }
  }
}