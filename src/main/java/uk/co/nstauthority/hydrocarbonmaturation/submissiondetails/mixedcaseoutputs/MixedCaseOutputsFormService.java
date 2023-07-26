package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class MixedCaseOutputsFormService implements SubmissionFormService {

  private final MixedCaseOutputsFormValidator mixedCaseOutputsFormValidator;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  @Autowired
  public MixedCaseOutputsFormService(
      MixedCaseOutputsFormValidator mixedCaseOutputsFormValidator,
      ProbabilityDistributionFormService probabilityDistributionFormService,
      MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService) {
    this.mixedCaseOutputsFormValidator = mixedCaseOutputsFormValidator;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.mixedCaseOutputsPersistenceService = mixedCaseOutputsPersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    mixedCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  MixedCaseOutputsForm getMixedCaseOutputsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private MixedCaseOutputsForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new MixedCaseOutputsForm();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var mixedCaseOutputs = mixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail);
    var hasAssociatedGasRecoverableResources = mixedCaseOutputs
        .map(MixedCaseOutput::getHasAssociatedGasRecoverableResources)
        .orElse(null);
    var riskedMixedEquivalentRecoverableMean = mixedCaseOutputs
        .map(MixedCaseOutput::getRiskedOilEquivalentRecoverableMean)
        .orElse(null);

    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(riskedMixedEquivalentRecoverableMean));
    form.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT
      ));

      form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT
      ));

      form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
      ));

      form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT
      ));

      form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT
      ));

      form.setAssociatedCondensateRecoverableResources(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
          ));

      form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY
      ));

      form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY
      ));

      form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      ));

      form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY
      ));

      form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      ));

      form.setAssociatedCondensateRecoverableResources(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
          ));

      form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      ));
    }

    return form;
  }
}
