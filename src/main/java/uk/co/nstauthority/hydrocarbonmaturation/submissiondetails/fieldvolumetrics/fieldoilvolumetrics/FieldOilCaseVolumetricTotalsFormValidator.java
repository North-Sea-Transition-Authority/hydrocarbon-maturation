package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilvolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class FieldOilCaseVolumetricTotalsFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var oilInPlace = oilCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            oilInPlace,
            errors,
            "oilInPlace",
            "Oil in place",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );

    validateResourcesPartial(oilCaseOutputsForm, errors);
    if (!submissionDetail.isConceptSelectReportSubmission()) {
      validateReservesPartial(oilCaseOutputsForm, errors);
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var oilInPlace = oilCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            oilInPlace,
            errors,
            "oilInPlace",
            "Oil in place",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );

    validateResourcesFull(oilCaseOutputsForm, errors);
    if (!submissionDetail.isConceptSelectReportSubmission()) {
      validateReservesFull(oilCaseOutputsForm, errors);
    }
  }

  private void validateResourcesPartial(OilCaseOutputsForm oilCaseOutputsForm, Errors errors) {
    var oilRecoverableResources = oilCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            oilRecoverableResources,
            errors,
            "oilRecoverableResources",
            "Oil recoverable resources",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );

    var associatedCondensateRecoverableResources = oilCaseOutputsForm.getAssociatedCondensateRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedCondensateRecoverableResources,
        errors,
        "associatedCondensateRecoverableResources",
        "Associated condensate recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    );

    var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD
    );

    var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            oilEquivalentRecoverableResources,
            errors,
            "oilEquivalentRecoverableResources",
            "Oil equivalent recoverable resources",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );

    var naturalGasLiquidsResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            naturalGasLiquidsResources,
            errors,
            "naturalGasLiquidsResources",
            "Natural gas liquids resources",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );

    var salesGasResources = oilCaseOutputsForm.getSalesGasResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            salesGasResources,
            errors,
            "salesGasResources",
            "Total sales gas resources",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesPartial(OilCaseOutputsForm oilCaseOutputsForm, Errors errors) {
    var oilRecoverableReserves = oilCaseOutputsForm.getOilRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            oilRecoverableReserves,
            errors,
            "oilRecoverableReserves",
            "Oil recoverable reserves",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    );

    var associatedGasRecoverableReserves = oilCaseOutputsForm.getAssociatedGasRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            associatedGasRecoverableReserves,
            errors,
            "associatedGasRecoverableReserves",
            "Associated gas recoverable reserves",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD
    );

    var associatedCondensateRecoverableReserves = oilCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate recoverable reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );

    var oilEquivalentRecoverableReserves = oilCaseOutputsForm.getOilEquivalentRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            oilEquivalentRecoverableReserves,
            errors,
            "oilEquivalentRecoverableReserves",
            "Oil equivalent recoverable reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );

    var naturalGasLiquidsReserves = oilCaseOutputsForm.getOilEquivalentRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            naturalGasLiquidsReserves,
            errors,
            "naturalGasLiquidsReserves",
            "Natural gas liquids reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );

    var salesGasReserves = oilCaseOutputsForm.getSalesGasReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
            salesGasReserves,
            errors,
            "salesGasReserves",
            "Total sales gas reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }

  private void validateResourcesFull(OilCaseOutputsForm oilCaseOutputsForm, Errors errors) {
    var oilRecoverableResources = oilCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            oilRecoverableResources,
            errors,
            "oilRecoverableResources",
            "Oil recoverable resources",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );

    var associatedCondensateRecoverableResources = oilCaseOutputsForm.getAssociatedCondensateRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedCondensateRecoverableResources,
        errors,
        "associatedCondensateRecoverableResources",
        "Associated condensate recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    );

    var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD
    );

    var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            oilEquivalentRecoverableResources,
            errors,
            "oilEquivalentRecoverableResources",
            "Oil equivalent recoverable resources",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );

    var naturalGasLiquidsResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            naturalGasLiquidsResources,
            errors,
            "naturalGasLiquidsResources",
            "Natural gas liquids resources",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );

    var salesGasResources = oilCaseOutputsForm.getSalesGasResources();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            salesGasResources,
            errors,
            "salesGasResources",
            "Total sales gas resources",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesFull(OilCaseOutputsForm oilCaseOutputsForm, Errors errors) {
    var oilRecoverableReserves = oilCaseOutputsForm.getOilRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            oilRecoverableReserves,
            errors,
            "oilRecoverableReserves",
            "Oil recoverable reserves",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    );

    var associatedCondensateRecoverableReserves = oilCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate recoverable reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );

    var associatedGasRecoverableReserves = oilCaseOutputsForm.getAssociatedGasRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            associatedGasRecoverableReserves,
            errors,
            "associatedGasRecoverableReserves",
            "Associated gas recoverable reserves",
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD
    );

    var oilEquivalentRecoverableReserves = oilCaseOutputsForm.getOilEquivalentRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            oilEquivalentRecoverableReserves,
            errors,
            "oilEquivalentRecoverableReserves",
            "Oil equivalent recoverable reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );

    var naturalGasLiquidsReserves = oilCaseOutputsForm.getOilEquivalentRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            naturalGasLiquidsReserves,
            errors,
            "naturalGasLiquidsReserves",
            "Natural gas liquids reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );

    var salesGasReserves = oilCaseOutputsForm.getSalesGasReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
            salesGasReserves,
            errors,
            "salesGasReserves",
            "Total sales gas reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }
}
