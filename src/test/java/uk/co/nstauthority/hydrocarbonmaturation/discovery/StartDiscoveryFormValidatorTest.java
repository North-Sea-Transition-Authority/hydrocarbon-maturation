package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

class StartDiscoveryFormValidatorTest {

  private StartDiscoveryFormValidator startDiscoveryFormValidator;

  @BeforeEach
  void setUp() {
    startDiscoveryFormValidator = new StartDiscoveryFormValidator();
  }

  @Test
  void validate_whenValidForm_thenNoErrors() {
    var form = new StartDiscoveryForm();
    form.setProspectSelectedOption("idhere");

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    startDiscoveryFormValidator.isValid(bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenErrors() {
    var form = new StartDiscoveryForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    startDiscoveryFormValidator.isValid(bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectSelectedOption", Set.of("prospectSelectedOption.required"))
        );
  }
}