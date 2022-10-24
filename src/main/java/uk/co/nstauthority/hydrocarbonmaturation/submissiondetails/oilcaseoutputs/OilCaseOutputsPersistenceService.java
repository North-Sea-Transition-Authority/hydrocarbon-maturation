package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    if (oilCaseOutputsForm.getOilInPlace().getProbabilityDistributionType() != null) {
      var oilInPlace = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilInPlace,
          oilCaseOutputsForm.getOilInPlace(),
          submissionDetail
      );
    }

    if (oilCaseOutputsForm.getOilRecoverableResources().getProbabilityDistributionType() != null) {
      var oilRecoverableResources = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilRecoverableResources,
          oilCaseOutputsForm.getOilRecoverableResources(),
          submissionDetail
      );
    }
    var associatedGasRecoverableResources =
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES;
    if (oilCaseOutputsForm.getHasAssociatedGasRecoverableResources() != null
        && oilCaseOutputsForm.getHasAssociatedGasRecoverableResources()
        && oilCaseOutputsForm.getAssociatedGasRecoverableResources().getProbabilityDistributionType() != null) {
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
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

    if (oilCaseOutputsForm.getOilEquivalentRecoverableResources().getProbabilityDistributionType() != null) {
      var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilEquivalentRecoverableResources,
          oilCaseOutputsForm.getOilEquivalentRecoverableResources(),
          submissionDetail
      );
    }
  }

  public Optional<OilCaseOutput> getOilCaseOutput(SubmissionDetail submissionDetail) {
    return oilCaseOutputsRepository.findBySubmissionDetail(submissionDetail);
  }
}
