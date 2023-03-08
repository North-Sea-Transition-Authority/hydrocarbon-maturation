package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsFormValidatorTest {

  private static OpportunityDetailsFormValidator opportunityDetailsFormValidator;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void beforeAll() {
    opportunityDetailsFormValidator = new OpportunityDetailsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
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
            entry("mapOfOpportunity", Set.of("mapOfOpportunity.belowThreshold"))
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
  void validate_whenInValidForm_noFileDescriptionsUploaded_thenValidationErrors() {
    var form = getValidOpportunityDetailsForm();
    form.setMapOfOpportunity(List.of(new FileUploadForm()));
    form.setGeologicalCrossSection(List.of(new FileUploadForm()));

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("mapOfOpportunity[0].uploadedFileDescription",
                Set.of("mapOfOpportunity[0].uploadedFileDescription.required")),
            entry("geologicalCrossSection[0].uploadedFileDescription",
                Set.of("geologicalCrossSection[0].uploadedFileDescription.required"))
        );
    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("mapOfOpportunity[0].uploadedFileDescription",
                Set.of("Enter a description of this file")),
            entry("geologicalCrossSection[0].uploadedFileDescription",
                Set.of("Enter a description of this file"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenTooManyFilesUploaded_thenValidationErrors(ValidationType validationType) {
    var form = getValidOpportunityDetailsForm();
    var formFileUploadForm = new FileUploadForm();
    formFileUploadForm.setUploadedFileId(UUID.randomUUID());
    formFileUploadForm.setUploadedFileDescription("description");
    form.setMapOfOpportunity(List.of(formFileUploadForm, formFileUploadForm));
    form.setGeologicalCrossSection(List.of(formFileUploadForm, formFileUploadForm));

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("mapOfOpportunity",
                Set.of("mapOfOpportunity.limitExceeded")),
            entry("geologicalCrossSection",
                Set.of("geologicalCrossSection.limitExceeded"))
        );
    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("mapOfOpportunity",
                Set.of("Upload at most one map of opportunity")),
            entry("geologicalCrossSection",
                Set.of("Upload at most one geological cross section"))
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

    opportunityDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    return bindingResult;
  }

  private BindingResult validateOpportunityDetailsForm_partialValidation(OpportunityDetailsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsFormValidator.validate(
        form,
        bindingResult, new SubmissionFormValidatorHints(ValidationType.PARTIAL, submissionDetail)
    );

    return bindingResult;
  }

  private OpportunityDetailsForm getValidOpportunityDetailsForm() {
    var form = new OpportunityDetailsForm();
    form.setOpportunityType(OpportunityType.LEAD);
    form.setKeyEvaluationWorkCarriedOut("Work being carried out");
    form.setKeyWorkRequiredForFurtherMaturation("Key work");
    FileUploadForm fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileId(UUID.randomUUID());
    fileUploadForm.setUploadedFileDescription("File description");
    form.setMapOfOpportunity(List.of(fileUploadForm));
    return form;
  }
}