package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

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
  public void saveMixedCaseOutputs(MixedCaseOutputsForm mixedCaseOutputsForm, SubmissionDetail submissionDetail) {
    saveProbabilityDistributionFields(mixedCaseOutputsForm, submissionDetail);

    var mixedCaseOutput = getMixedCaseOutput(submissionDetail).orElseGet(MixedCaseOutput::new);

    mixedCaseOutput.setSubmissionDetail(submissionDetail);
    var mean = mixedCaseOutputsForm.getRiskedOilEquivalentRecoverableMean().createBigDecimalOrNull();
    var hasAssociatedGasRecoverableResources = mixedCaseOutputsForm.getHasAssociatedGasRecoverableResources();
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(mean);

    mixedCaseOutputsRepository.save(mixedCaseOutput);
  }

  private void saveProbabilityDistributionFields(MixedCaseOutputsForm mixedCaseOutputsForm, SubmissionDetail submissionDetail) {
    if (mixedCaseOutputsForm.getOilInPlace().getProbabilityDistributionType() != null) {
      var oilInPlace = ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          oilInPlace,
          mixedCaseOutputsForm.getOilInPlace(),
          submissionDetail
      );
    }

    if (mixedCaseOutputsForm.getOilEquivalentRecoverableResources().getProbabilityDistributionType() != null) {
      var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          oilEquivalentRecoverableResources,
          mixedCaseOutputsForm.getOilEquivalentRecoverableResources(),
          submissionDetail
      );
    }

    var associatedGasRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES;

    if (hasAssociatedGasRecoverableResources(mixedCaseOutputsForm)
        && mixedCaseOutputsForm.getAssociatedGasRecoverableResources().getProbabilityDistributionType() != null) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedGasRecoverableResources,
          mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
          submissionDetail
      );
    } else if (BooleanUtils.isFalse(mixedCaseOutputsForm.getHasAssociatedGasRecoverableResources())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          associatedGasRecoverableResources,
          submissionDetail);
    }

    if (mixedCaseOutputsForm.getGasInPlace().getProbabilityDistributionType() != null) {
      var gasInPlace = ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          gasInPlace,
          mixedCaseOutputsForm.getGasInPlace(),
          submissionDetail
      );
    }

    if (mixedCaseOutputsForm.getGasRecoverableResources().getProbabilityDistributionType() != null) {
      var gasRecoverableResources =
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          gasRecoverableResources,
          mixedCaseOutputsForm.getGasRecoverableResources(),
          submissionDetail
      );
    }

    if (mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType() != null) {
      var associatedCondensateRecoverableResources =
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedCondensateRecoverableResources,
          mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
          submissionDetail
      );
    }

    if (mixedCaseOutputsForm.getOilRecoverableResources().getProbabilityDistributionType() != null) {
      var oilRecoverableResources =
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          oilRecoverableResources,
          mixedCaseOutputsForm.getOilRecoverableResources(),
          submissionDetail
      );
    }
  }

  public Optional<MixedCaseOutput> getMixedCaseOutput(SubmissionDetail submissionDetail) {
    return mixedCaseOutputsRepository.findBySubmissionDetail(submissionDetail);
  }

  private boolean hasAssociatedGasRecoverableResources(MixedCaseOutputsForm form) {
    return form.getHasAssociatedGasRecoverableResources() != null
        && form.getHasAssociatedGasRecoverableResources();
  }
}
