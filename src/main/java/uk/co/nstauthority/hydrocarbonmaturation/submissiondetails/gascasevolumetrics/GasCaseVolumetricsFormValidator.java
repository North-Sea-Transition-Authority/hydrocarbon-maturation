package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class GasCaseVolumetricsFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseVolumetricsForm = (GasCaseVolumetricsForm) form;

    var gasFormationVolumeFactor = gasCaseVolumetricsForm.getGasFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        gasFormationVolumeFactor,
        errors,
        "gasFormationVolumeFactor",
        "Gas formation volume factor (Bg)"
    );

    var gasHydrocarbonSaturation = gasCaseVolumetricsForm.getGasHydrocarbonSaturation();
    if (submissionDetail.getSubmission().getResource().getType() == ResourceType.FIELD) {
      ProbabilityDistributionValidationUtil.validateCustomPartialPercentage(
          gasHydrocarbonSaturation,
          errors,
          "gasHydrocarbonSaturation",
          "Gas hydrocarbon saturation distribution used",
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validatePartialPercentage(
          gasHydrocarbonSaturation,
          errors,
          "gasHydrocarbonSaturation",
          "Gas hydrocarbon saturation distribution used"
      );
    }

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
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseVolumetricsForm = (GasCaseVolumetricsForm) form;

    ValidationUtils.rejectIfEmpty(errors, "gasFormationVolumeFactor.probabilityDistributionType",
        "gasFormationVolumeFactor.probabilityDistributionType.required",
        "Select gas formation volume factor (Bg)");

    var gasFormationVolumeFactor = gasCaseVolumetricsForm.getGasFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        gasFormationVolumeFactor,
        errors,
        "gasFormationVolumeFactor",
        "Gas formation volume factor (Bg)"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasHydrocarbonSaturation.probabilityDistributionType",
        "gasHydrocarbonSaturation.probabilityDistributionType.required",
        "Select gas hydrocarbon saturation distribution used");

    var gasHydrocarbonSaturation = gasCaseVolumetricsForm.getGasHydrocarbonSaturation();
    if (submissionDetail.getSubmission().getResource().getType() == ResourceType.FIELD) {
      ProbabilityDistributionValidationUtil.validateCustomFullPercentage(
          gasHydrocarbonSaturation,
          errors,
          "gasHydrocarbonSaturation",
          "Gas hydrocarbon saturation distribution used",
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validateFullPercentage(
          gasHydrocarbonSaturation,
          errors,
          "gasHydrocarbonSaturation",
          "Gas hydrocarbon saturation distribution used"
      );
    }

    ValidationUtils.rejectIfEmpty(errors, "condensateGasRatioLifeOfField.probabilityDistributionType",
        "condensateGasRatioLifeOfField.probabilityDistributionType.required",
        "Select condensate-gas ratio, life-of-field distribution used");

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
