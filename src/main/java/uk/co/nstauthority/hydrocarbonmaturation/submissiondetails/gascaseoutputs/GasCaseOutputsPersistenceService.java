package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class GasCaseOutputsPersistenceService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GasCaseOutputsRepository gasCaseOutputsRepository;

  public GasCaseOutputsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      GasCaseOutputsRepository gasCaseOutputsRepository) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.gasCaseOutputsRepository = gasCaseOutputsRepository;
  }

  @Transactional
  public void saveGasCaseOutputs(GasCaseOutputsForm gasCaseOutputsForm, ReservoirSubmissionDetail reservoirSubmissionDetail) {

    var gasCaseOutput = getGasCaseOutput(reservoirSubmissionDetail).orElseGet(GasCaseOutput::new);
    gasCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(
        gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources());

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var gasInPlace = ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      gasInPlace = ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasInPlace,
        gasCaseOutputsForm.getGasInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var gasRecoverableResources =
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      gasRecoverableResources = ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoverableResources,
        gasCaseOutputsForm.getGasRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var associatedCondensateRecoverableResources =
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      associatedCondensateRecoverableResources =
          ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    if (gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources() != null
        && gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources()) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedCondensateRecoverableResources,
          gasCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
          submissionDetail,
          reservoirSubmissionDetail
      );
    } else if (gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources() != null
        && !gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources()) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          associatedCondensateRecoverableResources,
          reservoirSubmissionDetail
      );
    }

    var oilEquivalentRecoverableResources =
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        gasCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    gasCaseOutputsRepository.save(gasCaseOutput);
  }

  public Optional<GasCaseOutput> getGasCaseOutput(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return gasCaseOutputsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Transactional
  public void deleteGasCaseOutputForReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    gasCaseOutputsRepository.deleteByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}
