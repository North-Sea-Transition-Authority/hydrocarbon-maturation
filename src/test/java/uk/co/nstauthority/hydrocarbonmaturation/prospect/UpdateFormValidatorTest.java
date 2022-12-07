package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class UpdateFormValidatorTest {

  private final UpdateFormValidator updateFormValidator = new UpdateFormValidator();

  @Test
  void supports_whenUpdateForm_thenTrue() {
    var supportedClass = UpdateProspectForm.class;
    assertTrue(updateFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenUpdateForm_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(updateFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    updateFormValidator.validate(form, bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyFields_thenValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(null);
    form.setProspectSelectedOption(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    updateFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectSelectedOption", Set.of("prospectOption.required")),
            entry("informationSource", Set.of("informationSource.required"))
        );
  }

  @Test
  void validate_whenOneEmptyField_thenSingleError() {
    var form = getValidForm();
    form.setInformationSource(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    updateFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("informationSource", Set.of("informationSource.required"))
        );
  }

  @Test
  void validate_whenOtherFieldEmpty_thenSingleError() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.OTHER);
    form.setOtherSourceOfInformation(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    updateFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("otherSourceOfInformation", Set.of("otherSourceOfInformation.required"))
        );
  }

  @Test
  void validate_whenLicenceApplicatio_noErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.LICENCE_APPLICATION);
    form.setLicenceApplication("ref");
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    updateFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .isEmpty();
  }

  @Test
  void validate_whenLicenceApplicationEmpty_thenSingleError() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.LICENCE_APPLICATION);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    updateFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("licenceApplication", Set.of("licenceApplication.required"))
        );
  }

  private UpdateProspectForm getValidForm() {
    var form = new UpdateProspectForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());
    form.setInformationSource(InformationSource.OTHER);
    form.setOtherSourceOfInformation("Other source");

    return form;
  }
}