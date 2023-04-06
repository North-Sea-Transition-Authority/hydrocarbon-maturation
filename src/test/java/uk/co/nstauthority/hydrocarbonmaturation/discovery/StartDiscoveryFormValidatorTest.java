package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class StartDiscoveryFormValidatorTest {

  private StartDiscoveryFormValidator startDiscoveryFormValidator;

  @BeforeEach
  void setUp() {
    startDiscoveryFormValidator = new StartDiscoveryFormValidator();
  }

  @Test
  void supports_whenStartDiscoveryFormClass_thenTrue() {
    var supportedClass = StartDiscoveryForm.class;
    assertTrue(startDiscoveryFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotStartDiscoveryFormClass_thenTrue() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(startDiscoveryFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoErrors() {
    var form = new StartDiscoveryForm();
    form.setProspectSelectedOption("idhere");

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    startDiscoveryFormValidator.validate(form, bindingResult, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenErrors() {
    var form = new StartDiscoveryForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    startDiscoveryFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectSelectedOption", Set.of("prospectSelectedOption.required"))
        );
  }
}