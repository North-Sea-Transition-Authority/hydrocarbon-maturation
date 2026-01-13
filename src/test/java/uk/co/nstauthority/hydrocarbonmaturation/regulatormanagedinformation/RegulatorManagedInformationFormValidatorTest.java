package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
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
  void validate_whenValidForm_notRelinquishmentNotResponsibleTeamUpdate_assertNoErrors() {
    var form = getValidForm();
    form.setResponsibleTeamType(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false, false, false, false));

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenValidForm_notRelinquishmentResponsibleTeamUpdate_assertNoErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false, true, false, false));

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenInValidFormNotRelinquishment_assertErrors() {
    var form = new RegulatorManagedInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false, false, false, false));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("geographicalArea", Set.of("geographicalArea.required"))
        );
    assertThat(errorMessages)
        .containsExactly(
            entry("geographicalArea", Set.of("Select a geographical area"))
        );
  }

  @Test
  void validate_whenInValidFormWhenRelinquishment_assertErrors() {
    var form = new RegulatorManagedInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(true, false, false, false));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("geographicalArea", Set.of("geographicalArea.required"))
        );
    assertThat(errorMessages)
        .containsExactly(
            entry("geographicalArea", Set.of("Select a geographical area"))
        );
  }

  @Test
  void validate_whenInValidFormMultipleUploadsWhenRelinquishment_assertErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    form.setRelinquishmentReport(List.of(new UploadedFileForm(), new UploadedFileForm()));
    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(true, false, false, false));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("relinquishmentReport", Set.of("relinquishmentReport.limitExceeded"))
            );
    assertThat(errorMessages)
        .containsExactly(
            entry("relinquishmentReport", Set.of("Upload at most one relinquishment report"))
        );
  }

  @Test
  void validate_whenInvalidFormNoFileDescriptionWhenRelinquishment_assertErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    form.setRelinquishmentReport(List.of(new UploadedFileForm()));
    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(true, false, false, false));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("relinquishmentReport[0].uploadedFileDescription",
                Set.of("relinquishmentReport[0].uploadedFileDescription.required"))
        );
    assertThat(errorMessages)
        .containsExactly(
            entry("relinquishmentReport[0].uploadedFileDescription",
                Set.of("Enter a description of this file"))
        );
  }

  @Test
  void validate_whenInValidForm_notRelinquishmentResponsibleTeamUpdateNoResponsibleTeam_assertErrors() {
    var form = getValidForm();
    form.setResponsibleTeamType(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false, true, false, false));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("responsibleTeamType", Set.of("responsibleTeamType.required"))
        );
    assertThat(errorMessages)
        .containsExactly(
            entry("responsibleTeamType", Set.of("Select a responsible team"))
        );
  }

  @Test
  void validate_whenValidForm_whenIsAreaTeam_notLinkedToShelvedCopField_assertNoErrors() {
    var form = getValidForm();
    form.setLinkedToShelvedCopField(false);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false, false, false, true));

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenValidForm_linkedToShelvedCopField_fieldProvided_assertNoErrors() {
    var form = getValidForm();
    form.setLinkedToShelvedCopField(true);
    form.setFieldSelectedOption("fieldSelectedOption");
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false, false, true, true));

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenInValidForm_whenIsAreaTeam_linkedToShelvedCopFieldNotSelected_assertErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false, false, false, true));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("linkedToShelvedCopField", Set.of("linkedToShelvedCopField.required"))
        );
    assertThat(errorMessages)
        .containsExactly(
            entry("linkedToShelvedCopField", Set.of("Select if this discovery is linked to a shelved or ceased production field"))
        );
  }

  @Test
  void validate_whenInValidForm_whenIsAreaTeam_linkedToShelvedCopField_noFieldProvided_assertErrors() {
    var form = getValidForm();
    form.setLinkedToShelvedCopField(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(false, false, true, true));

    assertThat(bindingResult.hasErrors()).isTrue();
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldSelectedOption", Set.of("fieldSelectedOption.required"))
        );
    assertThat(errorMessages)
        .containsExactly(
            entry("fieldSelectedOption", Set.of("Provide a field"))
        );
  }

  @Test
  void validate_whenHintsNotProvided_assertThrows() {
    var form = new RegulatorManagedInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThatThrownBy(() -> regulatorManagedInformationFormValidator
        .validate(form, bindingResult))
        .isExactlyInstanceOf(ActionNotAllowedException.class)
        .hasMessage("Must provide validation hints to this validator");
  }

  @Test
  void validate_whenOtherOpportunityTypeProvided() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    regulatorManagedInformationFormValidator.validate(form, bindingResult,
        new RegulatorManagedInformationFormValidatorHints(
            false,
            false,
            false,
            false)
    );
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  private RegulatorManagedInformationForm getValidForm() {
    var form = new RegulatorManagedInformationForm();
    var relinquishmentReportUpload = new UploadedFileForm();
    relinquishmentReportUpload.setUploadedFileDescription("desc");
    relinquishmentReportUpload.setUploadedFileId(UUID.randomUUID());
    relinquishmentReportUpload.setUploadedFileInstant(Instant.now());
    form.setGeographicalArea(GeographicalArea.ONSHORE);
    form.setRelinquishmentReport(List.of(relinquishmentReportUpload));
    form.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    return form;
  }
}