package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class FieldCombinedCaseVolumetricTotalsFormValidator implements SubmissionValidator {
  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var mixedCaseOutputsForm  = (MixedCaseOutputsForm) form;

    var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );

    var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas in place",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );

    validateResourcesPartial(mixedCaseOutputsForm, errors);
    if (!submissionDetail.isConceptSelectReportSubmission()) {
      validateReservesPartial(mixedCaseOutputsForm, errors);
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var mixedCaseOutputsForm  = (MixedCaseOutputsForm) form;

    var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );

    var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas in place",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );

    validateResourcesFull(mixedCaseOutputsForm, errors);
    if (!submissionDetail.isConceptSelectReportSubmission()) {
      validateReservesFull(mixedCaseOutputsForm, errors);
    }
  }

  private void validateResourcesPartial(MixedCaseOutputsForm mixedCaseOutputsForm, Errors errors) {
    var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilRecoverableResources,
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );

    var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedGasRecoverableResources,
        errors,
        "associatedGasRecoverableResources",
        "Associated gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD
    );

    var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasRecoverableResources,
        errors,
        "gasRecoverableResources",
        "Gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    );

    var hasAssociatedCondensate = mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources();
    if (BooleanUtils.isTrue(hasAssociatedCondensate)) {
      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
      );
    }

    var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );

    var naturalGasLiquidsResources = mixedCaseOutputsForm.getNaturalGasLiquidsResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        naturalGasLiquidsResources,
        errors,
        "naturalGasLiquidsResources",
        "Natural gas liquids resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );

    var salesGasResources = mixedCaseOutputsForm.getSalesGasResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        salesGasResources,
        errors,
        "salesGasResources",
        "Total sales gas resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesPartial(MixedCaseOutputsForm mixedCaseOutputsForm, Errors errors) {
    var oilRecoverableReserves = mixedCaseOutputsForm.getOilRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilRecoverableReserves,
        errors,
        "oilRecoverableReserves",
        "Oil reserves",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    );

    var associatedGasRecoverableReserves = mixedCaseOutputsForm.getAssociatedGasRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedGasRecoverableReserves,
        errors,
        "associatedGasRecoverableReserves",
        "Associated gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD
    );

    var gasRecoverableReserves = mixedCaseOutputsForm.getGasRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasRecoverableReserves,
        errors,
        "gasRecoverableReserves",
        "Associated gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
    );

    var associatedCondensateRecoverableReserves = mixedCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );

    var oilEquivalentRecoverableReserves = mixedCaseOutputsForm.getOilEquivalentRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilEquivalentRecoverableReserves,
        errors,
        "oilEquivalentRecoverableReserves",
        "Oil equivalent reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );

    var naturalGasLiquidsReserves = mixedCaseOutputsForm.getNaturalGasLiquidsReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        naturalGasLiquidsReserves,
        errors,
        "naturalGasLiquidsReserves",
        "Natural gas liquids reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );

    var salesGasReserves = mixedCaseOutputsForm.getSalesGasReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        salesGasReserves,
        errors,
        "salesGasReserves",
        "Total sales gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }

  private void validateResourcesFull(MixedCaseOutputsForm mixedCaseOutputsForm, Errors errors) {
    var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilRecoverableResources,
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );

    var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedGasRecoverableResources,
        errors,
        "associatedGasRecoverableResources",
        "Associated gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD
    );

    var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasRecoverableResources,
        errors,
        "gasRecoverableResources",
        "Gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    );

    var hasAssociatedCondensate = mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources();
    if (BooleanUtils.isTrue(hasAssociatedCondensate)) {
      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
      );
    } else if (hasAssociatedCondensate == null) {
      errors.rejectValue(
          "hasAssociatedCondensateRecoverableResources",
          "hasAssociatedCondensateRecoverableResources.required",
          "Select if you expect to find associated condensate recoverable resources");
    }

    var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );

    var naturalGasLiquidsResources = mixedCaseOutputsForm.getNaturalGasLiquidsResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        naturalGasLiquidsResources,
        errors,
        "naturalGasLiquidsResources",
        "Natural gas liquids resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );

    var salesGasResources = mixedCaseOutputsForm.getSalesGasResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        salesGasResources,
        errors,
        "salesGasResources",
        "Total sales gas resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesFull(MixedCaseOutputsForm mixedCaseOutputsForm, Errors errors) {
    var oilRecoverableReserves = mixedCaseOutputsForm.getOilRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilRecoverableReserves,
        errors,
        "oilRecoverableReserves",
        "Oil reserves",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    );

    var associatedGasRecoverableReserves = mixedCaseOutputsForm.getAssociatedGasRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedGasRecoverableReserves,
        errors,
        "associatedGasRecoverableReserves",
        "Associated gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD
    );

    var gasRecoverableReserves = mixedCaseOutputsForm.getGasRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasRecoverableReserves,
        errors,
        "gasRecoverableReserves",
        "Associated gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
    );

    var associatedCondensateRecoverableReserves = mixedCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );

    var oilEquivalentRecoverableReserves = mixedCaseOutputsForm.getOilEquivalentRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilEquivalentRecoverableReserves,
        errors,
        "oilEquivalentRecoverableReserves",
        "Oil equivalent reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );

    var naturalGasLiquidsReserves = mixedCaseOutputsForm.getNaturalGasLiquidsReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        naturalGasLiquidsReserves,
        errors,
        "naturalGasLiquidsReserves",
        "Natural gas liquids reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );

    var salesGasReserves = mixedCaseOutputsForm.getSalesGasReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        salesGasReserves,
        errors,
        "salesGasReserves",
        "Total sales gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }
}
