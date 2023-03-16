package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
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
  void validate_whenValidForm_notRelinquishment_assertNoErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false));

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenInValidFormNotRelinquishment_assertErrors() {
    var form = new RegulatorManagedInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false));

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

  @Test
  void validate_whenInValidFormWhenRelinquishment_assertErrors() {
    var form = new RegulatorManagedInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(true));

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

  @Test
  void validate_whenInValidFormMultipleUploadsWhenRelinquishment_assertErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    form.setRelinquishmentReport(List.of(new FileUploadForm(), new FileUploadForm()));
    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(true));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    AssertionsForInterfaceTypes.assertThat(errors)
        .containsExactly(
            entry("relinquishmentReport", Set.of("relinquishmentReport.limitExceeded"))
            );
    AssertionsForInterfaceTypes.assertThat(errorMessages)
        .containsExactly(
            entry("relinquishmentReport", Set.of("Upload at most one relinquishment report"))
        );
  }

  @Test
  void validate_whenInvalidFormNoFileDescriptionWhenRelinquishment_assertErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    form.setRelinquishmentReport(List.of(new FileUploadForm()));
    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(true));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    AssertionsForInterfaceTypes.assertThat(errors)
        .containsExactly(
            entry("relinquishmentReport[0].uploadedFileDescription",
                Set.of("relinquishmentReport[0].uploadedFileDescription.required"))
        );
    AssertionsForInterfaceTypes.assertThat(errorMessages)
        .containsExactly(
            entry("relinquishmentReport[0].uploadedFileDescription",
                Set.of("Enter a description of this file"))
        );
  }

  @Test
  void validate_whenHintsNotProvided_asserThrows() {
    var form = new RegulatorManagedInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThatThrownBy(() -> regulatorManagedInformationFormValidator
        .validate(form, bindingResult))
        .isExactlyInstanceOf(ActionNotAllowedException.class)
        .hasMessage("Must provide validation hints to this validator");
  }

  private RegulatorManagedInformationForm getValidForm() {
    var form = new RegulatorManagedInformationForm();
    var relinquishmentReportUpload = new FileUploadForm();
    relinquishmentReportUpload.setUploadedFileDescription("desc");
    relinquishmentReportUpload.setUploadedFileId(UUID.randomUUID());
    relinquishmentReportUpload.setUploadedFileInstant(Instant.now());
    form.setGeographicalArea(GeographicalArea.ONSHORE);
    form.setRelinquishmentReport(List.of(relinquishmentReportUpload));
    return form;
  }
}