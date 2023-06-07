package uk.co.nstauthority.hydrocarbonmaturation.discovery;

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

class DiscoveryPurposeTypeSelectFormValidatorTest {

  private DiscoveryPurposeTypeSelectFormValidator discoveryPurposeTypeSelectFormValidator;

  @BeforeEach
  void setup() {
    discoveryPurposeTypeSelectFormValidator = new DiscoveryPurposeTypeSelectFormValidator();
  }

  @Test
  void supports_whenProspectPurposeTypeSelectFormClass_thenTrue() {
    var supportedClass = DiscoveryPurposeTypeSelectForm.class;
    assertTrue(discoveryPurposeTypeSelectFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotProspectPurposeTypeSelectFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(discoveryPurposeTypeSelectFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = new DiscoveryPurposeTypeSelectForm();
    form.setPurposeType(DiscoveryPurposeType.CREATE_DISCOVERY);
    var bindingResult = validateDiscoveryPurposeTypeSelectForm(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors() {
    var form = new DiscoveryPurposeTypeSelectForm();
    var bindingResult = validateDiscoveryPurposeTypeSelectForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).containsExactly(
        entry("purposeType", Set.of("purposeType.required"))
    );
  }

  private BindingResult validateDiscoveryPurposeTypeSelectForm(DiscoveryPurposeTypeSelectForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    discoveryPurposeTypeSelectFormValidator.validate(form, bindingResult);

    return bindingResult;
  }

  private static class NonSupportedClass {
  }
}