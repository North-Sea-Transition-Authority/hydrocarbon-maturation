package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class SubmissionSummaryFormValidatorTest {

  @Mock
  private InternalTeamService internalTeamService;

  @Mock
  private SubmissionReadyService submissionReadyService;

  @InjectMocks
  private SubmissionSummaryFormValidator submissionSummaryFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void validate_whenSubmissionCompleted_whenRegulatorOrAreaTeamUser_noErrors() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new SubmissionSummaryForm();
    form.setCompleteWithoutMandatoryInformation(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(internalTeamService.isRegulatorOrAreaTeamUser()).thenReturn(true);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail)).thenReturn(true);
    submissionSummaryFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenNotRegulator_whenSubmittable_noErrors() {
    var form = new SubmissionSummaryForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(internalTeamService.isRegulatorOrAreaTeamUser()).thenReturn(false);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail)).thenReturn(true);
    submissionSummaryFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenNotChecked_whenRegulator_notSubmittable() {
    var form = new SubmissionSummaryForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(internalTeamService.isRegulatorOrAreaTeamUser()).thenReturn(true);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail)).thenReturn(false);
    submissionSummaryFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("completeWithoutMandatoryInformation", Set.of("completeWithoutMandatoryInformation.required"))
        );
  }
}