package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
public class GasCaseVolumetricsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return GasCaseVolumetricsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var gasCaseVolumetricsForm = (GasCaseVolumetricsForm) form;

    var gasHydrocarbonSaturation = gasCaseVolumetricsForm.getGasHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        gasHydrocarbonSaturation,
        errors,
        "gasHydrocarbonSaturation",
        "Gas hydrocarbon saturation distribution used"
    );

    var gasExpansionFactor = gasCaseVolumetricsForm.getGasExpansionFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        gasExpansionFactor,
        errors,
        "gasExpansionFactor",
        "Gas expansion factor distribution used");

    var condensateGasRatioLifeOfField = gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validatePartial(
        condensateGasRatioLifeOfField,
        errors,
        "condensateGasRatioLifeOfField",
        "Condensate-gas ratio, life-of-field distribution used"
    );

    var gasRecoveryFactor = gasCaseVolumetricsForm.getGasRecoveryFactor();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        gasRecoveryFactor,
        errors,
        "gasRecoveryFactor",
        "Gas recovery factor distribution used");
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var gasCaseVolumetricsForm = (GasCaseVolumetricsForm) form;

    ValidationUtils.rejectIfEmpty(errors, "gasHydrocarbonSaturation.probabilityDistributionType",
        "gasHydrocarbonSaturation.probabilityDistributionType.required",
        "Select gas hydrocarbon saturation distribution used");

    var gasHydrocarbonSaturation = gasCaseVolumetricsForm.getGasHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        gasHydrocarbonSaturation,
        errors,
        "gasHydrocarbonSaturation",
        "Gas hydrocarbon saturation distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasExpansionFactor.probabilityDistributionType",
        "gasExpansionFactor.probabilityDistributionType.required",
        "Select gas expansion factor distribution used");

    var gasExpansionFactor = gasCaseVolumetricsForm.getGasExpansionFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        gasExpansionFactor,
        errors,
        "gasExpansionFactor",
        "Gas expansion factor distribution used");

    var condensateGasRatioLifeOfField = gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validateFull(
        condensateGasRatioLifeOfField,
        errors,
        "condensateGasRatioLifeOfField",
        "Condensate-gas ratio, life-of-field distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasRecoveryFactor.probabilityDistributionType",
        "gasRecoveryFactor.probabilityDistributionType.required",
        "Select gas recovery factor distribution used");

    var gasRecoveryFactor = gasCaseVolumetricsForm.getGasRecoveryFactor();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        gasRecoveryFactor,
        errors,
        "gasRecoveryFactor",
        "Gas recovery factor distribution used");
  }
}
