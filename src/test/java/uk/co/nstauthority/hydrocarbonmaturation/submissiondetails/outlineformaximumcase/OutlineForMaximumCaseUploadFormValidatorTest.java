package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCaseUploadFormValidatorTest {

  private static OutlineForMaximumCaseUploadFormValidator outlineForMaximumCaseUploadFormValidator;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void beforeAll() {
    outlineForMaximumCaseUploadFormValidator = new OutlineForMaximumCaseUploadFormValidator();
  }

  @Test
  void supports_whenOutlineForMaximumCaseFormClass_thenTrue() {
    var supportedClass = OutlineForMaximumCaseUploadForm.class;
    assertTrue(outlineForMaximumCaseUploadFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotOutlineForMaximumCaseFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(outlineForMaximumCaseUploadFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidOutlineForMaximumCaseForm();

    var bindingResult = validateOutlineForMaximumCaseForm_fullValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new OutlineForMaximumCaseUploadForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOutlineForMaximumCaseForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("outlineForMaximumCase", Set.of("outlineForMaximumCase.belowThreshold"))
        );
  }

  @Test
  void validate_whenInValidForm_noOutlineForMaximumCaseEntered_thenValidationErrors() {
    var form = getValidOutlineForMaximumCaseForm();
    form.setOutlineForMaximumCase(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOutlineForMaximumCaseForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("outlineForMaximumCase", Set.of("outlineForMaximumCase.belowThreshold"))
        );
  }

  @Test
  void validate_whenEmptyFormAndPartialValidation_thenNoValidationErrors() {
    var form = new OutlineForMaximumCaseUploadForm();
    var bindingResult = validateOutlineForMaximumCaseForm_partialValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  private BindingResult validateOutlineForMaximumCaseForm_fullValidation(OutlineForMaximumCaseUploadForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    outlineForMaximumCaseUploadFormValidator.validate(form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    return bindingResult;
  }

  private BindingResult validateOutlineForMaximumCaseForm_partialValidation(OutlineForMaximumCaseUploadForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    outlineForMaximumCaseUploadFormValidator.validate(form, bindingResult, new SubmissionFormValidatorHints(ValidationType.PARTIAL, submissionDetail));

    return bindingResult;
  }

  private OutlineForMaximumCaseUploadForm getValidOutlineForMaximumCaseForm() {
    var form = new OutlineForMaximumCaseUploadForm();
    var fileUploadForm = new UploadedFileForm();
    fileUploadForm.setUploadedFileId(UUID.randomUUID());
    fileUploadForm.setUploadedFileDescription("File description");
    form.setOutlineForMaximumCase(List.of(fileUploadForm));
    return form;
  }
}