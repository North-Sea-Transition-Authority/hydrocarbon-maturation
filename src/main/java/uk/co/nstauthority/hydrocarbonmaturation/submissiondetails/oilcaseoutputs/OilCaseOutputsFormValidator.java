package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import org.apache.commons.lang3.BooleanUtils;
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
public class OilCaseOutputsFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      var oilInPlace = oilCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil initially in place",
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

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "useIndustryStandardConversion",
          "useIndustryStandardConversion.required",
          "Select if you want to use industry standard conversion");

      if (BooleanUtils.isFalse(useIndustryStandardConversion(oilCaseOutputsForm))) {
        var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomPartial(
            oilEquivalentRecoverableResources,
            errors,
            "oilEquivalentRecoverableResources",
            "Oil equivalent recoverable resources",
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        );
      }

      var riskedOilEquivalentRecoverableMean = oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          riskedOilEquivalentRecoverableMean,
          errors,
          "riskedOilEquivalentRecoverableMean",
          "Risked oil equivalent recoverable",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
      );

    } else if (resourceType == ResourceType.DISCOVERY) {
      var oilInPlace = oilCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil initially in place",
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

      var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          associatedGasRecoverableResources,
          errors,
          "associatedGasRecoverableResources",
          "Associated gas recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      );

      var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
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
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      var oilInPlace = oilCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil initially in place",
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

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "useIndustryStandardConversion",
          "useIndustryStandardConversion.required",
          "Select if you want to use industry standard conversion");

      if (BooleanUtils.isFalse(useIndustryStandardConversion(oilCaseOutputsForm))) {
        var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
        ProbabilityDistributionValidationUtil.validateCustomFull(
            oilEquivalentRecoverableResources,
            errors,
            "oilEquivalentRecoverableResources",
            "Oil equivalent recoverable resources",
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        );
      }

      var riskedOilEquivalentRecoverableMean = oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          riskedOilEquivalentRecoverableMean,
          errors,
          "riskedOilEquivalentRecoverableMean",
          "Risked oil equivalent recoverable",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
      );

    } else if (resourceType == ResourceType.DISCOVERY) {
      var oilInPlace = oilCaseOutputsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil initially in place",
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

      var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          associatedGasRecoverableResources,
          errors,
          "associatedGasRecoverableResources",
          "Associated gas recoverable resources",
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      );

      var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }
  }

  private boolean hasAssociatedGasRecoverableResources(OilCaseOutputsForm oilCaseOutputsForm) {
    return BooleanUtils.isTrue(oilCaseOutputsForm.getHasAssociatedGasRecoverableResources());
  }

  private boolean useIndustryStandardConversion(OilCaseOutputsForm oilCaseOutputsForm) {
    return BooleanUtils.isTrue(oilCaseOutputsForm.getUseIndustryStandardConversion());
  }
}