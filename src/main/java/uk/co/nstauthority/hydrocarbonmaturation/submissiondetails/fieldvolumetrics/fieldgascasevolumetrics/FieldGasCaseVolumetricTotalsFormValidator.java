package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class FieldGasCaseVolumetricTotalsFormValidator implements SubmissionValidator {
  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseOutputsForm = (GasCaseOutputsForm) form;

    var gasInPlace = gasCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas in place",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );

    validateResourcesPartial(gasCaseOutputsForm, errors);
    if (!submissionDetail.isConceptSelectReportSubmission()) {
      validateReservesPartial(gasCaseOutputsForm, errors);
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseOutputsForm = (GasCaseOutputsForm) form;

    var gasInPlace = gasCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas in place",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );

    validateResourcesFull(gasCaseOutputsForm, errors);
    if (!submissionDetail.isConceptSelectReportSubmission()) {
      validateReservesFull(gasCaseOutputsForm, errors);
    }
  }

  private void validateResourcesPartial(GasCaseOutputsForm gasCaseOutputsForm, Errors errors) {
    var gasRecoverableResources = gasCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasRecoverableResources,
        errors,
        "gasRecoverableResources",
        "Gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    );

    var associatedCondensateRecoverableResources = gasCaseOutputsForm.getAssociatedCondensateRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedCondensateRecoverableResources,
        errors,
        "associatedCondensateRecoverableResources",
        "Associated condensate recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    );

    var oilEquivalentRecoverableResources = gasCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );

    var naturalGasLiquidsResources = gasCaseOutputsForm.getNaturalGasLiquidsResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        naturalGasLiquidsResources,
        errors,
        "naturalGasLiquidsResources",
        "Natural gas liquids resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );

    var salesGasResources = gasCaseOutputsForm.getSalesGasResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        salesGasResources,
        errors,
        "salesGasResources",
        "Total sales gas resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesPartial(GasCaseOutputsForm gasCaseOutputsForm, Errors errors) {
    var gasRecoverableReserves = gasCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasRecoverableReserves,
        errors,
        "gasRecoverableReserves",
        "Gas recoverable reserves",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
    );

    var associatedCondensateRecoverableReserves = gasCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate recoverable reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );

    var oilEquivalentRecoverableReserves = gasCaseOutputsForm.getOilEquivalentRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilEquivalentRecoverableReserves,
        errors,
        "oilEquivalentRecoverableReserves",
        "Oil equivalent recoverable reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );

    var naturalGasLiquidsReserves = gasCaseOutputsForm.getNaturalGasLiquidsReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        naturalGasLiquidsReserves,
        errors,
        "naturalGasLiquidsReserves",
        "Natural gas liquids reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );

    var salesGasReserves = gasCaseOutputsForm.getSalesGasReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        salesGasReserves,
        errors,
        "salesGasReserves",
        "Total sales gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }

  private void validateResourcesFull(GasCaseOutputsForm gasCaseOutputsForm, Errors errors) {
    var gasRecoverableResources = gasCaseOutputsForm.getGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasRecoverableResources,
        errors,
        "gasRecoverableResources",
        "Gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    );

    var associatedCondensateRecoverableResources = gasCaseOutputsForm.getAssociatedCondensateRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedCondensateRecoverableResources,
        errors,
        "associatedCondensateRecoverableResources",
        "Associated condensate recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    );

    var oilEquivalentRecoverableResources = gasCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );

    var naturalGasLiquidsResources = gasCaseOutputsForm.getNaturalGasLiquidsResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        naturalGasLiquidsResources,
        errors,
        "naturalGasLiquidsResources",
        "Natural gas liquids resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );

    var salesGasResources = gasCaseOutputsForm.getSalesGasResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        salesGasResources,
        errors,
        "salesGasResources",
        "Total sales gas resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesFull(GasCaseOutputsForm gasCaseOutputsForm, Errors errors) {
    var gasRecoverableReserves = gasCaseOutputsForm.getGasRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasRecoverableReserves,
        errors,
        "gasRecoverableReserves",
        "Gas recoverable reserves",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
    );

    var associatedCondensateRecoverableReserves = gasCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate recoverable reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );

    var oilEquivalentRecoverableReserves = gasCaseOutputsForm.getOilEquivalentRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilEquivalentRecoverableReserves,
        errors,
        "oilEquivalentRecoverableReserves",
        "Oil equivalent recoverable reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );

    var naturalGasLiquidsReserves = gasCaseOutputsForm.getNaturalGasLiquidsReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        naturalGasLiquidsReserves,
        errors,
        "naturalGasLiquidsReserves",
        "Natural gas liquids reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );

    var salesGasReserves = gasCaseOutputsForm.getSalesGasReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        salesGasReserves,
        errors,
        "salesGasReserves",
        "Total sales gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }
}
