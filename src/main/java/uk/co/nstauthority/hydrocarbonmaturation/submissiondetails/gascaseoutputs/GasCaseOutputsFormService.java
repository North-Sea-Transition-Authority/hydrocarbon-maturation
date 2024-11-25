package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class GasCaseOutputsFormService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  public GasCaseOutputsFormService(ProbabilityDistributionFormService probabilityDistributionFormService,
                                   GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
  }

  GasCaseOutputsForm getGasCaseOutputsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirSubmissionDetailToForm(reservoirSubmissionDetail);
  }

  private GasCaseOutputsForm reservoirSubmissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new GasCaseOutputsForm();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var gasCaseOutput = gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail);

    gasCaseOutput.ifPresent(caseOutput -> {
      form.setHasAssociatedCondensateRecoverableResources(caseOutput.getHasAssociatedCondensateRecoverableResources());
      form.setUseIndustryStandardConversion(caseOutput.getUseIndustryStandardConversion());
    });

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT
      ));

      form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT
      ));

      form.setAssociatedCondensateRecoverableResources(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
          ));

      form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
      ));

      form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      ));

      form.setAssociatedCondensateRecoverableResources(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
          ));

      form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      ));
    }

    return form;
  }
}
