package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSumService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class OilCaseOutputsPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final OilCaseOutputsRepository oilCaseOutputsRepository;
  private final ProbabilityDistributionSumService probabilityDistributionSumService;

  @Autowired
  public OilCaseOutputsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      OilCaseOutputsRepository oilCaseOutputsRepository,
      ProbabilityDistributionSumService probabilityDistributionSumService
  ) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.oilCaseOutputsRepository = oilCaseOutputsRepository;
    this.probabilityDistributionSumService = probabilityDistributionSumService;
  }

  public Optional<OilCaseOutput> getOilCaseOutput(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilCaseOutputsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Transactional
  public void saveOilCaseOutputs(OilCaseOutputsForm oilCaseOutputsForm, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    saveProbabilityDistributionFields(oilCaseOutputsForm, reservoirSubmissionDetail);
    var oilCaseOutput = getOilCaseOutput(reservoirSubmissionDetail).orElseGet(OilCaseOutput::new);
    oilCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    oilCaseOutput.setUseIndustryStandardConversion(oilCaseOutputsForm.getUseIndustryStandardConversion());

    if (reservoirSubmissionDetail.getSubmissionDetail().isProspectSubmission()) {
      oilCaseOutput.setHasAssociatedGasRecoverableResources(oilCaseOutputsForm.getHasAssociatedGasRecoverableResources());

      if (BooleanUtils.isTrue(oilCaseOutputsForm.getUseIndustryStandardConversion())) {
        calculateAndSaveIndustryStandardOilEquivalentResourcesForProspect(oilCaseOutputsForm, reservoirSubmissionDetail);
      }
    }

    oilCaseOutputsRepository.save(oilCaseOutput);

    if (reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()
        && BooleanUtils.isTrue(oilCaseOutputsForm.getUseIndustryStandardConversion())) {
      calculateAndSaveIndustryStandardOilEquivalentResourcesForDiscovery(oilCaseOutputsForm, reservoirSubmissionDetail);
    }
  }

  @Transactional
  public void deleteOilCaseOutputForReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    oilCaseOutputsRepository.deleteByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  private void calculateAndSaveIndustryStandardOilEquivalentResourcesForProspect(
      OilCaseOutputsForm oilCaseOutputsForm,
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var instance =
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT;

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
        oilCaseOutputsForm.getOilRecoverableResources(),
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
        oilCaseOutputsForm.getAssociatedGasRecoverableResources()
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
      OilCaseOutputsForm oilCaseOutputsForm,
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var instance =
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        oilCaseOutputsForm.getOilRecoverableResources(),
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        oilCaseOutputsForm.getAssociatedGasRecoverableResources()
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

  private void saveProbabilityDistributionFields(
      OilCaseOutputsForm oilCaseOutputsForm,
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var oilInPlace = resourceType == ResourceType.PROSPECT
        ? ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT
        : ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY;

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        oilCaseOutputsForm.getOilInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilRecoverableResources = resourceType == ResourceType.PROSPECT
        ? ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT
        : ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        oilCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    if (resourceType == ResourceType.PROSPECT) {
      var riskedOilEquivalentRecoverableMean =
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          riskedOilEquivalentRecoverableMean,
          oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean(),
          submissionDetail,
          reservoirSubmissionDetail
      );
    }

    var associatedGasRecoverableResources = resourceType == ResourceType.PROSPECT
        ? ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
        : ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY;

    if (BooleanUtils.isTrue(oilCaseOutputsForm.getHasAssociatedGasRecoverableResources())
        || resourceType == ResourceType.DISCOVERY) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedGasRecoverableResources,
          oilCaseOutputsForm.getAssociatedGasRecoverableResources(),
          submissionDetail,
          reservoirSubmissionDetail
      );
    } else if (BooleanUtils.isFalse(oilCaseOutputsForm.getHasAssociatedGasRecoverableResources())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
            associatedGasRecoverableResources,
            reservoirSubmissionDetail
      );
    }

    var oilEquivalentRecoverableResources = resourceType == ResourceType.PROSPECT
        ? ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        : ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        oilCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }


}