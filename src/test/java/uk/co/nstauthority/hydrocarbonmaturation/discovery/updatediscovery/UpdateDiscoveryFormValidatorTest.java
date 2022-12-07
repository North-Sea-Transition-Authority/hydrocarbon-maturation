package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class UpdateDiscoveryFormValidatorTest {

  @InjectMocks
  private UpdateDiscoveryFormValidator updateDiscoveryFormValidator;

  @Test
  void supports_whenUpdateDiscoveryForm_thenTrue() {
    var supportedClass = UpdateDiscoveryForm.class;
    assertTrue(updateDiscoveryFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotSupportedClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(updateDiscoveryFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    updateDiscoveryFormValidator.validate(form, bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyFields_thenValidationErrors() {
    var form = getValidForm();
    form.setDiscoverySelectedOption(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    updateDiscoveryFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("discoverySelectedOption", Set.of("discoverySelectedOption.required"))
        );
  }

  private UpdateDiscoveryForm getValidForm() {
    var form = new UpdateDiscoveryForm();
    form.setDiscoverySelectedOption(UUID.randomUUID().toString());

    return form;
  }
}