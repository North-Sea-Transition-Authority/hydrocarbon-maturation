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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class RelinquishmentFormValidatorTest {

  private final RelinquishmentFormValidator relinquishmentFormValidator = new RelinquishmentFormValidator();

  @Test
  void supports_whenRelinquishmentForm_thenTrue() {
    var supportedClass = RelinquishmentForm.class;
    assertTrue(relinquishmentFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenRelinquishmentForm_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(relinquishmentFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidProspectForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    relinquishmentFormValidator.validate(form, bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenValidDiscoveryForm_thenNoValidationErrors() {
    var form = new RelinquishmentForm();
    form.setDiscoverySelectedOption(UUID.randomUUID().toString());
    form.setChosenResourceType(ResourceType.DISCOVERY);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    relinquishmentFormValidator.validate(form, bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyFields_thenValidationErrors() {
    var form = new RelinquishmentForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    relinquishmentFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("chosenResourceType", Set.of("chosenResourceType.required"))
        );
  }

  @Test
  void validate_whenEmptyProspectSelector_thenValidationErrors() {
    var form = new RelinquishmentForm();
    form.setChosenResourceType(ResourceType.PROSPECT);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    relinquishmentFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectSelectedOption", Set.of("prospectSelectedOption.required"))
        );
  }

  @Test
  void validate_whenDiscovery_thenValidationErrors() {
    var form = new RelinquishmentForm();
    form.setChosenResourceType(ResourceType.DISCOVERY);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    relinquishmentFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("discoverySelectedOption", Set.of("discoverySelectedOption.required"))
        );
  }

  private RelinquishmentForm getValidForm() {
    var form = new RelinquishmentForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());
    form.setChosenResourceType(ResourceType.PROSPECT);
    return form;
  }
}