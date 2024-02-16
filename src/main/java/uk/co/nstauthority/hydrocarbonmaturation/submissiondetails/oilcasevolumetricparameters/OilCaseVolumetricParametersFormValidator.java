package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

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
public class OilCaseVolumetricParametersFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var oilCaseVolumetricsForm = (OilCaseVolumetricParametersForm) form;

    var oilFormationVolumeFactor = oilCaseVolumetricsForm.getOilFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        oilFormationVolumeFactor,
        errors,
        "oilFormationVolumeFactor",
        "Oil formation volume factor (Bo)"
    );

    var oilHydrocarbonSaturation = oilCaseVolumetricsForm.getOilHydrocarbonSaturation();
    if (submissionDetail.getSubmission().getResource().getType() == ResourceType.FIELD) {
      ProbabilityDistributionValidationUtil.validateCustomPartialPercentage(
          oilHydrocarbonSaturation,
          errors,
          "oilHydrocarbonSaturation",
          "Oil hydrocarbon saturation",
          ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validatePartialPercentage(
          oilHydrocarbonSaturation,
          errors,
          "oilHydrocarbonSaturation",
          "Oil hydrocarbon saturation"
      );
    }

    var associatedGasOilRatioLifeOfField = oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validatePartial(
        associatedGasOilRatioLifeOfField,
        errors,
        "associatedGasOilRatioLifeOfField",
        "Associated gas-oil ratio, life-of-field"
    );

    var oilRecoveryFactor = oilCaseVolumetricsForm.getOilRecoveryFactor();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        oilRecoveryFactor,
        errors,
        "oilRecoveryFactor",
        "Oil recovery factor"
    );
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var oilCaseVolumetricsForm = (OilCaseVolumetricParametersForm) form;

    ValidationUtils.rejectIfEmpty(errors, "oilFormationVolumeFactor.probabilityDistributionType",
        "oilFormationVolumeFactor.probabilityDistributionType.required",
        "Select oil formation volume factor (Bo) distribution used");

    var oilFormationVolumeFactor = oilCaseVolumetricsForm.getOilFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        oilFormationVolumeFactor,
        errors,
        "oilFormationVolumeFactor",
        "Oil formation volume factor"
    );

    ValidationUtils.rejectIfEmpty(errors, "oilHydrocarbonSaturation.probabilityDistributionType",
        "oilHydrocarbonSaturation.probabilityDistributionType.required",
        "Select oil hydrocarbon saturation distribution used");

    var oilHydrocarbonSaturation = oilCaseVolumetricsForm.getOilHydrocarbonSaturation();
    if (submissionDetail.getSubmission().getResource().getType() == ResourceType.FIELD) {
      ProbabilityDistributionValidationUtil.validateCustomFullPercentage(
          oilHydrocarbonSaturation,
          errors,
          "oilHydrocarbonSaturation",
          "Oil hydrocarbon saturation",
          ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validateFullPercentage(
          oilHydrocarbonSaturation,
          errors,
          "oilHydrocarbonSaturation",
          "Oil hydrocarbon saturation"
      );
    }

    ValidationUtils.rejectIfEmpty(errors, "associatedGasOilRatioLifeOfField.probabilityDistributionType",
        "associatedGasOilRatioLifeOfField.probabilityDistributionType.required",
        "Select associated gas-oil ratio, life-of-field distribution used");

    var associatedGasOilRatioLifeOfField = oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validateFull(
        associatedGasOilRatioLifeOfField,
        errors,
        "associatedGasOilRatioLifeOfField",
        "Associated gas-oil ratio, life-of-field"
    );

    ValidationUtils.rejectIfEmpty(errors, "oilRecoveryFactor.probabilityDistributionType",
        "oilRecoveryFactor.probabilityDistributionType.required",
        "Select oil recovery factor distribution used");

    var gasRecoveryFactor = oilCaseVolumetricsForm.getOilRecoveryFactor();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        gasRecoveryFactor,
        errors,
        "oilRecoveryFactor",
        "Oil recovery factor"
    );
  }
}
