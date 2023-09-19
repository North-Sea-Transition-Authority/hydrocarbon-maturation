package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
class GasCaseOutputsFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseOutputsForm = (GasCaseOutputsForm) form;

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      var gasInPlace = gasCaseOutputsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT
      );

      var gasRecoverableResources = gasCaseOutputsForm.getGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasRecoverableResources,
          errors,
          "gasRecoverableResources",
          "Gas recoverable resources",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT
      );

      if (hasAssociatedCondensateRecoverableResources(gasCaseOutputsForm)) {
        var associatedCondensateRecoverableResources = gasCaseOutputsForm.getAssociatedCondensateRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            associatedCondensateRecoverableResources,
            errors,
            "associatedCondensateRecoverableResources",
            "Associated condensate recoverable resources",
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
        );
      }

      var oilEquivalentRecoverableResources = gasCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
    } else if (resourceType == ResourceType.DISCOVERY) {
      var gasInPlace = gasCaseOutputsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
      );

      var gasRecoverableResources = gasCaseOutputsForm.getGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasRecoverableResources,
          errors,
          "gasRecoverableResources",
          "Gas recoverable resources",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      );

      if (hasAssociatedCondensateRecoverableResources(gasCaseOutputsForm)) {
        var associatedCondensateRecoverableResources = gasCaseOutputsForm.getAssociatedCondensateRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            associatedCondensateRecoverableResources,
            errors,
            "associatedCondensateRecoverableResources",
            "Associated condensate recoverable resources",
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }

      var oilEquivalentRecoverableResources = gasCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var gasCaseOutputsForm = (GasCaseOutputsForm) form;

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      var gasInPlace = gasCaseOutputsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT
      );

      var gasRecoverableResources = gasCaseOutputsForm.getGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasRecoverableResources,
          errors,
          "gasRecoverableResources",
          "Gas recoverable resources",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT
      );

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "hasAssociatedCondensateRecoverableResources",
          "hasAssociatedCondensateRecoverableResources.required",
          "Select whether you expect to find associated condensate recoverable resources");

      if (hasAssociatedCondensateRecoverableResources(gasCaseOutputsForm)) {
        var associatedCondensateRecoverableResources = gasCaseOutputsForm.getAssociatedCondensateRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomFull(
            associatedCondensateRecoverableResources,
            errors,
            "associatedCondensateRecoverableResources",
            "Associated condensate recoverable resources",
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
        );
      }

      var oilEquivalentRecoverableResources = gasCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
    } else if (resourceType == ResourceType.DISCOVERY) {
      var gasInPlace = gasCaseOutputsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
      );

      var gasRecoverableResources = gasCaseOutputsForm.getGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasRecoverableResources,
          errors,
          "gasRecoverableResources",
          "Gas recoverable resources",
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      );

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "hasAssociatedCondensateRecoverableResources",
          "hasAssociatedCondensateRecoverableResources.required",
          "Select whether you expect to find associated condensate recoverable resources");

      if (hasAssociatedCondensateRecoverableResources(gasCaseOutputsForm)) {
        var associatedCondensateRecoverableResources = gasCaseOutputsForm.getAssociatedCondensateRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomFull(
            associatedCondensateRecoverableResources,
            errors,
            "associatedCondensateRecoverableResources",
            "Associated condensate recoverable resources",
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }

      var oilEquivalentRecoverableResources = gasCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }
  }

  private boolean hasAssociatedCondensateRecoverableResources(GasCaseOutputsForm gasCaseOutputsForm) {
    return gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources() != null
        && gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources();
  }
}
