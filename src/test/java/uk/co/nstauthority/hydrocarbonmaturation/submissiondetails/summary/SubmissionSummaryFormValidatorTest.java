package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

class SubmissionSummaryFormValidatorTest {

  private SubmissionSummaryFormValidator submissionSummaryFormValidator;

  @BeforeEach
  void setUp() {
    submissionSummaryFormValidator = new SubmissionSummaryFormValidator();
  }

  @Test
  void supports_whenSubmissionSummaryFormClass_thenTrue() {
    var supportedClass = SubmissionSummaryForm.class;
    assertTrue(submissionSummaryFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenSubmissionSummaryFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(submissionSummaryFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void validate_whenChecked_whenRegulatorOrAreaTeamUser_noErrors(Boolean submittable) {
    var form = new SubmissionSummaryForm();
    form.setCompleteWithoutMandatoryInformation(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionSummaryFormValidator.validate(
        form, bindingResult, new SubmitValidatorHints(true, submittable));

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenNotRegulator_whenSubmittable_noErrors() {
    var form = new SubmissionSummaryForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionSummaryFormValidator.validate(
        form, bindingResult, new SubmitValidatorHints(false, true));

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenNotChecked_whenRegulator_notSubmittable() {
    var form = new SubmissionSummaryForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionSummaryFormValidator.validate(
        form, bindingResult, new SubmitValidatorHints(true, false));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("completeWithoutMandatoryInformation", Set.of("completeWithoutMandatoryInformation.required"))
        );
  }
}