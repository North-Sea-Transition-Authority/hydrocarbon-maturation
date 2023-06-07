package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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

class ProspectPurposeTypeSelectFormValidatorTest {

  private ProspectPurposeTypeSelectFormValidator prospectPurposeTypeSelectFormValidator;

  @BeforeEach
  void setup() {
    prospectPurposeTypeSelectFormValidator = new ProspectPurposeTypeSelectFormValidator();
  }

  @Test
  void supports_whenProspectPurposeTypeSelectFormClass_thenTrue() {
    var supportedClass = ProspectPurposeTypeSelectForm.class;
    assertTrue(prospectPurposeTypeSelectFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotProspectPurposeTypeSelectFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(prospectPurposeTypeSelectFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = new ProspectPurposeTypeSelectForm();
    form.setPurposeType(ProspectPurposeType.CREATE_PROSPECT);
    var bindingResult = validateProspectPurposeTypeSelectForm(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors() {
    var form = new ProspectPurposeTypeSelectForm();
    var bindingResult = validateProspectPurposeTypeSelectForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).containsExactly(
        entry("purposeType", Set.of("purposeType.required"))
    );
  }

  private BindingResult validateProspectPurposeTypeSelectForm(ProspectPurposeTypeSelectForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    prospectPurposeTypeSelectFormValidator.validate(form, bindingResult);

    return bindingResult;
  }

  private static class NonSupportedClass {
  }
}