package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
public class OilCaseOutputsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return OilCaseOutputsForm.class.equals(clazz);
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
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      var oilInPlace = oilCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT
      );

      var oilRecoverableResources = oilCaseOutputsForm.getOilRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilRecoverableResources,
          errors,
          "oilRecoverableResources",
          "Oil recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT
      );

      if (hasAssociatedGasRecoverableResources(oilCaseOutputsForm)) {
        var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
        );
      }

      var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
    } else if (resourceType == ResourceType.DISCOVERY) {
      var oilInPlace = oilCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
      );

      var oilRecoverableResources = oilCaseOutputsForm.getOilRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilRecoverableResources,
          errors,
          "oilRecoverableResources",
          "Oil recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
      );

      if (hasAssociatedGasRecoverableResources(oilCaseOutputsForm)) {
        var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }

      var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }
  }

  @Override
  public void validateFullWithSubmissionDetail(SubmissionForm form, SubmissionDetail submissionDetail, Errors errors) {
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      var oilInPlace = oilCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT
      );

      var oilRecoverableResources = oilCaseOutputsForm.getOilRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilRecoverableResources,
          errors,
          "oilRecoverableResources",
          "Oil recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT
      );

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "hasAssociatedGasRecoverableResources",
          "hasAssociatedGasRecoverableResources.required",
          "Select whether you expect to find associated gas recoverable resources");

      if (hasAssociatedGasRecoverableResources(oilCaseOutputsForm)) {
        var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomFull(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
        );
      }

      var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
    } else if (resourceType == ResourceType.DISCOVERY) {
      var oilInPlace = oilCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
      );

      var oilRecoverableResources = oilCaseOutputsForm.getOilRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilRecoverableResources,
          errors,
          "oilRecoverableResources",
          "Oil recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
      );

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "hasAssociatedGasRecoverableResources",
          "hasAssociatedGasRecoverableResources.required",
          "Select whether you expect to find associated gas recoverable resources");

      if (hasAssociatedGasRecoverableResources(oilCaseOutputsForm)) {
        var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomFull(
            associatedGasRecoverableResources,
            errors,
            "associatedGasRecoverableResources",
            "Associated gas recoverable resources",
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }

      var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }

    DecimalInputValidator.configureValidator(errors, "riskedOilEquivalentRecoverableMean.decimalInputValue",
        "The risked oil equivalent recoverable mean", oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean())
        .setMaxDecimalPlaces(0)
        .validate();
  }

  private boolean hasAssociatedGasRecoverableResources(OilCaseOutputsForm oilCaseOutputsForm) {
    return oilCaseOutputsForm.getHasAssociatedGasRecoverableResources() != null
        && oilCaseOutputsForm.getHasAssociatedGasRecoverableResources();
  }
}