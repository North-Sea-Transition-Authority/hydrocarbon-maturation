package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationFormValidatorTest {

  private AdditionalInformationFormValidator additionalInformationFormValidator;

  @BeforeEach
  void setUp() {
    additionalInformationFormValidator = new AdditionalInformationFormValidator();
  }

  @Test
  void supports_whenAdditionalInformationFormClass_thenTrue() {
    var supportedClass = AdditionalInformationForm.class;
    assertTrue(additionalInformationFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotAdditionalInformationFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(additionalInformationFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidAdditionalInformationForm();

    var bindingResult = validateAdditionalInformationForm_fullValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new AdditionalInformationForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateAdditionalInformationForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("providingAdditionalComments", Set.of("providingAdditionalComments.required")),
            entry("providingAdditionalDocuments", Set.of("providingAdditionalDocuments.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noAdditionalCommentsEntered_thenValidationErrors() {
    var form = getValidAdditionalInformationForm();
    form.setAdditionalComments(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateAdditionalInformationForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("additionalComments", Set.of("additionalComments.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noUploadEntered_thenValidationErrors() {
    var form = getValidAdditionalInformationForm();
    form.setAdditionalDocuments(List.of());

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateAdditionalInformationForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("additionalDocuments", Set.of("additionalDocuments.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noAdditionalDocumentFileDescriptionUploaded_thenValidationErrors() {
    var form = getValidAdditionalInformationForm();
    form.setAdditionalDocuments(List.of(new FileUploadDetail()));

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateAdditionalInformationForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("additionalDocuments[0].uploadedFileDescription", Set.of("additionalDocuments[0].uploadedFileDescription.required"))
        );
  }

  @Test
  void validate_whenEmptyFormAndPartialValidation_thenNoValidationErrors() {
    var form = new AdditionalInformationForm();
    var bindingResult = validateAdditionalInformationForm_partialValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  private BindingResult validateAdditionalInformationForm_fullValidation(AdditionalInformationForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    additionalInformationFormValidator.validate(form, bindingResult, ValidationType.FULL);

    return bindingResult;
  }

  private BindingResult validateAdditionalInformationForm_partialValidation(AdditionalInformationForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    additionalInformationFormValidator.validate(form, bindingResult, ValidationType.PARTIAL);

    return bindingResult;
  }

  private AdditionalInformationForm getValidAdditionalInformationForm() {
    var form = new AdditionalInformationForm();
    form.setAdditionalComments("comments");
    form.setProvidingAdditionalDocuments(true);
    FileUploadDetail fileUploadDetail = new FileUploadDetail();
    fileUploadDetail.setUploadedFileDescription("File description");
    form.setAdditionalDocuments(List.of(fileUploadDetail));
    form.setProvidingAdditionalComments(true);
    return form;
  }
}