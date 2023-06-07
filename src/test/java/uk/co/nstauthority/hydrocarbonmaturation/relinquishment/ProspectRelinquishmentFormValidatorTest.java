package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class ProspectRelinquishmentFormValidatorTest {

  private final ProspectRelinquishmentFormValidator prospectRelinquishmentFormValidator
      = new ProspectRelinquishmentFormValidator();

  @Test
  void supports_whenProspectRelinquishmentForm_thenTrue() {
    var supportedClass = ProspectRelinquishmentForm.class;
    assertTrue(prospectRelinquishmentFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenProspectRelinquishmentForm_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(prospectRelinquishmentFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidProspectForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    prospectRelinquishmentFormValidator.validate(form, bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyProspectSelector_thenValidationErrors() {
    var form = new ProspectRelinquishmentForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    prospectRelinquishmentFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectSelectedOption", Set.of("prospectSelectedOption.required"))
        );
  }

  private ProspectRelinquishmentForm getValidForm() {
    var form = new ProspectRelinquishmentForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());
    return form;
  }
}