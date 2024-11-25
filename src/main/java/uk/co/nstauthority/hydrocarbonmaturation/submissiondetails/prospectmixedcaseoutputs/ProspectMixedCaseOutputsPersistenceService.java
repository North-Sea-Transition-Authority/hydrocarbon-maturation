package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

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
public class ProspectMixedCaseOutputsPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final MixedCaseOutputsRepository mixedCaseOutputsRepository;
  private final ProbabilityDistributionSumService probabilityDistributionSumService;

  @Autowired
  public ProspectMixedCaseOutputsPersistenceService(
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
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(
        mixedCaseOutputsForm.getHasAssociatedGasRecoverableResources());
    mixedCaseOutput.setHasAssociatedCondensateRecoverableResources(
        mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources());
    mixedCaseOutput.setUseIndustryStandardConversion(mixedCaseOutputsForm.getUseIndustryStandardConversion());

    mixedCaseOutputsRepository.save(mixedCaseOutput);

    if (BooleanUtils.isTrue(mixedCaseOutputsForm.getUseIndustryStandardConversion())) {
      calculateAndSaveIndustryStandardOilEquivalentResourcesForProspect(mixedCaseOutputsForm, reservoirSubmissionDetail);
    }
  }

  private void saveProbabilityDistributionFields(MixedCaseOutputsForm mixedCaseOutputsForm,
                                                 ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var oilInPlace = ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        mixedCaseOutputsForm.getOilInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilEquivalentRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        mixedCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var associatedGasRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT;
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
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasInPlace,
        mixedCaseOutputsForm.getGasInPlace(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var gasRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoverableResources,
        mixedCaseOutputsForm.getGasRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var associatedCondensateRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT;
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
          reservoirSubmissionDetail);
    }

    var oilRecoverableResources =
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        mixedCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var riskedOilEquivalentRecoverableMean =
        ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        riskedOilEquivalentRecoverableMean,
        mixedCaseOutputsForm.getRiskedOilEquivalentRecoverableMean(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }

  public Optional<MixedCaseOutput> getMixedCaseOutput(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return mixedCaseOutputsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  private void calculateAndSaveIndustryStandardOilEquivalentResourcesForProspect(
      MixedCaseOutputsForm mixedCaseOutputsForm,
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
        mixedCaseOutputsForm.getOilRecoverableResources(),
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
        mixedCaseOutputsForm.getGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
        mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
        mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources()
    );

    var summedDistribution = probabilityDistributionPersistenceService.getProbabilityDistribution(
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT
        )
        .orElse(new ProbabilityDistribution());

    probabilityDistributionPersistenceService.saveSummedProbabilityDistribution(
        probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForProspect(
            summedDistribution, distributionFormMap),
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT,
        reservoirSubmissionDetail
    );
  }

  private boolean hasAssociatedGasRecoverableResources(MixedCaseOutputsForm form) {
    return BooleanUtils.isTrue(form.getHasAssociatedGasRecoverableResources());
  }

  @Transactional
  public void deleteMixedCaseOutputForReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    mixedCaseOutputsRepository.deleteByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}
