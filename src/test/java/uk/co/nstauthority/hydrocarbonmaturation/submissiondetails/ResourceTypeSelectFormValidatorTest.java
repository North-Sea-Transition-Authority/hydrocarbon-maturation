package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

class ResourceTypeSelectFormValidatorTest {

  private ResourceTypeSelectFormValidator resourceTypeSelectFormValidator;

  @BeforeEach
  void setup() {
    resourceTypeSelectFormValidator = new ResourceTypeSelectFormValidator();
  }

  @Test
  void supports_whenResourceTypeSelectFormClass_thenTrue() {
    var supportedClass = ResourceTypeSelectForm.class;
    assertTrue(resourceTypeSelectFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotResourceTypeSelectFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(resourceTypeSelectFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = new ResourceTypeSelectForm();
    form.setResourceType(ResourceType.PROSPECT);
    var bindingResult = validateResourceTypeSelectForm(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors() {
    var form = new ResourceTypeSelectForm();
    var bindingResult = validateResourceTypeSelectForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).containsExactly(
        entry("resourceType", Set.of("resourceType.required"))
    );
  }

  private BindingResult validateResourceTypeSelectForm(ResourceTypeSelectForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    resourceTypeSelectFormValidator.validate(form, bindingResult);

    return bindingResult;
  }

  private static class NonSupportedClass {
  }
}