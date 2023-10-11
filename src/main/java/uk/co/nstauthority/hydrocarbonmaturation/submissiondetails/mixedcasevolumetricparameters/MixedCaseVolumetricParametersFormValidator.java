package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class MixedCaseVolumetricParametersFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return MixedCaseVolumetricParametersForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var mixedCaseVolumetricsForm = (MixedCaseVolumetricParametersForm) form;

    var oilFormationVolumeFactor = mixedCaseVolumetricsForm.getOilFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        oilFormationVolumeFactor,
        errors,
        "oilFormationVolumeFactor",
        "Oil formation volume factor distribution used"
    );

    var gasFormationVolumeFactor = mixedCaseVolumetricsForm.getGasFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        gasFormationVolumeFactor,
        errors,
        "gasFormationVolumeFactor",
        "Gas formation volume factor distribution used"
    );

    var oilHydrocarbonSaturation = mixedCaseVolumetricsForm.getOilHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        oilHydrocarbonSaturation,
        errors,
        "oilHydrocarbonSaturation",
        "Oil hydrocarbon saturation distribution used"
    );

    var associatedGasOilRatioLifeOfField = mixedCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validatePartial(
        associatedGasOilRatioLifeOfField,
        errors,
        "associatedGasOilRatioLifeOfField",
        "Associated gas-oil ratio, life-of-field distribution used"
    );

    var oilRecoveryFactor = mixedCaseVolumetricsForm.getOilRecoveryFactor();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        oilRecoveryFactor,
        errors,
        "oilRecoveryFactor",
        "Oil recovery factor distribution used"
    );

    var gasHydrocarbonSaturation = mixedCaseVolumetricsForm.getGasHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        gasHydrocarbonSaturation,
        errors,
        "gasHydrocarbonSaturation",
        "Gas hydrocarbon saturation distribution used"
    );

    var condensateGasRatioLifeOfField = mixedCaseVolumetricsForm.getCondensateGasRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validatePartial(
        condensateGasRatioLifeOfField,
        errors,
        "condensateGasRatioLifeOfField",
        "Condensate-gas ratio, life-of-field distribution used"
    );

    var gasRecoveryFactor = mixedCaseVolumetricsForm.getGasRecoveryFactor();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        gasRecoveryFactor,
        errors,
        "gasRecoveryFactor",
        "Gas recovery factor distribution used"
    );
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var mixedCaseVolumetricsForm = (MixedCaseVolumetricParametersForm) form;

    ValidationUtils.rejectIfEmpty(errors, "oilFormationVolumeFactor.probabilityDistributionType",
        "oilFormationVolumeFactor.probabilityDistributionType.required",
        "Select oil formation volume factor distribution used");

    var oilFormationVolumeFactor = mixedCaseVolumetricsForm.getOilFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        oilFormationVolumeFactor,
        errors,
        "oilFormationVolumeFactor",
        "Oil formation volume factor distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "oilHydrocarbonSaturation.probabilityDistributionType",
        "oilHydrocarbonSaturation.probabilityDistributionType.required",
        "Select oil hydrocarbon saturation distribution used");

    var oilHydrocarbonSaturation = mixedCaseVolumetricsForm.getOilHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        oilHydrocarbonSaturation,
        errors,
        "oilHydrocarbonSaturation",
        "Oil hydrocarbon saturation distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "associatedGasOilRatioLifeOfField.probabilityDistributionType",
        "associatedGasOilRatioLifeOfField.probabilityDistributionType.required",
        "Select associated gas-oil ratio, life-of-field distribution used");

    var associatedGasOilRatioLifeOfField = mixedCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validateFull(
        associatedGasOilRatioLifeOfField,
        errors,
        "associatedGasOilRatioLifeOfField",
        "Associated gas-oil ratio, life-of-field distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "oilRecoveryFactor.probabilityDistributionType",
        "oilRecoveryFactor.probabilityDistributionType.required",
        "Select oil recovery factor distribution used");

    var oilRecoveryFactor = mixedCaseVolumetricsForm.getOilRecoveryFactor();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        oilRecoveryFactor,
        errors,
        "oilRecoveryFactor",
        "Oil recovery factor distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasFormationVolumeFactor.probabilityDistributionType",
        "gasFormationVolumeFactor.probabilityDistributionType.required",
        "Select gas formation volume factor distribution used");

    var gasFormationVolumeFactor = mixedCaseVolumetricsForm.getGasFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        gasFormationVolumeFactor,
        errors,
        "gasFormationVolumeFactor",
        "Gas formation volume factor distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasHydrocarbonSaturation.probabilityDistributionType",
        "gasHydrocarbonSaturation.probabilityDistributionType.required",
        "Select gas hydrocarbon saturation distribution used");

    var gasHydrocarbonSaturation = mixedCaseVolumetricsForm.getGasHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        gasHydrocarbonSaturation,
        errors,
        "gasHydrocarbonSaturation",
        "Gas hydrocarbon saturation distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "condensateGasRatioLifeOfField.probabilityDistributionType",
        "condensateGasRatioLifeOfField.probabilityDistributionType.required",
        "Select condensate-gas ratio, life-of-field distribution used");

    var condensateGasRatioLifeOfField = mixedCaseVolumetricsForm.getCondensateGasRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validateFull(
        condensateGasRatioLifeOfField,
        errors,
        "condensateGasRatioLifeOfField",
        "Condensate-gas ratio, life-of-field distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasRecoveryFactor.probabilityDistributionType",
        "gasRecoveryFactor.probabilityDistributionType.required",
        "Select gas recovery factor distribution used");

    var gasRecoveryFactor = mixedCaseVolumetricsForm.getGasRecoveryFactor();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        gasRecoveryFactor,
        errors,
        "gasRecoveryFactor",
        "Gas recovery factor distribution used"
    );
  }
}
