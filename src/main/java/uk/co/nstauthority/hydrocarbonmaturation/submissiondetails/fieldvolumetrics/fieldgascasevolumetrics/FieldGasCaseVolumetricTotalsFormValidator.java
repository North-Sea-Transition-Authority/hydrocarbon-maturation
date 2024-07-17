package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FieldGasCaseVolumetricTotalsFormValidator implements SubmissionValidator {

  static final List<SubmissionType> VOLUMETRIC_CORRECT_QUESTION_SUBMISSION_TYPES = List.of(SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_FDPA, SubmissionType.CREATE_COP);

  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public FieldGasCaseVolumetricTotalsFormValidator(SubmissionDetailService submissionDetailService) {
    this.submissionDetailService = submissionDetailService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseOutputsForm = (GasCaseOutputsForm) form;

    var gasInPlace = gasCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas initially in place",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );

    var withReserves = !submissionDetail.isConceptSelectReportSubmission();

    validateResourcesPartial(gasCaseOutputsForm, errors);
    if (BooleanUtils.isTrue(withReserves)) {
      validateReservesPartial(gasCaseOutputsForm, errors);
    }

    validateOilEquivalent(gasCaseOutputsForm, errors, withReserves, ValidationType.PARTIAL);
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseOutputsForm = (GasCaseOutputsForm) form;

    var gasInPlace = gasCaseOutputsForm.getGasInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        gasInPlace,
        errors,
        "gasInPlace",
        "Gas initially in place",
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );

    var withReserves = !submissionDetail.isConceptSelectReportSubmission();

    validateResourcesFull(gasCaseOutputsForm, errors);

    if (BooleanUtils.isTrue(withReserves)) {
      validateReservesFull(gasCaseOutputsForm, errors);
    }

    validateOilEquivalent(gasCaseOutputsForm, errors, withReserves, ValidationType.FULL);

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

  private void validateResourcesPartial(
      GasCaseOutputsForm gasCaseOutputsForm, Errors errors
  ) {

    var hasAssociatedCondensate = gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources();
    if (BooleanUtils.isTrue(hasAssociatedCondensate)) {
      var associatedCondensateRecoverableResources = gasCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
      );
    }

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
        "Sales gas resources resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesPartial(
      GasCaseOutputsForm gasCaseOutputsForm, Errors errors
  ) {
    var associatedCondensateRecoverableReserves = gasCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
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
        "Sales gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }

  private void validateResourcesFull(
      GasCaseOutputsForm gasCaseOutputsForm, Errors errors
  ) {

    var hasAssociatedCondensate = gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources();
    if (BooleanUtils.isTrue(hasAssociatedCondensate)) {
      var associatedCondensateRecoverableResources = gasCaseOutputsForm.getAssociatedCondensateRecoverableResources();
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
        "Sales gas resources resources",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );
  }

  private void validateReservesFull(GasCaseOutputsForm gasCaseOutputsForm, Errors errors) {
    var associatedCondensateRecoverableReserves = gasCaseOutputsForm.getAssociatedCondensateRecoverableReserves();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        associatedCondensateRecoverableReserves,
        errors,
        "associatedCondensateRecoverableReserves",
        "Associated condensate reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
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
        "Sales gas reserves",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );
  }

  private void validateOilEquivalent(GasCaseOutputsForm gasCaseOutputsForm, Errors errors,
                                     Boolean withReserves, ValidationType validationType) {
    if (ValidationType.PARTIAL.equals(validationType)) {
      var oilEquivalentRecoverableResources = gasCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
      );

      if (BooleanUtils.isTrue(withReserves)) {
        var oilEquivalentRecoverableReserves = gasCaseOutputsForm.getOilEquivalentRecoverableReserves();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            oilEquivalentRecoverableReserves,
            errors,
            "oilEquivalentRecoverableReserves",
            "Oil equivalent reserves",
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
        );
      }
    } else {
      var oilEquivalentRecoverableResources = gasCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
      );

      if (BooleanUtils.isTrue(withReserves)) {
        var oilEquivalentRecoverableReserves = gasCaseOutputsForm.getOilEquivalentRecoverableReserves();
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
}
