package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
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
    throw new ActionNotAllowedException("You must call validatePartialWithSubmissionDetail");
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    throw new ActionNotAllowedException("You must call validateFullWithSubmissionDetail");
  }

  @Override
  public void validatePartialWithSubmissionDetail(SubmissionForm form, SubmissionDetail submissionDetail, Errors errors) {
    var mixedCaseOutputsForm = (MixedCaseOutputsForm) form;

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT
      );

      var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilRecoverableResources,
          errors,
          "oilRecoverableResources",
          "Oil recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT
      );

      if (hasAssociatedGasRecoverableResources(mixedCaseOutputsForm)) {
        var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
        );
      }

      var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT
      );

      var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasRecoverableResources,
          errors,
          "gasRecoverableResources",
          "Gas recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT
      );

      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
      );

      var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
    } else if (resourceType == ResourceType.DISCOVERY) {
      var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY
      );

      var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilRecoverableResources,
          errors,
          "oilRecoverableResources",
          "Oil recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY
      );

      if (hasAssociatedGasRecoverableResources(mixedCaseOutputsForm)) {
        var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }

      var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY
      );

      var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasRecoverableResources,
          errors,
          "gasRecoverableResources",
          "Gas recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      );

      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );

      var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }
  }

  @Override
  public void validateFullWithSubmissionDetail(SubmissionForm form, SubmissionDetail submissionDetail, Errors errors) {
    var mixedCaseOutputsForm = (MixedCaseOutputsForm) form;

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT
      );

      var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilRecoverableResources,
          errors,
          "oilRecoverableResources",
          "Oil recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT
      );

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "hasAssociatedGasRecoverableResources",
          "hasAssociatedGasRecoverableResources.required",
          "Select whether you expect to find associated gas recoverable resources"
      );

      if (hasAssociatedGasRecoverableResources(mixedCaseOutputsForm)) {
        var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomFull(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
        );
      }

      var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT
      );

      var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasRecoverableResources,
          errors,
          "gasRecoverableResources",
          "Gas recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT
      );

      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
      );

      var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
    } else if (resourceType == ResourceType.DISCOVERY) {
      var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY
      );

      var oilRecoverableResources = mixedCaseOutputsForm.getOilRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilRecoverableResources,
          errors,
          "oilRecoverableResources",
          "Oil recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY
      );

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "hasAssociatedGasRecoverableResources",
          "hasAssociatedGasRecoverableResources.required",
          "Select whether you expect to find associated gas recoverable resources"
      );

      if (hasAssociatedGasRecoverableResources(mixedCaseOutputsForm)) {
        var associatedGasRecoverableResources = mixedCaseOutputsForm.getAssociatedGasRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomFull(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }

      var gasInPlace = mixedCaseOutputsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY
      );

      var gasRecoverableResources = mixedCaseOutputsForm.getGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasRecoverableResources,
          errors,
          "gasRecoverableResources",
          "Gas recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      );

      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );

      var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }

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