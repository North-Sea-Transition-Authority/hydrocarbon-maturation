package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
  void validate_whenChecked_noErrors(Boolean submittable) {
    var form = new SubmissionSummaryForm();
    form.setCompleteWithoutMandatoryInformation(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionSummaryFormValidator.validate(form, bindingResult, submittable);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenNotChecked_notSubmittable() {
    var form = new SubmissionSummaryForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionSummaryFormValidator.validate(form, bindingResult, false);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("completeWithoutMandatoryInformation", Set.of("completeWithoutMandatoryInformation.required"))
        );
  }
}