package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilCaseVolumetricsFormValidator implements SubmissionValidator {

  @Override
  public boolean isValid(SubmissionForm submissionForm,
                         Errors errors,
                         SubmissionDetail submissionDetail,
                         ValidationType validationType) {
    switch (validationType) {
      case FULL -> validateFull(submissionForm, errors);
      case PARTIAL -> validatePartial(submissionForm, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }

    return !errors.hasErrors();
  }

  public void validatePartial(SubmissionForm form, Errors errors) {
    var oilCaseVolumetricsForm = (OilCaseVolumetricsForm) form;

    var oilFormationVolumeFactor = oilCaseVolumetricsForm.getOilFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validatePartial(
        oilFormationVolumeFactor,
        errors,
        "oilFormationVolumeFactor",
        "Oil formation volume factor (Bo)"
    );

    var oilHydrocarbonSaturation = oilCaseVolumetricsForm.getOilHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        oilHydrocarbonSaturation,
        errors,
        "oilHydrocarbonSaturation",
        "Oil hydrocarbon saturation distribution used"
    );

    var associatedGasOilRatioLifeOfField = oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validatePartial(
        associatedGasOilRatioLifeOfField,
        errors,
        "associatedGasOilRatioLifeOfField",
        "Associated gas-oil ratio, life-of-field distribution used"
    );

    var oilRecoveryFactor = oilCaseVolumetricsForm.getOilRecoveryFactor();
    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        oilRecoveryFactor,
        errors,
        "oilRecoveryFactor",
        "Oil recovery factor distribution used"
    );
  }

  public void validateFull(SubmissionForm form, Errors errors) {
    var oilCaseVolumetricsForm = (OilCaseVolumetricsForm) form;

    ValidationUtils.rejectIfEmpty(errors, "oilFormationVolumeFactor.probabilityDistributionType",
        "oilFormationVolumeFactor.probabilityDistributionType.required",
        "Select oil formation volume factor (Bo)");

    var oilFormationVolumeFactor = oilCaseVolumetricsForm.getOilFormationVolumeFactor();
    ProbabilityDistributionValidationUtil.validateFull(
        oilFormationVolumeFactor,
        errors,
        "oilFormationVolumeFactor",
        "Oil formation volume factor distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "oilHydrocarbonSaturation.probabilityDistributionType",
        "oilHydrocarbonSaturation.probabilityDistributionType.required",
        "Select oil hydrocarbon saturation distribution used");

    var oilHydrocarbonSaturation = oilCaseVolumetricsForm.getOilHydrocarbonSaturation();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        oilHydrocarbonSaturation,
        errors,
        "oilHydrocarbonSaturation",
        "Oil hydrocarbon saturation distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "associatedGasOilRatioLifeOfField.probabilityDistributionType",
        "associatedGasOilRatioLifeOfField.probabilityDistributionType.required",
        "Select associated gas-oil ratio, life-of-field distribution used");

    var associatedGasOilRatioLifeOfField = oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField();
    ProbabilityDistributionValidationUtil.validateFull(
        associatedGasOilRatioLifeOfField,
        errors,
        "associatedGasOilRatioLifeOfField",
        "Associated gas-oil ratio, life-of-field distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "oilRecoveryFactor.probabilityDistributionType",
        "oilRecoveryFactor.probabilityDistributionType.required",
        "Select oil recovery factor distribution used");

    var gasRecoveryFactor = oilCaseVolumetricsForm.getOilRecoveryFactor();
    ProbabilityDistributionValidationUtil.validateFullPercentage(
        gasRecoveryFactor,
        errors,
        "oilRecoveryFactor",
        "Oil recovery factor distribution used"
    );
  }
}
