
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FieldOilCaseVolumetricTotalsFormValidator implements SubmissionValidator {

  static final List<SubmissionType> VOLUMETRIC_CORRECT_QUESTION_SUBMISSION_TYPES = List.of(SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_FDPA, SubmissionType.CREATE_COP);

  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public FieldOilCaseVolumetricTotalsFormValidator(SubmissionDetailService submissionDetailService) {
    this.submissionDetailService = submissionDetailService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var oilInPlace = oilCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil initially in place",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );

    var withReserves = !submissionDetail.isConceptSelectReportSubmission();

    validateResourcesPartial(oilCaseOutputsForm, errors);

    if (BooleanUtils.isTrue(withReserves)) {
      validateReservesPartial(oilCaseOutputsForm, errors);
    }

    validateOilEquivalent(oilCaseOutputsForm, errors, withReserves, ValidationType.PARTIAL);
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var oilInPlace = oilCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil initially in place",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );

    validateResourcesFull(oilCaseOutputsForm, errors);
    var withReserves = !submissionDetail.isConceptSelectReportSubmission();
    if (BooleanUtils.isTrue(withReserves)) {
      validateReservesFull(oilCaseOutputsForm, errors);
    }

    validateOilEquivalent(oilCaseOutputsForm, errors, withReserves, ValidationType.FULL);

    if (VOLUMETRIC_CORRECT_QUESTION_SUBMISSION_TYPES.contains(submissionDetail.getSubmissionType())
        && submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail)) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "fieldVolumetricInformationCorrect",
          "fieldVolumetricInformationCorrect.required",
          "Select if the field volumetric information reflects the current state of the field"
      );
    }
  }

  private void validateOilEquivalent(OilCaseOutputsForm oilCaseOutputsForm, Errors errors,
                                     Boolean withReserves, ValidationType validationType) {
    if (ValidationType.PARTIAL.equals(validationType)) {
      var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
      );

      if (BooleanUtils.isTrue(withReserves)) {
        var oilEquivalentRecoverableReserves = oilCaseOutputsForm.getOilEquivalentRecoverableReserves();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            oilEquivalentRecoverableReserves,
            errors,
            "oilEquivalentRecoverableReserves",
            "Oil equivalent reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
        );
      }
    } else {
      var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
      );

      if (BooleanUtils.isTrue(withReserves)) {
        var oilEquivalentRecoverableReserves = oilCaseOutputsForm.getOilEquivalentRecoverableReserves();
        ProbabilityDistributionValidationUtil.validateCustomFull(
            oilEquivalentRecoverableReserves,
            errors,
            "oilEquivalentRecoverableReserves",
            "Oil equivalent reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
        );
      }
    }
  }

  private void validateResourcesPartial(
      OilCaseOutputsForm oilCaseOutputsForm, Errors errors
  ) {
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

    var naturalGasLiquidsResources = oilCaseOutputsForm.getNaturalGasLiquidsResources();
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
        "Sales gas resources resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesPartial(OilCaseOutputsForm oilCaseOutputsForm, Errors errors) {
    var oilRecoverableReserves = oilCaseOutputsForm.getOilRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilRecoverableReserves,
        errors,
        "oilRecoverableReserves",
        "Oil reserves",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    );

    var associatedCondensateRecoverableReserves = oilCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );

    var naturalGasLiquidsReserves = oilCaseOutputsForm.getNaturalGasLiquidsReserves();
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
        "Sales gas reserves",
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

    var naturalGasLiquidsResources = oilCaseOutputsForm.getNaturalGasLiquidsResources();
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
        "Sales gas resources resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesFull(OilCaseOutputsForm oilCaseOutputsForm, Errors errors) {
    var oilRecoverableReserves = oilCaseOutputsForm.getOilRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilRecoverableReserves,
        errors,
        "oilRecoverableReserves",
        "Oil reserves",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    );

    var associatedCondensateRecoverableReserves = oilCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );

    var naturalGasLiquidsReserves = oilCaseOutputsForm.getNaturalGasLiquidsReserves();
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
        "Sales gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }
}
