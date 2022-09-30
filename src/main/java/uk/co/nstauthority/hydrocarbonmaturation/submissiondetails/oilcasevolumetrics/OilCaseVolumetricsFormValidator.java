package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
public class OilCaseVolumetricsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return OilCaseVolumetricsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var oilCaseVolumetricsForm = (OilCaseVolumetricsForm) form;

    var formationVolumeFactor = oilCaseVolumetricsForm.getFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        formationVolumeFactor,
        errors,
        "formationVolumeFactor",
        "Formation volume factor");

    var oilHydrocarbonSaturation = oilCaseVolumetricsForm.getOilHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validatePartial(
        oilHydrocarbonSaturation,
        errors,
        "oilHydrocarbonSaturation",
        "Oil hydrocarbon saturation"
    );

    var associatedGasOilRatioLifeOfField = oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validatePartial(
        associatedGasOilRatioLifeOfField,
        errors,
        "associatedGasOilRatioLifeOfField",
        "Associated-gas ratio, life-of-field"
    );

    var oilRecoveryFactor = oilCaseVolumetricsForm.getOilRecoveryFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        oilRecoveryFactor,
        errors,
        "oilRecoveryFactor",
        "Oil recovery factor");
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var oilCaseVolumetricsForm = (OilCaseVolumetricsForm) form;

    ValidationUtils.rejectIfEmpty(errors, "formationVolumeFactor.probabilityDistributionType",
        "formationVolumeFactor.probabilityDistributionType.required",
        "Select formation volume factor");

    var formationVolumeFactor = oilCaseVolumetricsForm.getFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        formationVolumeFactor,
        errors,
        "formationVolumeFactor",
        "Formation volume factor");

    ValidationUtils.rejectIfEmpty(errors, "oilHydrocarbonSaturation.probabilityDistributionType",
        "oilHydrocarbonSaturation.probabilityDistributionType.required",
        "Select oil hydrocarbon saturation");

    var oilHydrocarbonSaturation = oilCaseVolumetricsForm.getOilHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validateFull(
        oilHydrocarbonSaturation,
        errors,
        "oilHydrocarbonSaturation",
        "Gas hydrocarbon saturation"
    );

    ValidationUtils.rejectIfEmpty(errors, "associatedGasOilRatioLifeOfField.probabilityDistributionType",
        "associatedGasOilRatioLifeOfField.probabilityDistributionType.required",
        "Select associated gas-oil ratio, life-of-field");

    var associatedGasOilRatioLifeOfField = oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validateFull(
        associatedGasOilRatioLifeOfField,
        errors,
        "associatedGasOilRatioLifeOfField",
        "Associated gas-oil ratio, life-of-field"
    );

    ValidationUtils.rejectIfEmpty(errors, "oilRecoveryFactor.probabilityDistributionType",
        "oilRecoveryFactor.probabilityDistributionType.required",
        "Select oil recovery factor");

    var gasRecoveryFactor = oilCaseVolumetricsForm.getOilRecoveryFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        gasRecoveryFactor,
        errors,
        "oilRecoveryFactor",
        "Oil recovery factor");
  }
}
