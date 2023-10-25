package uk.co.nstauthority.hydrocarbonmaturation.newresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ResourceDetailsFormValidatorTest {

  @InjectMocks
  private ResourceDetailsFormValidator resourceDetailsFormValidator;

  @Test
  void supports_whenSupportedClass() {
    var supportedClass = ResourceDetailsForm.class;
    assertTrue(resourceDetailsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotSupportedClass() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(resourceDetailsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenNoHintsProvided_assertThrow() {
    assertThatThrownBy(() -> resourceDetailsFormValidator
        .validate(new ResourceDetailsForm(), ReverseRouter.emptyBindingResult()))
        .hasMessage("You must provide ResourceDetailsFormValidatorHints")
        .isExactlyInstanceOf(ActionNotAllowedException.class);
  }

  @Test
  void validate_whenFilledValidForm_asRegulator_noErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.FULL, true, ResourceType.PROSPECT, null));
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenFilledValidForm_partialValidation_asRegulator_noErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.PARTIAL, true, ResourceType.PROSPECT, null));
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenEmptyForm_partialValidation_asRegulator_whenOrgOnAnEarlierSubmission_assertErrors(
      ValidationType validationType) {
    var form = new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    resourceDetail.setOrganisationRegisteredNumber("(1)");
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            validationType, true, ResourceType.PROSPECT, resourceDetail));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenEmptyForm_partialValidation_asNotRegulator_whenOrgOnAnEarlierSubmission_assertErrors(
      ValidationType validationType) {
    var form = new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    resourceDetail.setOrganisationRegisteredNumber("(1)");
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            validationType, false, ResourceType.PROSPECT, resourceDetail));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required"))
        );

    assertThat(extractedErrorMessages)
        .contains(
            entry("resourceName", Set.of("Enter the prospect name")),
            entry("responsibleOrganisationId",
                Set.of("You cannot remove the responsible organisation" +
                    " from a resource that has a responsible organisation on an earlier accepted submission"))
        );
  }

  @Test
  void validate_whenEmptyForm_partialValidation_asNotRegulator_assertErrorsAndMessages() {
    var form =  new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.PARTIAL, false, ResourceType.DISCOVERY, null));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );

    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("resourceName", Set.of("Enter the discovery name")),
            entry("onshoreOffshore", Set.of("Select onshore or offshore"))
        );
  }

  @Test
  void validate_whenEmptyForm_fullValidation_asNotRegulator_errors() {
    var form =  new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.FULL, false, ResourceType.PROSPECT, null));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );

    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("resourceName", Set.of("Enter the prospect name")),
            entry("responsibleOrganisationId", Set.of("Enter the responsible organisation")),
            entry("onshoreOffshore", Set.of("Select onshore or offshore"))
        );
  }

  @Test
  void validate_whenEmptyForm_asRegulator_errors() {
    var form =  new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.FULL, true, ResourceType.PROSPECT, null));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );
  }

  @Test
  void validate_whenFilledValidForm_asNotRegulator_noErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.FULL, false, ResourceType.DISCOVERY, null));
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  private ResourceDetailsForm getValidForm() {
    var form = new ResourceDetailsForm();
    form.setResourceName("resource");
    form.setResponsibleOrganisationId("1");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    return form;
  }
}