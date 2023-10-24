package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class DiscoveryMixedCaseVolumetricsPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final MixedCaseOutputsRepository mixedCaseOutputsRepository;

  @Autowired
  public DiscoveryMixedCaseVolumetricsPersistenceService(
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
    var hasAssociatedGasRecoverableResources = mixedCaseOutputsForm.getHasAssociatedGasRecoverableResources();
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);

    mixedCaseOutputsRepository.save(mixedCaseOutput);
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

    var associatedGasRecoverableResources =
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
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
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        associatedCondensateRecoverableResources,
        mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilRecoverableResources = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
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
    return BooleanUtils.isTrue(form.getHasAssociatedGasRecoverableResources());
  }

  @Transactional
  public void deleteMixedCaseOutputForReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    mixedCaseOutputsRepository.deleteByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}
