package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

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
  public void saveOilCaseOutputs(OilCaseOutputsForm oilCaseOutputsForm, SubmissionDetail submissionDetail) {
    saveProbabilityDistributionFields(oilCaseOutputsForm, submissionDetail);

    var oilCaseOutput = getOilCaseOutput(submissionDetail).orElseGet(OilCaseOutput::new);

    oilCaseOutput.setSubmissionDetail(submissionDetail);
    var mean = oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean().createBigDecimalOrNull();
    oilCaseOutput.setRiskedOilEquivalentRecoverableMean(mean);
    var hasAssociatedGasRecoverableResources = oilCaseOutputsForm.getHasAssociatedGasRecoverableResources();
    oilCaseOutput.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);

    oilCaseOutputsRepository.save(oilCaseOutput);
  }

  private void saveProbabilityDistributionFields(OilCaseOutputsForm oilCaseOutputsForm, SubmissionDetail submissionDetail) {
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var oilInPlace = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilInPlace = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        oilCaseOutputsForm.getOilInPlace(),
        submissionDetail
    );

    var oilRecoverableResources = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilRecoverableResources = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        oilCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail
    );

    var associatedGasRecoverableResources =
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      associatedGasRecoverableResources =
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    if (oilCaseOutputsForm.getHasAssociatedGasRecoverableResources() != null
        && oilCaseOutputsForm.getHasAssociatedGasRecoverableResources()) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedGasRecoverableResources,
          oilCaseOutputsForm.getAssociatedGasRecoverableResources(),
          submissionDetail
      );
    } else if (oilCaseOutputsForm.getHasAssociatedGasRecoverableResources() != null
        && !oilCaseOutputsForm.getHasAssociatedGasRecoverableResources()) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
            associatedGasRecoverableResources,
            submissionDetail
      );
    }

    var oilEquivalentRecoverableResources =
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        oilCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail
    );
  }

  public Optional<OilCaseOutput> getOilCaseOutput(SubmissionDetail submissionDetail) {
    return oilCaseOutputsRepository.findBySubmissionDetail(submissionDetail);
  }
}
