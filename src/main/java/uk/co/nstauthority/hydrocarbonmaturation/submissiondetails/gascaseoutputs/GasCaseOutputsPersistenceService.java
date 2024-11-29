package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSumService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class GasCaseOutputsPersistenceService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GasCaseOutputsRepository gasCaseOutputsRepository;
  private final ProbabilityDistributionSumService probabilityDistributionSumService;

  public GasCaseOutputsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      GasCaseOutputsRepository gasCaseOutputsRepository,
      ProbabilityDistributionSumService probabilityDistributionSumService
  ) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.gasCaseOutputsRepository = gasCaseOutputsRepository;
    this.probabilityDistributionSumService = probabilityDistributionSumService;
  }

  @Transactional
  public void saveGasCaseOutputs(GasCaseOutputsForm gasCaseOutputsForm, ReservoirSubmissionDetail reservoirSubmissionDetail) {

    var gasCaseOutput = getGasCaseOutput(reservoirSubmissionDetail).orElseGet(GasCaseOutput::new);
    gasCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(
        gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources());
    gasCaseOutput.setUseIndustryStandardConversion(gasCaseOutputsForm.getUseIndustryStandardConversion());

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var gasInPlace = resourceType == ResourceType.PROSPECT
        ? ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT
        : ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY;

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasInPlace,
        gasCaseOutputsForm.getGasInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var gasRecoverableResources = resourceType == ResourceType.PROSPECT
        ? ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT
        : ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY;

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoverableResources,
        gasCaseOutputsForm.getGasRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var associatedCondensateRecoverableResources = resourceType == ResourceType.PROSPECT
        ? ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
        : ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY;

    if (BooleanUtils.isTrue(gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources())) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedCondensateRecoverableResources,
          gasCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
          submissionDetail,
          reservoirSubmissionDetail
      );
    } else if (BooleanUtils.isFalse(gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          associatedCondensateRecoverableResources,
          reservoirSubmissionDetail
      );
    }

    var oilEquivalentRecoverableResources = resourceType == ResourceType.PROSPECT
        ? ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        : ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        gasCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    gasCaseOutputsRepository.save(gasCaseOutput);

    if (BooleanUtils.isTrue(gasCaseOutputsForm.getUseIndustryStandardConversion())) {
      if (resourceType == ResourceType.PROSPECT) {
        calculateAndSaveIndustryStandardOilEquivalentResourcesForProspect(gasCaseOutputsForm, reservoirSubmissionDetail);
      } else if (resourceType == ResourceType.DISCOVERY) {
        calculateAndSaveIndustryStandardOilEquivalentResourcesForDiscovery(gasCaseOutputsForm, reservoirSubmissionDetail);
      }
    }
  }

  public Optional<GasCaseOutput> getGasCaseOutput(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return gasCaseOutputsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Transactional
  public void deleteGasCaseOutputForReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    gasCaseOutputsRepository.deleteByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  private void calculateAndSaveIndustryStandardOilEquivalentResourcesForProspect(
      GasCaseOutputsForm gasCaseOutputsForm,
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var instance =
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT;

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
        gasCaseOutputsForm.getGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
        gasCaseOutputsForm.getAssociatedCondensateRecoverableResources()
    );

    var summedDistribution = probabilityDistributionPersistenceService.getProbabilityDistribution(
            reservoirSubmissionDetail,
            instance
        )
        .orElse(new ProbabilityDistribution());

    probabilityDistributionPersistenceService.saveSummedProbabilityDistribution(
        probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForProspect(
            summedDistribution, distributionFormMap),
        instance,
        reservoirSubmissionDetail
    );
  }

  private void calculateAndSaveIndustryStandardOilEquivalentResourcesForDiscovery(
      GasCaseOutputsForm gasCaseOutputsForm,
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var instance =
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        gasCaseOutputsForm.getGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
        gasCaseOutputsForm.getAssociatedCondensateRecoverableResources()
    );

    var summedDistribution = probabilityDistributionPersistenceService.getProbabilityDistribution(
            reservoirSubmissionDetail,
            instance
        )
        .orElse(new ProbabilityDistribution());

    probabilityDistributionPersistenceService.saveSummedProbabilityDistribution(
        probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForDiscovery(
            summedDistribution, distributionFormMap),
        instance,
        reservoirSubmissionDetail
    );
  }
}
