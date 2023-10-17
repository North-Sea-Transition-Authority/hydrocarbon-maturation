package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryoilcasevolumetrictotals;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class DiscoveryOilCaseVolumetricTotalsFormValidator implements SubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var oilCaseOutputsForm = (OilCaseOutputsForm) form;

    var oilInPlace = oilCaseOutputsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );

    var oilRecoverableResources = oilCaseOutputsForm.getOilRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilRecoverableResources,
        errors,
        "oilRecoverableResources",
        "Oil recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    );

    var associatedGasRecoverableResources = oilCaseOutputsForm.getAssociatedGasRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        associatedGasRecoverableResources,
        errors,
        "associatedGasRecoverableResources",
        "Associated gas recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    var oilEquivalentRecoverableResources = oilCaseOutputsForm.getOilEquivalentRecoverableResources();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilEquivalentRecoverableResources,
        errors,
        "oilEquivalentRecoverableResources",
        "Oil equivalent recoverable resources",
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );
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