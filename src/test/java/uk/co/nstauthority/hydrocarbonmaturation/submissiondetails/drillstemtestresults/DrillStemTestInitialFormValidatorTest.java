package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class DrillStemTestInitialFormValidatorTest {

  @InjectMocks
  private DrillStemTestInitialFormValidator drillStemTestInitialFormValidator;

  @Test
  void supports_whenDrillStemTestInitialFormClass_thenTrue() {
    var supportedClass = DrillStemTestInitialForm.class;
    assertTrue(drillStemTestInitialFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotDrillStemTestInitialFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(drillStemTestInitialFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestInitialFormValidator.validate(form, bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_wheEmptyForm_thenValidationErrors() {
    var form = new DrillStemTestInitialForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestInitialFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("hasDrillStemTestResult", Set.of("hasDrillStemTestResult.required"))
        );
  }

  private DrillStemTestInitialForm getValidForm() {
    var form = new DrillStemTestInitialForm();
    form.setHasDrillStemTestResult(true);
    return form;
  }

}