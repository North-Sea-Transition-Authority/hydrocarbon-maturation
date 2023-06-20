package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

class SubmissionTypeSelectFormValidatorTest {

  private SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator;

  @BeforeEach
  void setup() {
    submissionTypeSelectFormValidator = new SubmissionTypeSelectFormValidator();
  }

  @Test
  void supports_whenProspectPurposeTypeSelectFormClass_thenTrue() {
    var supportedClass = SubmissionTypeSelectForm.class;
    assertTrue(submissionTypeSelectFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotProspectPurposeTypeSelectFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(submissionTypeSelectFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = new SubmissionTypeSelectForm();
    form.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var bindingResult = validateDiscoveryPurposeTypeSelectForm(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors() {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = validateDiscoveryPurposeTypeSelectForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).containsExactly(
        entry("submissionType", Set.of("submissionType.required"))
    );
  }

  private BindingResult validateDiscoveryPurposeTypeSelectForm(SubmissionTypeSelectForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionTypeSelectFormValidator.validate(form, bindingResult);

    return bindingResult;
  }

  private static class NonSupportedClass {
  }
}