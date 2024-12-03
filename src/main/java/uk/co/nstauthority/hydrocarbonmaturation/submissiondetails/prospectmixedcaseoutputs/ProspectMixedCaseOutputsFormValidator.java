package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
class ProspectMixedCaseOutputsFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var mixedCaseOutputsForm = (MixedCaseOutputsForm) form;

    var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil initially in place",
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
        "Gas initially in place",
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

    if (hasAssociatedCondensateRecoverableResources(mixedCaseOutputsForm)) {
      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
      );
    }

    if (BooleanUtils.isFalse(useIndustryStandardConversion(mixedCaseOutputsForm))) {
      var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
    }

    var riskedOilEquivalentRecoverableMean = mixedCaseOutputsForm.getRiskedOilEquivalentRecoverableMean();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        riskedOilEquivalentRecoverableMean,
        errors,
        "riskedOilEquivalentRecoverableMean",
        "Risked oil equivalent recoverable",
        ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
    );
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var mixedCaseOutputsForm = (MixedCaseOutputsForm) form;

    var oilInPlace = mixedCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil initially in place",
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
        "Gas initially in place",
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

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "hasAssociatedCondensateRecoverableResources",
        "hasAssociatedCondensateRecoverableResources.required",
        "Select whether you expect to find associated condensate recoverable resources"
    );

    if (hasAssociatedCondensateRecoverableResources(mixedCaseOutputsForm)) {
      var associatedCondensateRecoverableResources = mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          associatedCondensateRecoverableResources,
          errors,
          "associatedCondensateRecoverableResources",
          "Associated condensate recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
      );
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "useIndustryStandardConversion",
        "useIndustryStandardConversion.required",
        "Select if you want to use industry standard conversion"
    );

    if (BooleanUtils.isFalse(useIndustryStandardConversion(mixedCaseOutputsForm))) {
      var oilEquivalentRecoverableResources = mixedCaseOutputsForm.getOilEquivalentRecoverableResources();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilEquivalentRecoverableResources,
          errors,
          "oilEquivalentRecoverableResources",
          "Oil equivalent recoverable resources",
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
    }

    var riskedOilEquivalentRecoverableMean = mixedCaseOutputsForm.getRiskedOilEquivalentRecoverableMean();
    ProbabilityDistributionValidationUtil.validateCustomFull(
        riskedOilEquivalentRecoverableMean,
        errors,
        "riskedOilEquivalentRecoverableMean",
        "Risked oil equivalent recoverable",
        ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
    );
  }

  private boolean hasAssociatedGasRecoverableResources(MixedCaseOutputsForm form) {
    return BooleanUtils.isTrue(form.getHasAssociatedGasRecoverableResources());
  }

  private boolean hasAssociatedCondensateRecoverableResources(MixedCaseOutputsForm form) {
    return BooleanUtils.isTrue(form.getHasAssociatedCondensateRecoverableResources());
  }
  
  private boolean useIndustryStandardConversion(MixedCaseOutputsForm mixedCaseOutputsForm) {
    return BooleanUtils.isTrue(mixedCaseOutputsForm.getUseIndustryStandardConversion());
  }
}