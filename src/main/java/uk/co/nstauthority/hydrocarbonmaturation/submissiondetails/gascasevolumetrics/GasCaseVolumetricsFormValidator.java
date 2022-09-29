package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasCaseVolumetricsFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return GasCaseVolumetricsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (GasCaseVolumetricsForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var gasCaseVolumetricsForm = (GasCaseVolumetricsForm) form;

    var gasHydrocarbonSaturation = gasCaseVolumetricsForm.getGasHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validatePartial(
        gasHydrocarbonSaturation,
        errors,
        "gasHydrocarbonSaturation",
        "Gas hydrocarbon saturation"
    );

    var gasExpansionFactor = gasCaseVolumetricsForm.getGasExpansionFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        gasExpansionFactor,
        errors,
        "gasExpansionFactor",
        "Gas expansion factor");

    var condensateGasRatioLifeOfField = gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validatePartial(
        condensateGasRatioLifeOfField,
        errors,
        "condensateGasRatioLifeOfField",
        "Condensate-gas ratio, life-of-field"
    );

    var gasRecoveryFactor = gasCaseVolumetricsForm.getGasRecoveryFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        gasRecoveryFactor,
        errors,
        "gasRecoveryFactor",
        "Gas recovery factor");
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var gasCaseVolumetricsForm = (GasCaseVolumetricsForm) form;

    ValidationUtils.rejectIfEmpty(errors, "gasHydrocarbonSaturation.probabilityDistributionType",
        "gasHydrocarbonSaturation.probabilityDistributionType.required",
        "Select gas hydrocarbon saturation");

    var gasHydrocarbonSaturation = gasCaseVolumetricsForm.getGasHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validateFull(
        gasHydrocarbonSaturation,
        errors,
        "gasHydrocarbonSaturation",
        "Gas hydrocarbon saturation"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasExpansionFactor.probabilityDistributionType",
        "gasExpansionFactor.probabilityDistributionType.required",
        "Select gas expansion factor");

    var gasExpansionFactor = gasCaseVolumetricsForm.getGasExpansionFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        gasExpansionFactor,
        errors,
        "gasExpansionFactor",
        "Gas expansion factor");

    ValidationUtils.rejectIfEmpty(errors, "condensateGasRatioLifeOfField.probabilityDistributionType",
        "condensateGasRatioLifeOfField.probabilityDistributionType.required",
        "Select condensate-gas ratio, life-of-field");

    var condensateGasRatioLifeOfField = gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validateFull(
        condensateGasRatioLifeOfField,
        errors,
        "condensateGasRatioLifeOfField",
        "Condensate-gas ratio, life-of-field"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasRecoveryFactor.probabilityDistributionType",
        "gasRecoveryFactor.probabilityDistributionType.required",
        "Select gas recovery factor");

    var gasRecoveryFactor = gasCaseVolumetricsForm.getGasRecoveryFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        gasRecoveryFactor,
        errors,
        "gasRecoveryFactor",
        "Gas recovery factor");
  }
}
