package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCaseVerifyFormValidatorTest {

  private static OutlineForMaximumCaseVerifyFormValidator outlineForMaximumCaseVerifyFormValidator;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void beforeAll() {
    outlineForMaximumCaseVerifyFormValidator = new OutlineForMaximumCaseVerifyFormValidator();
  }

  @Test
  void supports_whenOutlineForMaximumCaseFormClass_thenTrue() {
    var supportedClass = OutlineForMaximumCaseVerifyForm.class;
    assertTrue(outlineForMaximumCaseVerifyFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotOutlineForMaximumCaseFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(outlineForMaximumCaseVerifyFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidOutlineForMaximumCaseForm();

    var bindingResult = validateOutlineForMaximumCaseForm_fullValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new OutlineForMaximumCaseVerifyForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOutlineForMaximumCaseForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("shapeIsCorrect", Set.of("shapeIsCorrect.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noShapeIsCorrectEntered_thenValidationErrors() {
    var form = getValidOutlineForMaximumCaseForm();
    form.setShapeIsCorrect(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOutlineForMaximumCaseForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("shapeIsCorrect", Set.of("shapeIsCorrect.required"))
        );
  }

  @Test
  void validate_whenEmptyFormAndPartialValidation_thenNoValidationErrors() {
    var form = new OutlineForMaximumCaseVerifyForm();
    var bindingResult = validateOutlineForMaximumCaseForm_partialValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  private BindingResult validateOutlineForMaximumCaseForm_fullValidation(OutlineForMaximumCaseVerifyForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    outlineForMaximumCaseVerifyFormValidator.validate(form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    return bindingResult;
  }

  private BindingResult validateOutlineForMaximumCaseForm_partialValidation(OutlineForMaximumCaseVerifyForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    outlineForMaximumCaseVerifyFormValidator.validate(form, bindingResult, new SubmissionFormValidatorHints(ValidationType.PARTIAL, submissionDetail));

    return bindingResult;
  }

  private OutlineForMaximumCaseVerifyForm getValidOutlineForMaximumCaseForm() {
    var form = new OutlineForMaximumCaseVerifyForm();
    form.setShapeIsCorrect(true);
    return form;
  }
}