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
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DrillStemTestSummaryFormValidatorTest {

  @InjectMocks
  private DrillStemTestSummaryFormValidator drillStemTestSummaryFormValidator;

  @Test
  void supports_whenDrillStemTestSummaryFormClass_thenTrue() {
    var supportedClass = DrillStemTestSummaryForm.class;
    assertTrue(drillStemTestSummaryFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotDrillStemTestSummaryFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(drillStemTestSummaryFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestSummaryFormValidator.validate(form, bindingResult, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_wheEmptyForm_thenValidationErrors() {
    var form = new DrillStemTestSummaryForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestSummaryFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("drillStemTestSummaryOption", Set.of("drillStemTestSummaryOption.required"))
        );
  }

  private DrillStemTestSummaryForm getValidForm() {
    var form = new DrillStemTestSummaryForm();
    form.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);
    return form;
  }
}