package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class MixedCaseOutputsPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final MixedCaseOutputsRepository mixedCaseOutputsRepository;

  @Autowired
  public MixedCaseOutputsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      MixedCaseOutputsRepository mixedCaseOutputsRepository) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.mixedCaseOutputsRepository = mixedCaseOutputsRepository;
  }

  @Transactional
  public void saveMixedCaseOutputs(MixedCaseOutputsForm mixedCaseOutputsForm,
                                   ReservoirSubmissionDetail reservoirSubmissionDetail) {
    saveProbabilityDistributionFields(mixedCaseOutputsForm, reservoirSubmissionDetail);

    var mixedCaseOutput = getMixedCaseOutput(reservoirSubmissionDetail).orElseGet(MixedCaseOutput::new);

    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    var mean = mixedCaseOutputsForm.getRiskedOilEquivalentRecoverableMean().createBigDecimalOrNull();
    var hasAssociatedGasRecoverableResources = mixedCaseOutputsForm.getHasAssociatedGasRecoverableResources();
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(mean);

    mixedCaseOutputsRepository.save(mixedCaseOutput);
  }

  private void saveProbabilityDistributionFields(MixedCaseOutputsForm mixedCaseOutputsForm,
                                                 ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var oilInPlace = ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilInPlace = ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        mixedCaseOutputsForm.getOilInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilEquivalentRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        mixedCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var associatedGasRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      associatedGasRecoverableResources =
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    if (hasAssociatedGasRecoverableResources(mixedCaseOutputsForm)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedGasRecoverableResources,
          mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
          submissionDetail,
          reservoirSubmissionDetail
      );
    } else if (BooleanUtils.isFalse(mixedCaseOutputsForm.getHasAssociatedGasRecoverableResources())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          associatedGasRecoverableResources,
          reservoirSubmissionDetail);
    }

    var gasInPlace = ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      gasInPlace = ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasInPlace,
        mixedCaseOutputsForm.getGasInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var gasRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      gasRecoverableResources = ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoverableResources,
        mixedCaseOutputsForm.getGasRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var associatedCondensateRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      associatedCondensateRecoverableResources =
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        associatedCondensateRecoverableResources,
        mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilRecoverableResources = ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        mixedCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }

  public Optional<MixedCaseOutput> getMixedCaseOutput(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return mixedCaseOutputsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  private boolean hasAssociatedGasRecoverableResources(MixedCaseOutputsForm form) {
    return form.getHasAssociatedGasRecoverableResources() != null
        && form.getHasAssociatedGasRecoverableResources();
  }
}
