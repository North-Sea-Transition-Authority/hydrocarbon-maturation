package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverymixedcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class DiscoveryMixedCaseOutputsFormService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final DiscoveryMixedCaseOutputsPersistenceService discoveryMixedCaseOutputsPersistenceService;

  @Autowired
  public DiscoveryMixedCaseOutputsFormService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      DiscoveryMixedCaseOutputsPersistenceService discoveryMixedCaseOutputsPersistenceService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.discoveryMixedCaseOutputsPersistenceService = discoveryMixedCaseOutputsPersistenceService;
  }

  public MixedCaseOutputsForm getMixedCaseOutputsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private MixedCaseOutputsForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new MixedCaseOutputsForm();
    var mixedCaseOutputs = discoveryMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail);
    var hasAssociatedGasRecoverableResources = mixedCaseOutputs
        .map(MixedCaseOutput::getHasAssociatedGasRecoverableResources)
        .orElse(null);
    var riskedMixedEquivalentRecoverableMean = mixedCaseOutputs
        .map(MixedCaseOutput::getRiskedOilEquivalentRecoverableMean)
        .orElse(null);

    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(riskedMixedEquivalentRecoverableMean));
    form.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);

    form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    ));

    form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    ));

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

    return form;
  }
}
