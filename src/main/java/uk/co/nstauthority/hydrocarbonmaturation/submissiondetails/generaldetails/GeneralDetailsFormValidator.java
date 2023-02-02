package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.math.BigDecimal;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
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
  private Optional<GeneralDetail> latestApprovedGeneralDetail;

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
    submissionType = validatorHints.getSubmissionDetail().getSubmissionType();
    isLicencePresent = validatorHints.getLicencePresent();
    var form = (GeneralDetailsForm) target;

    latestApprovedGeneralDetail = validatorHints.getLatestApprovedGeneralDetail();

    validateAlways(form, errors);

    if (validationType.equals(ValidationType.PARTIAL)) {
      validatePartial(form, errors);
    }

    if (validationType.equals(ValidationType.FULL)) {
      validateFull(form, errors);
    }

  }

  public void validateAlways(GeneralDetailsForm form, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resourceName", "resourceName.required", "Enter the resource name");

    latestApprovedGeneralDetail.ifPresent(generalDetail -> {
      if (generalDetail.getOrganisationId() != null) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "responsibleOrganisationId",
            "responsibleOrganisationId.required",
            "You cannot remove the responsible organisation from a resource that has " +
                "a responsible organisation on an earlier approved submission");
      }

      if (BooleanUtils.isTrue(generalDetail.getResourceOnALicence())
          && submissionType != SubmissionType.RELINQUISHMENT_DISCOVERY
          && submissionType != SubmissionType.RELINQUISHMENT_PROSPECT) {
        if (form.getResourceOnALicence() == null || !form.getResourceOnALicence()) {
          errors.rejectValue("resourceOnALicence",
              "resourceOnALicence.required",
              "You cannot remove all licences from a resource that has " +
                  "a licence on an earlier approved submission");
        } else if (isLicencePresent && CollectionUtils.isEmpty(form.getLicences())) {
          errors.rejectValue(LICENCE_SELECT, LICENCE_SELECT + ".required",
              "You cannot remove all licences from a resource that has " +
                  "a licence on an earlier approved submission");
        }
      }
    });
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    GeneralDetailsForm generalDetailsForm = (GeneralDetailsForm) form;

    if (submissionType != SubmissionType.RELINQUISHMENT_DISCOVERY
        && submissionType != SubmissionType.RELINQUISHMENT_PROSPECT
        && !generalDetailsForm.getLicences().isEmpty()
        && BooleanUtils.isTrue(generalDetailsForm.getResourceOnALicence())) {
      validateExtantLicences(generalDetailsForm, errors);
    }

    partialValidationPercentageLocatedInUkcs(generalDetailsForm, errors);
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var generalDetailsForm = (GeneralDetailsForm) form;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "responsibleOrganisationId", "responsibleOrganisationId.required",
        "Enter the responsible organisation");

    if (submissionType != SubmissionType.RELINQUISHMENT_DISCOVERY
        && submissionType != SubmissionType.RELINQUISHMENT_PROSPECT) {
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

    ValidationUtils.rejectIfEmpty(errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");
    ValidationUtils.rejectIfEmpty(errors, "resourceLocatedInUkcs", "resourceLocatedInUkcs.required",
        "Select whether the resource is wholly located within UKCS");

    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
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
