package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsFormValidatorTest {

  private static OpportunityDetailsFormValidator opportunityDetailsFormValidator;

  @BeforeAll
  static void beforeAll() {
    opportunityDetailsFormValidator = new OpportunityDetailsFormValidator();
  }

  @Test
  void supports_whenOpportunityDetailsFormClass_thenTrue() {
    var supportedClass = OpportunityDetailsForm.class;
    assertTrue(opportunityDetailsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotOpportunityDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(opportunityDetailsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    OpportunityDetailsForm form = getValidOpportunityDetailsForm();

    var bindingResult = validateOpportunityDetailsForm_fullValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new OpportunityDetailsForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("opportunityType", Set.of("opportunityType.required")),
            entry("keyEvaluationWorkCarriedOut", Set.of("keyEvaluationWorkCarriedOut.required")),
            entry("mapOfOpportunity", Set.of("mapOfOpportunity.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noOpportunityTypeEntered_thenValidationErrors() {
    var form = getValidOpportunityDetailsForm();
    form.setOpportunityType(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("opportunityType", Set.of("opportunityType.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noOtherTypeOfOpportunityEntered_thenValidationErrors() {
    var form = getValidOpportunityDetailsForm();
    form.setOpportunityType(OpportunityType.OTHER);
    form.setOtherTypeOfOpportunity("");

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("otherTypeOfOpportunity", Set.of("otherTypeOfOpportunity.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noKeyEvaluationWorkCarriedOutEntered_thenValidationErrors() {
    var form = getValidOpportunityDetailsForm();
    form.setKeyEvaluationWorkCarriedOut("");

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("keyEvaluationWorkCarriedOut", Set.of("keyEvaluationWorkCarriedOut.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noMapOfOpportunityFileDescriptionUploaded_thenValidationErrors() {
    var form = getValidOpportunityDetailsForm();
    form.setMapOfOpportunity(List.of(new FileUploadForm()));

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("mapOfOpportunity[0].uploadedFileDescription", Set.of("mapOfOpportunity[0].uploadedFileDescription.required"))
        );
  }

  @Test
  void validate_whenEmptyFormAndPartialValidation_thenNoValidationErrors() {
    var form = new OpportunityDetailsForm();
    var bindingResult = validateOpportunityDetailsForm_partialValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  private BindingResult validateOpportunityDetailsForm_fullValidation(OpportunityDetailsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    return bindingResult;
  }

  private BindingResult validateOpportunityDetailsForm_partialValidation(OpportunityDetailsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsFormValidator.validate(form, bindingResult, ValidationType.PARTIAL);

    return bindingResult;
  }

  private OpportunityDetailsForm getValidOpportunityDetailsForm() {
    var form = new OpportunityDetailsForm();
    form.setOpportunityType(OpportunityType.LEAD);
    form.setKeyEvaluationWorkCarriedOut("Work being carried out");
    form.setKeyWorkRequiredForFurtherMaturation("Key work");
    FileUploadForm fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileDescription("File description");
    form.setMapOfOpportunity(List.of(fileUploadForm));
    return form;
  }
}