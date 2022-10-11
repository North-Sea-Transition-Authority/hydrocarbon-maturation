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

    oilCaseOutputsRepository.save(oilCaseOutput);
  }

  private void saveProbabilityDistributionFields(OilCaseOutputsForm oilCaseOutputsForm, SubmissionDetail submissionDetail) {
    if (oilCaseOutputsForm.getOilInPlace().getProbabilityDistributionType() != null) {
      var oilInPlace = ProbabilityDistributionFieldInstance.OIL_IN_PLACE;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilInPlace,
          oilCaseOutputsForm.getOilInPlace(),
          submissionDetail
      );
    }

    if (oilCaseOutputsForm.getOilRecoverableResources().getProbabilityDistributionType() != null) {
      var oilRecoverableResources = ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilRecoverableResources,
          oilCaseOutputsForm.getOilRecoverableResources(),
          submissionDetail
      );
    }

    if (oilCaseOutputsForm.getAssociatedGasRecoverableResources().getProbabilityDistributionType() != null) {
      var associatedGasRecoverableResources = ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          associatedGasRecoverableResources,
          oilCaseOutputsForm.getAssociatedGasRecoverableResources(),
          submissionDetail
      );
    }

    if (oilCaseOutputsForm.getOilEquivalentRecoverableResources().getProbabilityDistributionType() != null) {
      var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES;
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
