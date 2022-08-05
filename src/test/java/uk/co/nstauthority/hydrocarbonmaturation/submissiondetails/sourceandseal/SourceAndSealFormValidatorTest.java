package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class SourceAndSealFormValidatorTest {

  private SourceAndSealFormValidator sourceAndSealFormValidator;

  @BeforeEach
  void setup() {
    sourceAndSealFormValidator = new SourceAndSealFormValidator();
  }

  @Test
  void supports_whenSourceAndSealFormClass_thenTrue() {
    var supportedClass = SourceAndSealForm.class;
    assertTrue(sourceAndSealFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNonSupportedClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(sourceAndSealFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = new SourceAndSealForm();
    form.setSourceFormationName("source formation name");
    form.setTopSealFormationName("top seal formation name");
    var bindingResult = validateSourceAndSealForm(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenNoValidationErrors() {
    var form = new SourceAndSealForm();
    var bindingResult = validateSourceAndSealForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("sourceFormationName", Set.of("sourceFormationName.required")),
            entry("topSealFormationName", Set.of("topSealFormationName.required"))
        );
  }

  private BindingResult validateSourceAndSealForm(SourceAndSealForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    sourceAndSealFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  private static class NonSupportedClass {
  }
}