package uk.co.nstauthority.hydrocarbonmaturation.newresource;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class NewResourceFormValidatorTest {

  @InjectMocks
  private NewResourceFormValidator newResourceFormValidator;

  @Test
  void supports_whenSupportedClass() {
    var supportedClass = NewResourceForm.class;
    assertTrue(newResourceFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotSupportedClass() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(newResourceFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenNoHintsProvided_assertThrow() {
    assertThatThrownBy(() -> newResourceFormValidator
        .validate(new NewResourceForm(), ReverseRouter.emptyBindingResult()))
        .hasMessage("You must provide NewResourceFormValidatorHints")
        .isExactlyInstanceOf(ActionNotAllowedException.class);
  }

  @Test
  void validate_whenFilledValidForm_asRegulator_noErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    newResourceFormValidator.validate(form, bindingResult,
        new NewResourceFormValidatorHints(true, ResourceType.PROSPECT));
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenEmptyForm_asNotRegulator_errors() {
    var form =  new NewResourceForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    newResourceFormValidator.validate(form, bindingResult,
        new NewResourceFormValidatorHints(false, ResourceType.DISCOVERY));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    AssertionsForInterfaceTypes.assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );
  }

  @Test
  void validate_whenEmptyForm_asRegulator_errors() {
    var form =  new NewResourceForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    newResourceFormValidator.validate(form, bindingResult,
        new NewResourceFormValidatorHints(true, ResourceType.PROSPECT));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    AssertionsForInterfaceTypes.assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );
  }

  @Test
  void validate_whenFilledValidForm_asNotRegulator_noErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    newResourceFormValidator.validate(form, bindingResult,
        new NewResourceFormValidatorHints(false, ResourceType.DISCOVERY));
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  private NewResourceForm getValidForm() {
    var form = new NewResourceForm();
    form.setResourceName("resource");
    form.setResponsibleOrganisationId("1");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    return form;
  }
}