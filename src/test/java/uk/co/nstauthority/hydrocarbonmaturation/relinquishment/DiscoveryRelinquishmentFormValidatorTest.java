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
class DiscoveryRelinquishmentFormValidatorTest {

  private final DiscoveryRelinquishmentFormValidator discoveryRelinquishmentFormValidator
      = new DiscoveryRelinquishmentFormValidator();

  @Test
  void supports_whenDiscoveryRelinquishmentForm_thenTrue() {
    var supportedClass = DiscoveryRelinquishmentForm.class;
    assertTrue(discoveryRelinquishmentFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenDiscoveryRelinquishmentForm_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(discoveryRelinquishmentFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidDiscoveryForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryRelinquishmentFormValidator.validate(form, bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyDiscoverySelector_thenValidationErrors() {
    var form = new DiscoveryRelinquishmentForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryRelinquishmentFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("discoverySelectedOption", Set.of("discoverySelectedOption.required"))
        );
  }

  private DiscoveryRelinquishmentForm getValidForm() {
    var form = new DiscoveryRelinquishmentForm();
    form.setDiscoverySelectedOption(UUID.randomUUID().toString());
    return form;
  }
}