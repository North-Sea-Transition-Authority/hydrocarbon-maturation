package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class MixedCaseVolumetricsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return MixedCaseVolumetricsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var mixedCaseVolumetricsForm = (MixedCaseVolumetricsForm) form;

    var formationVolumeFactor = mixedCaseVolumetricsForm.getFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        formationVolumeFactor,
        errors,
        "formationVolumeFactor",
        "Formation volume factor distribution used"
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

    var gasExpansionFactor = mixedCaseVolumetricsForm.getGasExpansionFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        gasExpansionFactor,
        errors,
        "gasExpansionFactor",
        "Gas expansion factor distribution used"
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
    var mixedCaseVolumetricsForm = (MixedCaseVolumetricsForm) form;

    ValidationUtils.rejectIfEmpty(errors, "formationVolumeFactor.probabilityDistributionType",
        "formationVolumeFactor.probabilityDistributionType.required",
        "Select formation volume factor distribution used");
    var formationVolumeFactor = mixedCaseVolumetricsForm.getFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        formationVolumeFactor,
        errors,
        "formationVolumeFactor",
        "Formation volume factor distribution used"
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

    ValidationUtils.rejectIfEmpty(errors, "gasExpansionFactor.probabilityDistributionType",
        "gasExpansionFactor.probabilityDistributionType.required",
        "Select gas expansion factor distribution used");
    var gasExpansionFactor = mixedCaseVolumetricsForm.getGasExpansionFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        gasExpansionFactor,
        errors,
        "gasExpansionFactor",
        "Gas expansion factor distribution used"
    );

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
