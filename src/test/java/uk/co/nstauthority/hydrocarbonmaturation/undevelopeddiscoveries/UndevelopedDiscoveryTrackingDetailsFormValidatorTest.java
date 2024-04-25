
package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static java.util.Map.entry;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryTrackingDetailsFormValidatorTest {

  @InjectMocks
  private UndevelopedDiscoveryTrackingDetailsFormValidator formValidator;

  @Test
  void isValid() {
    var validForm = new UndevelopedDiscoveryTrackingDetailsForm();
    validForm.setIsUndevelopedDiscovery(Boolean.FALSE);

    var errors = validateForm(validForm);

    assertFalse(errors.hasErrors());
  }

  @Test
  void isInvalid() {
    var invalidForm = new UndevelopedDiscoveryTrackingDetailsForm();

    var errors = validateForm(invalidForm);

    var extractedErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(extractedErrors).containsExactly(
        entry("isUndevelopedDiscovery", Set.of("isUndevelopedDiscovery.required"))
    );
  }

  private BindingResult validateForm(UndevelopedDiscoveryTrackingDetailsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    formValidator.isValidTrackingDetails(bindingResult);
    return bindingResult;
  }
}
