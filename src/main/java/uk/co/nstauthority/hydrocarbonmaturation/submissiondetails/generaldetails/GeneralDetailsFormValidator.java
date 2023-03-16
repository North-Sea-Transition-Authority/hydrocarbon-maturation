package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.math.BigDecimal;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class GeneralDetailsFormValidator implements SmartValidator, SubmissionFormValidator {

  private static final String LICENCE_SELECT = "licenceSelect";

  private final LicenceQueryService licenceQueryService;
  private SubmissionType submissionType;
  private boolean isLicencePresent;
  private boolean isRelinquishment;
  private GeneralDetail latestApprovedGeneralDetail;
  private boolean missingLicenceErrorPresent;

  @Autowired
  GeneralDetailsFormValidator(LicenceQueryService licenceQueryService) {
    this.licenceQueryService = licenceQueryService;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return GeneralDetailsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Must provide GeneralDetailsFormValidatorHints");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var validatorHints = (GeneralDetailsFormValidatorHints) SubmissionFormValidationUtils
        .extractValidatorHints(validationHints);
    var validationType = validatorHints.getValidationType();
    var submissionDetail = validatorHints.getSubmissionDetail();
    submissionType = submissionDetail.getSubmissionType();
    isRelinquishment = submissionDetail.isRelinquishment();
    isLicencePresent = validatorHints.getLicencePresent();
    var form = (GeneralDetailsForm) target;

    latestApprovedGeneralDetail = validatorHints.getLatestApprovedGeneralDetail();
    missingLicenceErrorPresent = false;
    validateAlways(form, errors);

    if (validationType.equals(ValidationType.PARTIAL)) {
      validatePartial(form, errors);
    }

    if (validationType.equals(ValidationType.FULL)) {
      validateFull(form, errors);
    }

  }

  private void validateAlways(GeneralDetailsForm form, Errors errors) {
    if (latestApprovedGeneralDetail != null
        && BooleanUtils.isTrue(latestApprovedGeneralDetail.getResourceOnALicence())
        && !isRelinquishment) {
      if (form.getResourceOnALicence() == null || !form.getResourceOnALicence()) {
        errors.rejectValue("resourceOnALicence",
            "resourceOnALicence.required",
            "You cannot remove all licences from a resource that has " +
                "a licence on an earlier approved submission");
        missingLicenceErrorPresent = true;
      } else if (isLicencePresent && CollectionUtils.isEmpty(form.getLicences())) {
        errors.rejectValue(LICENCE_SELECT, LICENCE_SELECT + ".required",
            "You cannot remove all licences from a resource that has " +
                "a licence on an earlier approved submission");
        missingLicenceErrorPresent = true;
      }
    }
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    GeneralDetailsForm generalDetailsForm = (GeneralDetailsForm) form;

    if (!isRelinquishment
        && !generalDetailsForm.getLicences().isEmpty()
        && BooleanUtils.isTrue(generalDetailsForm.getResourceOnALicence())) {
      validateExtantLicences(generalDetailsForm, errors);
    }

    partialValidationPercentageLocatedInUkcs(generalDetailsForm, errors);

    if (isRelinquishment) {
      validateDateOfRelinquishment(ValidationType.PARTIAL, generalDetailsForm, errors);
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var generalDetailsForm = (GeneralDetailsForm) form;

    if (!isRelinquishment && !missingLicenceErrorPresent) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resourceOnALicence",
          "resourceOnALicence.required", "Select whether the resource is on a licence");

      if (BooleanUtils.isTrue(generalDetailsForm.getResourceOnALicence())
          && CollectionUtils.isEmpty(generalDetailsForm.getLicences())) {
        errors.rejectValue(LICENCE_SELECT, LICENCE_SELECT + ".required",
            "You must select at least one licence");
      } else {
        validateExtantLicences(generalDetailsForm, errors);
      }
    }

    ValidationUtils.rejectIfEmpty(errors, "resourceLocatedInUkcs", "resourceLocatedInUkcs.required",
        "Select whether the resource is wholly located within UKCS");

    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY) {
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

    if (CollectionUtils.isEmpty(generalDetailsForm.getQuadrantBlocks())) {
      errors.rejectValue("quadrantBlockSelect", "quadrantBlockSelect.notEmpty",
          "You must select at least one quadrant and block");
    }

    if (isRelinquishment) {
      validateDateOfRelinquishment(ValidationType.FULL, generalDetailsForm, errors);
    }
  }

  private void validateDateOfRelinquishment(ValidationType validationType,
                                            GeneralDetailsForm form,
                                            Errors errors) {
    if (validationType == ValidationType.FULL) {
      DateValidationUtil.validateDate(
          "relinquishment",
          "date of relinquishment",
          form.getRelinquishmentDay(),
          form.getRelinquishmentMonth(),
          form.getRelinquishmentYear(),
          errors);
    } else if (validationType == ValidationType.PARTIAL
        && !StringUtils.isAllBlank(
            form.getRelinquishmentYear(), form.getRelinquishmentMonth(), form.getRelinquishmentDay())) {
      DateValidationUtil.validateDate(
          "relinquishment",
          "date of relinquishment",
          form.getRelinquishmentDay(),
          form.getRelinquishmentMonth(),
          form.getRelinquishmentYear(),
          errors);
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

  private void validateExtantLicences(GeneralDetailsForm form, Errors errors) {
    var licences = licenceQueryService.getLicencesByIds(form.getLicences());

    var invalidLicenceSelected = licences
        .stream()
        .anyMatch(licence -> !LicenceQueryService.isLicenceStatusValid(licence.licenceStatus()));

    if (invalidLicenceSelected) {
      errors.rejectValue(LICENCE_SELECT, LICENCE_SELECT + ".notValid", "You must only select extant licences");
    }
  }
}
