package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumincreasedvolumes;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class FieldDevelopmentPlanAddendumIncreasedVolumesFormValidator implements SubmissionValidator {

  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  public FieldDevelopmentPlanAddendumIncreasedVolumesFormValidator(
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService) {
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var fdpaIncreasedVolumesForm = (FieldDevelopmentPlanAddendumIncreasedVolumesForm) form;
    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail);

    if (Boolean.TRUE.equals(fdpaIncreasedVolumesForm.getChangeInVolumetrics())) {
      validateProbabilityDistributionsPartial(fdpaIncreasedVolumesForm, errors, hydrocarbonsDiscovered);
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var fdpaIncreasedVolumesForm = (FieldDevelopmentPlanAddendumIncreasedVolumesForm) form;
    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail);

    ValidationUtils.rejectIfEmpty(
        errors,
        "changeInVolumetrics",
        "changeInVolumetrics.required"
    );

    if (BooleanUtils.isTrue(fdpaIncreasedVolumesForm.getChangeInVolumetrics())) {
      validateProbabilityDistributionsFull(fdpaIncreasedVolumesForm, errors, hydrocarbonsDiscovered);

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "comment",
          "comment.required",
          "Provide comments on the incremental increased"
      );
    }
  }

  private void validateProbabilityDistributionsPartial(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                       Errors errors, HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (BooleanUtils.isTrue(isOilOrCombinedCase(hydrocarbonsDiscovered))) {
      validatePartialOilInitiallyInPlace(form, errors);
    }

    if (BooleanUtils.isTrue(isGasOrCombinedCase(hydrocarbonsDiscovered))) {
      validatePartialGasInitiallyInPlace(form, errors);
    }

    if (BooleanUtils.isTrue(isOilOrCombinedCase(hydrocarbonsDiscovered))) {
      validatePartialOilRecoverableResources(form, errors);
      validatePartialOilReserves(form, errors);
    }

    validatePartialAssociatedCondensateRecoverableResources(form, errors);
    validatePartialAssociatedCondensateReserves(form, errors);
    validatePartialNaturalGasLiquidsRecoverableResources(form, errors);
    validatePartialNaturalGasLiquidsReserves(form, errors);
    validatePartialSalesGasResources(form, errors);
    validatePartialSalesGasReserves(form, errors);
    validatePartialOilEquivalentRecoverableResources(form, errors);
    validatePartialOilEquivalentReserves(form, errors);
  }

  private void validateProbabilityDistributionsFull(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                    Errors errors, HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (BooleanUtils.isTrue(isOilOrCombinedCase(hydrocarbonsDiscovered))) {
      validateFullOilInitiallyInPlace(form, errors);
    }

    if (BooleanUtils.isTrue(isGasOrCombinedCase(hydrocarbonsDiscovered))) {
      validateFullGasInitiallyInPlace(form, errors);
    }

    if (BooleanUtils.isTrue(isOilOrCombinedCase(hydrocarbonsDiscovered))) {
      validateFullOilRecoverableResources(form, errors);
      validateFullOilReserves(form, errors);
    }

    validateFullAssociatedCondensateRecoverableResources(form, errors);
    validateFullAssociatedCondensateReserves(form, errors);
    validateFullNaturalGasLiquidsRecoverableResources(form, errors);
    validateFullNaturalGasLiquidsReserves(form, errors);
    validateFullSalesGasResources(form, errors);
    validateFullSalesGasReserves(form, errors);
    validateFullOilEquivalentRecoverableResources(form, errors);
    validateFullOilEquivalentReserves(form, errors);
  }

  private void validatePartialOilEquivalentReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                    Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getOilEquivalentReserves(),
        errors,
        "oilEquivalentReserves",
        "Oil equivalent reserves",
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialOilEquivalentRecoverableResources(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                                Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getOilEquivalentRecoverableResources(),
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialSalesGasReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getSalesGasReserves(),
        errors,
        "salesGasReserves",
        "Sales gas reserves",
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialSalesGasResources(FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getSalesGasRecoverableResources(),
        errors,
        "salesGasRecoverableResources",
        "Sales gas resources",
        ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialNaturalGasLiquidsReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                        Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getNaturalGasLiquidsReserves(),
        errors,
        "naturalGasLiquidsReserves",
        "Natural gas liquids reserves",
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialNaturalGasLiquidsRecoverableResources(
      FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getNaturalGasLiquidsRecoverableResources(),
        errors,
        "naturalGasLiquidsRecoverableResources",
        "Natural gas liquids resources",
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialAssociatedCondensateReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                           Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getAssociatedCondensateReserves(),
        errors,
        "associatedCondensateReserves",
        "Associated condensate reserves",
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialOilInitiallyInPlace(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                  Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getOilInitiallyInPlace(),
        errors,
        "oilInitiallyInPlace",
        "Oil initially in place",
        ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialGasInitiallyInPlace(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                  Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getGasInitiallyInPlace(),
        errors,
        "gasInitiallyInPlace",
        "Gas initially in place",
        ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialOilRecoverableResources(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                      Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getOilRecoverableResources(),
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialOilReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getOilReserves(),
        errors,
        "oilReserves",
        "Oil reserves",
        ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validatePartialAssociatedCondensateRecoverableResources(
      FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        form.getAssociatedCondensateRecoverableResources(),
        errors,
        "associatedCondensateRecoverableResources",
        "Associated condensate recoverable resources",
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullOilEquivalentReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                 Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getOilEquivalentReserves(),
        errors,
        "oilEquivalentReserves",
        "Oil equivalent reserves",
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullOilEquivalentRecoverableResources(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                             Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getOilEquivalentRecoverableResources(),
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullSalesGasReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getSalesGasReserves(),
        errors,
        "salesGasReserves",
        "Sales gas reserves",
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullSalesGasResources(FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getSalesGasRecoverableResources(),
        errors,
        "salesGasRecoverableResources",
        "Sales gas resources",
        ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullNaturalGasLiquidsReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                     Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getNaturalGasLiquidsReserves(),
        errors,
        "naturalGasLiquidsReserves",
        "Natural gas liquids reserves",
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullNaturalGasLiquidsRecoverableResources(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                                 Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getNaturalGasLiquidsRecoverableResources(),
        errors,
        "naturalGasLiquidsRecoverableResources",
        "Natural gas liquids resources",
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullAssociatedCondensateReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                           Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getAssociatedCondensateReserves(),
        errors,
        "associatedCondensateReserves",
        "Associated condensate reserves",
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullOilInitiallyInPlace(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                  Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getOilInitiallyInPlace(),
        errors,
        "oilInitiallyInPlace",
        "Oil initially in place",
        ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullGasInitiallyInPlace(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                  Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getGasInitiallyInPlace(),
        errors,
        "gasInitiallyInPlace",
        "Gas initially in place",
        ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullOilRecoverableResources(FieldDevelopmentPlanAddendumIncreasedVolumesForm form,
                                                      Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getOilRecoverableResources(),
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullOilReserves(FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getOilReserves(),
        errors,
        "oilReserves",
        "Oil reserves",
        ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA
    );
  }

  private void validateFullAssociatedCondensateRecoverableResources(
      FieldDevelopmentPlanAddendumIncreasedVolumesForm form, Errors errors) {
    ProbabilityDistributionValidationUtil.validateCustomFull(
        form.getAssociatedCondensateRecoverableResources(),
        errors,
        "associatedCondensateRecoverableResources",
        "Associated condensate recoverable resources",
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
    );
  }

  private boolean isOilOrCombinedCase(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    return hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.OIL_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
  }

  private boolean isGasOrCombinedCase(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    return hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.GAS_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
  }
}
