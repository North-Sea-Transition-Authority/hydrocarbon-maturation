package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSumService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class DiscoveryMixedCaseVolumetricsPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final MixedCaseOutputsRepository mixedCaseOutputsRepository;
  private final ProbabilityDistributionSumService probabilityDistributionSumService;

  @Autowired
  public DiscoveryMixedCaseVolumetricsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      MixedCaseOutputsRepository mixedCaseOutputsRepository,
      ProbabilityDistributionSumService probabilityDistributionSumService
  ) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.mixedCaseOutputsRepository = mixedCaseOutputsRepository;
    this.probabilityDistributionSumService = probabilityDistributionSumService;
  }

  @Transactional
  public void saveMixedCaseOutputs(MixedCaseOutputsForm mixedCaseOutputsForm,
                                   ReservoirSubmissionDetail reservoirSubmissionDetail) {
    saveProbabilityDistributionFields(mixedCaseOutputsForm, reservoirSubmissionDetail);

    var mixedCaseOutput = getMixedCaseOutput(reservoirSubmissionDetail).orElseGet(MixedCaseOutput::new);

    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    mixedCaseOutput.setHasAssociatedCondensateRecoverableResources(
        mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources());
    mixedCaseOutput.setUseIndustryStandardConversion(mixedCaseOutputsForm.getUseIndustryStandardConversion());

    mixedCaseOutputsRepository.save(mixedCaseOutput);

    if (BooleanUtils.isTrue(mixedCaseOutputsForm.getUseIndustryStandardConversion())) {
      calculateAndSaveIndustryStandardOilEquivalentResources(mixedCaseOutputsForm, reservoirSubmissionDetail);
    }
  }

  public Optional<MixedCaseOutput> getMixedCaseOutput(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return mixedCaseOutputsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  private void saveProbabilityDistributionFields(MixedCaseOutputsForm mixedCaseOutputsForm,
                                                 ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var oilInPlace = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        mixedCaseOutputsForm.getOilInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        mixedCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var gasInPlace = ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasInPlace,
        mixedCaseOutputsForm.getGasInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var gasRecoverableResources = ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoverableResources,
        mixedCaseOutputsForm.getGasRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var associatedCondensateRecoverableResources =
          ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY;
    if (BooleanUtils.isTrue(mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources())) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedCondensateRecoverableResources,
          mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
          submissionDetail,
          reservoirSubmissionDetail
      );
    } else if (BooleanUtils.isFalse(mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          associatedCondensateRecoverableResources,
          reservoirSubmissionDetail
      );
    }

    var oilRecoverableResources = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        mixedCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }

  private void calculateAndSaveIndustryStandardOilEquivalentResources(
      MixedCaseOutputsForm mixedCaseOutputsForm,
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var instance =
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getOilRecoverableResources(),
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources()
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
