package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class GasCaseOutputsPersistenceService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GasCaseOutputsRepository gasCaseOutputsRepository;

  public GasCaseOutputsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      GasCaseOutputsRepository gasCaseOutputsRepository) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.gasCaseOutputsRepository = gasCaseOutputsRepository;
  }

  @Transactional
  public void saveGasCaseOutputs(GasCaseOutputsForm gasCaseOutputsForm, SubmissionDetail submissionDetail) {

    var gasCaseOutput = getGasCaseOutput(submissionDetail).orElseGet(GasCaseOutput::new);
    gasCaseOutput.setSubmissionDetail(submissionDetail);
    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(
        gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources());

    if (gasCaseOutputsForm.getGasInPlace().getProbabilityDistributionType() != null) {
      var gasInPlace = ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          gasInPlace,
          gasCaseOutputsForm.getGasInPlace(),
          submissionDetail
      );
    }

    if (gasCaseOutputsForm.getGasRecoverableResources().getProbabilityDistributionType() != null) {
      var gasRecoverableResources = ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          gasRecoverableResources,
          gasCaseOutputsForm.getGasRecoverableResources(),
          submissionDetail
      );
    }
    var associatedCondensateRecoverableResources =
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES;
    if (gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources() != null
        && gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources()
        && gasCaseOutputsForm.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType() != null) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedCondensateRecoverableResources,
          gasCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
          submissionDetail
      );
    } else if (gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources() != null
        && !gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources()) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          associatedCondensateRecoverableResources,
          submissionDetail
      );
    }

    if (gasCaseOutputsForm.getOilEquivalentRecoverableResources().getProbabilityDistributionType() != null) {
      var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          oilEquivalentRecoverableResources,
          gasCaseOutputsForm.getOilEquivalentRecoverableResources(),
          submissionDetail
      );
    }

    gasCaseOutputsRepository.save(gasCaseOutput);
  }

  public Optional<GasCaseOutput> getGasCaseOutput(SubmissionDetail submissionDetail) {
    return gasCaseOutputsRepository.findBySubmissionDetail(submissionDetail);
  }
}
