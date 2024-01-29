package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

class FileValidationUtilTest {

  @Test
  void testValidator_whenMaxAndMin_isOne_assertErrors() {
    var maxMinValidator = FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most 1")
        .withMinimumNumberOfFiles(1, "Upload at least 1");
    var bindingResult = new BeanPropertyBindingResult(new TestForm(), "form");
    var fileForms = List.of(getUploadedFileForm("Desc"), getUploadedFileForm("descr 2"));
    maxMinValidator.validate(bindingResult, fileForms, "fileUploadField");

    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errorMessages)
        .containsExactly(
            entry("fileUploadField", Set.of("Upload at most 1"))
        );

    assertThat(errors)
        .containsExactly(
            entry("fileUploadField", Set.of("fileUploadField.limitExceeded"))
        );
  }

  @Test
  void testValidator_whenMaxAndMin_isNone_assertErrors() {
    var maxMinValidator = FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most 1")
        .withMinimumNumberOfFiles(1, "Upload at least 1");
    var bindingResult = new BeanPropertyBindingResult(new TestForm(), "form");

    maxMinValidator.validate(bindingResult, List.of(), "fileUploadField");

    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errorMessages)
        .containsExactly(
            entry("fileUploadField", Set.of("Upload at least 1"))
        );

    assertThat(errors)
        .containsExactly(
            entry("fileUploadField", Set.of("fileUploadField.belowThreshold"))
        );
  }

  @Test
  void testValidator_whenNoDescription_assertErrors() {
    var validator = FileValidationUtil.validator();
    var bindingResult = new BeanPropertyBindingResult(new TestForm(), "form");
    var fileForms = List.of(
        getUploadedFileForm(null),
        getUploadedFileForm(null));
    validator.validate(bindingResult, fileForms, "fileUploadField");

    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errorMessages)
        .containsExactly(
            entry("fileUploadField[0].uploadedFileDescription",
                Set.of("Enter a description of this file")),
            entry("fileUploadField[1].uploadedFileDescription",
                Set.of("Enter a description of this file"))
        );

    assertThat(errors)
        .containsExactly(
            entry("fileUploadField[0].uploadedFileDescription",
                Set.of("fileUploadField[0].uploadedFileDescription.required")),
            entry("fileUploadField[1].uploadedFileDescription",
                Set.of("fileUploadField[1].uploadedFileDescription.required"))
        );
  }

  @Test
  void testValidator_whenIsPartiallyValidated_noDescriptionAndTooManyUploaded_assertErrors() {
    var validator = FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Too many files")
        .isPartiallyValidated();
    var bindingResult = new BeanPropertyBindingResult(new TestForm(), "form");
    var fileForms = List.of(
        getUploadedFileForm(null),
        getUploadedFileForm(null));
    validator.validate(bindingResult, fileForms, "fileUploadField");

    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errorMessages)
        .containsExactly(
            entry("fileUploadField",
                Set.of("Too many files"))
        );

    assertThat(errors)
        .containsExactly(
            entry("fileUploadField",
                Set.of("fileUploadField.limitExceeded"))
        );
  }

  private UploadedFileForm getUploadedFileForm(String description) {
    var form = new UploadedFileForm();
    form.setUploadedFileDescription(description);
    form.setUploadedFileId(UUID.randomUUID());
    return form;
  }

  @Nested
  private class TestForm implements SubmissionForm {
    private List<UploadedFileForm> fileUploadField = new ArrayList<>();

    public List<UploadedFileForm> getFileUploadField() {
      return fileUploadField;
    }

    public void setFileUploadField(
        List<UploadedFileForm> fileUploadField) {
      this.fileUploadField = fileUploadField;
    }
  }
}