package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

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
public class GasCaseVolumetricParametersFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseVolumetricsForm = (GasCaseVolumetricParametersForm) form;

    var gasExpansionFactor = gasCaseVolumetricsForm.getGasExpansionFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        gasExpansionFactor,
        errors,
        "gasExpansionFactor",
        "Gas expansion factor"
    );

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
          "Gas hydrocarbon saturation",
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validatePartialPercentage(
          gasHydrocarbonSaturation,
          errors,
          "gasHydrocarbonSaturation",
          "Gas hydrocarbon saturation"
      );
    }

    var condensateGasRatioLifeOfField = gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validatePartial(
        condensateGasRatioLifeOfField,
        errors,
        "condensateGasRatioLifeOfField",
        "Condensate-gas ratio, life-of-field"
    );

    var gasRecoveryFactor = gasCaseVolumetricsForm.getGasRecoveryFactor();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        gasRecoveryFactor,
        errors,
        "gasRecoveryFactor",
        "Gas recovery factor");
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseVolumetricsForm = (GasCaseVolumetricParametersForm) form;

    ValidationUtils.rejectIfEmpty(errors, "gasExpansionFactor.probabilityDistributionType",
        "gasExpansionFactor.probabilityDistributionType.required",
        "Select gas expansion factor distribution used");

    var gasExpansionFactor = gasCaseVolumetricsForm.getGasExpansionFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        gasExpansionFactor,
        errors,
        "gasExpansionFactor",
        "Gas expansion factor"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasFormationVolumeFactor.probabilityDistributionType",
        "gasFormationVolumeFactor.probabilityDistributionType.required",
        "Select gas formation volume factor (Bg) distribution used");

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
          "Gas hydrocarbon saturation",
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validateFullPercentage(
          gasHydrocarbonSaturation,
          errors,
          "gasHydrocarbonSaturation",
          "Gas hydrocarbon saturation"
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
        "Condensate-gas ratio, life-of-field"
    );

    ValidationUtils.rejectIfEmpty(errors, "gasRecoveryFactor.probabilityDistributionType",
        "gasRecoveryFactor.probabilityDistributionType.required",
        "Select gas recovery factor distribution used");

    var gasRecoveryFactor = gasCaseVolumetricsForm.getGasRecoveryFactor();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        gasRecoveryFactor,
        errors,
        "gasRecoveryFactor",
        "Gas recovery factor");
  }
}
