package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.math.BigDecimal;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class GeneralDetailsFormValidator implements SmartValidator, SubmissionFormValidator {

  private SubmissionType submissionType;

  @Override
  public boolean supports(Class<?> clazz) {
    return GeneralDetailsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var validationType = SubmissionFormValidationUtils.extractValidationTypeFromHints(validationHints);
    submissionType = SubmissionFormValidationUtils.extractSubmissionTypeFromHints(validationHints);
    var form = (GeneralDetailsForm) target;

    validateAlways(errors);

    if (validationType.equals(ValidationType.PARTIAL)) {
      validatePartial(form, errors);
    }

    if (validationType.equals(ValidationType.FULL)) {
      validateFull(form, errors);
    }

  }

  public void validateAlways(Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resourceName", "resourceName.required", "Enter the resource name");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    GeneralDetailsForm generalDetailsForm = (GeneralDetailsForm) form;
    partialValidationPercentageLocatedInUkcs(generalDetailsForm, errors);
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var generalDetailsForm = (GeneralDetailsForm) form;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "responsibleOrganisationId", "responsibleOrganisationId.required",
        "Enter the responsible organisation");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resourceOnALicence",
        "resourceOnALicence.required", "Select whether the resource is on a licence");

    if (BooleanUtils.isTrue(generalDetailsForm.getResourceOnALicence())
        && generalDetailsForm.getLicences().isEmpty()) {
      errors.rejectValue("licenceSelect", "licenceSelect.required",
          "You must select at least one licence");
    }

    ValidationUtils.rejectIfEmpty(errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");
    ValidationUtils.rejectIfEmpty(errors, "resourceLocatedInUkcs", "resourceLocatedInUkcs.required",
        "Select whether the resource is wholly located within UKCS");

    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.UPDATE_DISCOVERY) {
      ValidationUtils.rejectIfEmpty(errors, "informationSource", "informationSource.required",
          "Select the source of information");
      if (generalDetailsForm.getInformationSource() == InformationSource.LICENCE_APPLICATION) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "licenceApplication", "licenceApplication.required",
            "Enter the licence application reference");
      }
      if (generalDetailsForm.getInformationSource() == InformationSource.OTHER) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherSourceOfInformation", "otherSourceOfInformation.required",
            "Enter the source of information for this submission");
      }
    }

    fullValidationPercentageLocatedInUkcs(generalDetailsForm, errors);

    if (generalDetailsForm.getQuadrantBlocks().isEmpty()) {
      errors.rejectValue("quadrantBlockSelect", "quadrantBlockSelect.notEmpty",
          "You must select at least one quadrant and block");
    }
  }

  private void fullValidationPercentageLocatedInUkcs(GeneralDetailsForm form, Errors errors) {
    if (BooleanUtils.isFalse(form.getResourceLocatedInUkcs())) {
      configurePercentageLocatedInUkcs(form, errors).validate();
    }
  }

  private void partialValidationPercentageLocatedInUkcs(GeneralDetailsForm form, Errors errors) {
    if (BooleanUtils.isFalse(form.getResourceLocatedInUkcs())) {
      configurePercentageLocatedInUkcs(form, errors).isOptional().validate();
    }
  }

  private DecimalInputValidator configurePercentageLocatedInUkcs(GeneralDetailsForm form, Errors errors) {
    return DecimalInputValidator.configureValidator(errors, "percentageLocatedInUkcs.decimalInputValue",
            "The hydrocarbon volume percentage", form.getPercentageLocatedInUkcs())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.valueOf(0.1))
        .setMaxValue(BigDecimal.valueOf(100));
  }
}
