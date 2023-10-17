package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
public class OilCaseOutputsPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final OilCaseOutputsRepository oilCaseOutputsRepository;

  @Autowired
  public OilCaseOutputsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      OilCaseOutputsRepository oilCaseOutputsRepository) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.oilCaseOutputsRepository = oilCaseOutputsRepository;
  }

  @Transactional
  public void saveOilCaseOutputs(OilCaseOutputsForm oilCaseOutputsForm, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    saveProbabilityDistributionFields(oilCaseOutputsForm, reservoirSubmissionDetail);

    var oilCaseOutput = getOilCaseOutput(reservoirSubmissionDetail).orElseGet(OilCaseOutput::new);

    oilCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    var mean = oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean().createBigDecimalOrNull();
    oilCaseOutput.setRiskedOilEquivalentRecoverableMean(mean);
    var hasAssociatedGasRecoverableResources = oilCaseOutputsForm.getHasAssociatedGasRecoverableResources();
    oilCaseOutput.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);

    oilCaseOutputsRepository.save(oilCaseOutput);
  }

  private void saveProbabilityDistributionFields(
      OilCaseOutputsForm oilCaseOutputsForm,
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var oilInPlace = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilInPlace = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        oilCaseOutputsForm.getOilInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilRecoverableResources = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilRecoverableResources = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        oilCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var associatedGasRecoverableResources =
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      associatedGasRecoverableResources =
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    }
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

    var oilEquivalentRecoverableResources =
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        oilCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }

  public Optional<OilCaseOutput> getOilCaseOutput(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilCaseOutputsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Transactional
  public void deleteOilCaseOutputForReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    oilCaseOutputsRepository.deleteByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}