package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class DiscoveryCombinedCaseVolTotalsFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var mixedCaseOutputsForm = (MixedCaseOutputsForm) form;

    var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );

    var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilRecoverableResources,
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    );

    var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedGasRecoverableResources,
        errors,
        "associatedGasRecoverableResources",
        "Associated gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas in place",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    );

    var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasRecoverableResources,
        errors,
        "gasRecoverableResources",
        "Gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    if (hasAssociatedCondensateRecoverableResources(mixedCaseOutputsForm)) {
      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }

    var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var mixedCaseOutputsForm = (MixedCaseOutputsForm) form;

    var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );

    var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilRecoverableResources,
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    );

    var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedGasRecoverableResources,
        errors,
        "associatedGasRecoverableResources",
        "Associated gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas in place",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    );

    var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasRecoverableResources,
        errors,
        "gasRecoverableResources",
        "Gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "hasAssociatedCondensateRecoverableResources",
        "hasAssociatedCondensateRecoverableResources.required",
        "Select whether you expect to find associated condensate recoverable resources");

    if (hasAssociatedCondensateRecoverableResources(mixedCaseOutputsForm)) {
      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }

    var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );
  }

  private boolean hasAssociatedCondensateRecoverableResources(MixedCaseOutputsForm form) {
    return BooleanUtils.isTrue(form.getHasAssociatedCondensateRecoverableResources());
  }
}
