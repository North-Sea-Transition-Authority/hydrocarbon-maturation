package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class MixedCaseOutputsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return MixedCaseOutputsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var mixedCaseOutputsForm = (MixedCaseOutputsForm) form;

    var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE
    );

    var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilRecoverableResources,
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES
    );

    if (hasAssociatedGasRecoverableResources(mixedCaseOutputsForm)) {
      var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          associatedGasRecoverableResources,
          errors,
          "associatedGasRecoverableResources",
          "Associated gas recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES
      );
    }

    var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas in place",
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE
    );

    var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasRecoverableResources,
        errors,
        "gasRecoverableResources",
        "Gas recoverable resources",
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES
    );

    var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedCondensateRecoverableResources,
        errors,
        "associatedCondensateRecoverableResources",
        "Associated condensate recoverable resources",
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES
    );

    var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES
    );
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var mixedCaseOutputsForm = (MixedCaseOutputsForm) form;

    var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE
    );

    var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilRecoverableResources,
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES
    );

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "hasAssociatedGasRecoverableResources",
        "hasAssociatedGasRecoverableResources.required",
        "Select one of the options"
    );

    if (hasAssociatedGasRecoverableResources(mixedCaseOutputsForm)) {
      var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          associatedGasRecoverableResources,
          errors,
          "associatedGasRecoverableResources",
          "Associated gas recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES
      );
    }

    var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas in place",
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE
    );

    var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasRecoverableResources,
        errors,
        "gasRecoverableResources",
        "Gas recoverable resources",
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES
    );

    var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedCondensateRecoverableResources,
        errors,
        "associatedCondensateRecoverableResources",
        "Associated condensate recoverable resources",
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES
    );

    var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES
    );

    DecimalInputValidator.configureValidator(errors, "riskedOilEquivalentRecoverableMean.decimalInputValue",
            "The risked oil equivalent recoverable mean", mixedCaseOutputsForm.getRiskedOilEquivalentRecoverableMean())
        .setMaxDecimalPlaces(0)
        .validate();
  }

  private boolean hasAssociatedGasRecoverableResources(MixedCaseOutputsForm form) {
    return form.getHasAssociatedGasRecoverableResources() != null
        && form.getHasAssociatedGasRecoverableResources();
  }
}