package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class FieldTrapPropertiesFormValidator implements SubmissionValidator {
  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var fieldTrapPropertiesForm = (FieldTrapPropertiesForm) form;

    validateOptionalProbabilityDistributions(fieldTrapPropertiesForm, errors);

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "verticallySeparateHcPools.decimalInputValue",
            "The number of vertically separate HC pools",
            fieldTrapPropertiesForm.getVerticallySeparateHcPools())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "structuralCompartments.decimalInputValue",
            "The number of structural compartments",
            fieldTrapPropertiesForm.getStructuralCompartments())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "originalHydrocarbonColumnHeight.decimalInputValue",
            "The original hydrocarbon column height",
            fieldTrapPropertiesForm.getOriginalHydrocarbonColumnHeight())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var fieldTrapPropertiesForm = (FieldTrapPropertiesForm) form;

    validateOptionalProbabilityDistributions(fieldTrapPropertiesForm, errors);

    var trapType = fieldTrapPropertiesForm.getTrapType();
    if (trapType == null) {
      errors.rejectValue(
          "trapType",
          "trapType.required",
          "Select the trap type"
      );
    } else if (trapType.equals(TrapType.OTHER)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "otherTrapType",
          "otherTrapType.required",
          "Enter the trap type"
      );
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "centreOfInterestQuadrantBlock",
        "centreOfInterestQuadrantBlock.required",
        "Enter the quadrant and block the centre of interest is within"
    );

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "closureMechanisms",
        "closureMechanisms.required",
        "Enter the closure mechanisms"
    );

    DecimalInputValidator.configureValidator(
            errors,
            "verticallySeparateHcPools.decimalInputValue",
            "The number of vertically separate HC pools",
            fieldTrapPropertiesForm.getVerticallySeparateHcPools())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "structuralCompartments.decimalInputValue",
            "The number of structural compartments",
            fieldTrapPropertiesForm.getStructuralCompartments())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "originalHydrocarbonColumnHeight.decimalInputValue",
            "The original hydrocarbon column height",
            fieldTrapPropertiesForm.getOriginalHydrocarbonColumnHeight())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();
  }

  private void validateOptionalProbabilityDistributions(FieldTrapPropertiesForm fieldTrapPropertiesForm, Errors errors) {
    var depthToCrest = fieldTrapPropertiesForm.getDepthToCrest();
    // partial validation on optional fields
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        depthToCrest,
        errors,
        "depthToCrest",
        "Depth to crest",
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD
    );

    var depthToSpillPoint = fieldTrapPropertiesForm.getDepthToSpillPoint();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        depthToSpillPoint,
        errors,
        "depthToSpillPoint",
        "Depth to spill point",
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD
    );

    var productiveArea = fieldTrapPropertiesForm.getProductiveArea();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        productiveArea,
        errors,
        "productiveArea",
        "Productive area",
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD
    );
  }
}
