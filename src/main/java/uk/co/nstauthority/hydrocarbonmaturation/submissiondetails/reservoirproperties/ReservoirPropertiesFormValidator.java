package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;

@Service
class ReservoirPropertiesFormValidator implements SmartValidator, SubmissionFormValidator {

  private SubmissionType submissionType;

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ReservoirPropertiesForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var validationType = SubmissionFormValidationUtils.extractValidationTypeFromHints(validationHints);
    submissionType = SubmissionFormValidationUtils.extractSubmissionTypeFromHints(validationHints);
    var form = (ReservoirPropertiesForm) target;

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var reservoirForm = ((ReservoirPropertiesForm) form);

    ProbabilityDistributionValidationUtil.validatePartial(
        reservoirForm.getAverageNetPayThickness(),
        errors,
        "averageNetPayThickness",
        "Average net pay thickness distribution used"
    );

    ProbabilityDistributionValidationUtil.validatePartial(
        reservoirForm.getGrossReservoirThickness(),
        errors,
        "grossReservoirThickness",
        "Gross reservoir thickness distribution used"
    );

    ProbabilityDistributionValidationUtil.validatePartial(
        reservoirForm.getGrossRockVolume(),
        errors,
        "grossRockVolume",
        "Gross rock volume (GRV) distribution used"
    );

    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        reservoirForm.getPercentOfGrvOilBearing(),
        errors,
        "percentOfGrvOilBearing",
        "Percentage of GRV which is oil bearing distribution used"
    );

    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        reservoirForm.getNetToGross(),
        errors,
        "netToGross",
        "Net to gross distribution used"
    );

    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        reservoirForm.getNetPorosity(),
        errors,
        "netPorosity",
        "Net porosity distribution used"
    );

    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE) {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getDepthToHydrocarbonContact(),
          errors,
          "depthToHydrocarbonContact",
          "Depth to hydrocarbon contact distribution used"
      );
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "formationName", "formationName.required", "Enter a formation name");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ageStageOrSeries", "ageStageOrSeries.required",
        "Enter an age stage or series");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lithology", "lithology.required", "Enter a lithology");

    var reservoirForm = ((ReservoirPropertiesForm) form);

    ValidationUtils.rejectIfEmpty(errors, "averageNetPayThickness.probabilityDistributionType",
        "averageNetPayThickness.probabilityDistributionType.required",
        "Select the average net pay thickness distribution used");

    ProbabilityDistributionValidationUtil.validateFull(
        reservoirForm.getAverageNetPayThickness(),
        errors,
        "averageNetPayThickness",
        "Average net pay thickness distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "grossReservoirThickness.probabilityDistributionType",
        "grossReservoirThickness.probabilityDistributionType.required",
        "Select the gross reservoir thickness distribution used");

    ProbabilityDistributionValidationUtil.validateFull(
        reservoirForm.getGrossReservoirThickness(),
        errors,
        "grossReservoirThickness",
        "Gross reservoir thickness distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "grossRockVolume.probabilityDistributionType",
        "grossRockVolume.probabilityDistributionType.required",
        "Select the gross rock volume distribution used");

    ProbabilityDistributionValidationUtil.validateFull(
        reservoirForm.getGrossRockVolume(),
        errors,
        "grossRockVolume",
        "Gross rock volume (GRV) distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "percentOfGrvOilBearing.probabilityDistributionType",
        "percentOfGrvOilBearing.probabilityDistributionType.required",
        "Select the percentage of GRV which is oil bearing distribution used");

    ProbabilityDistributionValidationUtil.validateFullPercentage(
        reservoirForm.getPercentOfGrvOilBearing(),
        errors,
        "percentOfGrvOilBearing",
        "Percentage of GRV which is oil bearing distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "netToGross.probabilityDistributionType",
        "netToGross.probabilityDistributionType.required",
        "Select net-to-gross distribution used");

    ProbabilityDistributionValidationUtil.validateFullPercentage(
        reservoirForm.getNetToGross(),
        errors,
        "netToGross",
        "Net to gross distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "netPorosity.probabilityDistributionType",
        "netPorosity.probabilityDistributionType.required",
        "Select the net porosity distribution used");

    ProbabilityDistributionValidationUtil.validateFullPercentage(
        reservoirForm.getNetPorosity(),
        errors,
        "netPorosity",
        "Net porosity distribution used"
    );

    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE) {
      ValidationUtils.rejectIfEmpty(errors, "depthToHydrocarbonContact.probabilityDistributionType",
          "depthToHydrocarbonContact.probabilityDistributionType.required",
          "Select the depth to hydrocarbon contact distribution used");

      ProbabilityDistributionValidationUtil.validateFull(
          reservoirForm.getDepthToHydrocarbonContact(),
          errors,
          "depthToHydrocarbonContact",
          "Depth to hydrocarbon contact distribution used"
      );
    }
  }
}
