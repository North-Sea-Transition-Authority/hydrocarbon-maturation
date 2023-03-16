package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class RegulatorManagedInformationFormValidatorTest {

  @InjectMocks
  private RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator;


  @Test
  void supports_whenRegulatorManagedInformationFormClass_thenTrue() {
    var supportedClass = RegulatorManagedInformationForm.class;
    assertTrue(regulatorManagedInformationFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotRegulatorManagedInformationFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(regulatorManagedInformationFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_assertNoErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenInValidForm_assertErrors() {
    var form = new RegulatorManagedInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult);

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    AssertionsForInterfaceTypes.assertThat(errors)
        .containsExactly(
            entry("geographicalArea", Set.of("geographicalArea.required"))
        );
    AssertionsForInterfaceTypes.assertThat(errorMessages)
        .containsExactly(
            entry("geographicalArea", Set.of("Select a geographical area"))
        );
  }

  private RegulatorManagedInformationForm getValidForm() {
    var form = new RegulatorManagedInformationForm();
    form.setGeographicalArea(GeographicalArea.ONSHORE);
    return form;
  }
}