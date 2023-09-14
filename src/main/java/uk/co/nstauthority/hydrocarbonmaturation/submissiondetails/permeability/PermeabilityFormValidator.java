package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import java.math.BigDecimal;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
class PermeabilityFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var permeabilityForm = (PermeabilityForm) form;

    var submissionType = submissionDetail.getSubmissionType();
    if (submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT
        || BooleanUtils.isTrue(permeabilityForm.getHasPermeability())) {
      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.permeabilityMin.decimalInputValue",
              "Permeability (md) min",
              permeabilityForm.getPermeabilityValuesForm().getPermeabilityMin())
          .setMaxDecimalPlaces(2)
          .setMinValue(BigDecimal.ZERO)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.permeabilityMax.decimalInputValue",
              "Permeability (md) max",
              permeabilityForm.getPermeabilityValuesForm().getPermeabilityMax())
          .setMaxDecimalPlaces(2)
          .setMinValue(BigDecimal.ZERO)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.permeabilityAverage.decimalInputValue",
              "Permeability (md) average",
              permeabilityForm.getPermeabilityValuesForm().getPermeabilityAverage())
          .setMaxDecimalPlaces(2)
          .setMinValue(BigDecimal.ZERO)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.kvKhAverage.decimalInputValue",
              "Kv/kh average",
              permeabilityForm.getPermeabilityValuesForm().getKvKhAverage())
          .setMaxDecimalPlaces(2)
          .setMinValue(BigDecimal.ZERO)
          .validate();
    }

    if (submissionType == SubmissionType.CREATE_CSR
        || submissionType == SubmissionType.CREATE_FDP
        || submissionType == SubmissionType.CREATE_FDPA
        || submissionType == SubmissionType.CREATE_COP) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          permeabilityForm.getPermeabilityValuesForm().getPermeability(),
          errors,
          "permeabilityValuesForm.permeability",
          "Permeability",
          ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY
      );

      ProbabilityDistributionValidationUtil.validateCustomPartial(
          permeabilityForm.getPermeabilityValuesForm().getHorizontalPermeability(),
          errors,
          "permeabilityValuesForm.horizontalPermeability",
          "Horizontal permeability",
          ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY
      );

      ProbabilityDistributionValidationUtil.validateCustomPartial(
          permeabilityForm.getPermeabilityValuesForm().getVerticalPermeability(),
          errors,
          "permeabilityValuesForm.verticalPermeability",
          "Vertical permeability",
          ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY
      );
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var permeabilityForm = (PermeabilityForm) form;
    var submissionType = submissionDetail.getSubmissionType();

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "hasPermeability",
          "hasPermeability.required",
          "Select whether you have permeability details");
      if (BooleanUtils.isTrue(permeabilityForm.getHasPermeability())) {
        validatePermeability(permeabilityForm, errors, submissionType);
      }
    } else {
      validatePermeability(permeabilityForm, errors, submissionType);
    }
  }

  private void validatePermeability(PermeabilityForm permeabilityForm, Errors errors, SubmissionType submissionType) {
    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.permeabilityMin.decimalInputValue",
            "Permeability (md) min",
            permeabilityForm.getPermeabilityValuesForm().getPermeabilityMin())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.permeabilityMax.decimalInputValue",
            "Permeability (md) max",
            permeabilityForm.getPermeabilityValuesForm().getPermeabilityMax())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.permeabilityAverage.decimalInputValue",
            "Permeability (md) average",
            permeabilityForm.getPermeabilityValuesForm().getPermeabilityAverage())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    ValidationUtils.rejectIfEmpty(errors,
        "permeabilityValuesForm.averageMethodUsed",
        "permeabilityValuesForm.averageMethodUsed.required",
        "Enter permeability (md) average method used");

    ValidationUtils.rejectIfEmpty(errors,
        "permeabilityValuesForm.permeabilitySource",
        "permeabilityValuesForm.permeabilitySource.required",
        "Enter Permeability (md) Source");

    if (permeabilityForm.getPermeabilityValuesForm().getPermeabilitySource() == PermeabilitySource.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "permeabilityValuesForm.otherPermeabilitySource",
          "permeabilityValuesForm.otherPermeabilitySource.required",
          "Enter other permeability (md) source"
      );
    }

    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.kvKhAverage.decimalInputValue",
            "Kv/kh average",
            permeabilityForm.getPermeabilityValuesForm().getKvKhAverage())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    if (submissionType == SubmissionType.CREATE_CSR
        || submissionType == SubmissionType.CREATE_FDP
        || submissionType == SubmissionType.CREATE_FDPA
        || submissionType == SubmissionType.CREATE_COP) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          permeabilityForm.getPermeabilityValuesForm().getPermeability(),
          errors,
          "permeabilityValuesForm.permeability",
          "Permeability",
          ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY
      );

      ProbabilityDistributionValidationUtil.validateCustomFull(
          permeabilityForm.getPermeabilityValuesForm().getHorizontalPermeability(),
          errors,
          "permeabilityValuesForm.horizontalPermeability",
          "Horizontal permeability",
          ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY
      );

      ProbabilityDistributionValidationUtil.validateCustomFull(
          permeabilityForm.getPermeabilityValuesForm().getVerticalPermeability(),
          errors,
          "permeabilityValuesForm.verticalPermeability",
          "Vertical permeability",
          ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY
      );
    }
  }
}
