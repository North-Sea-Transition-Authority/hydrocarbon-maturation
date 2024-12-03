package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals;

import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSumService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;

@Service
public class DiscoveryCombinedCaseVolTotalsService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository;
  private final ProbabilityDistributionSumService probabilityDistributionSumService;

  @Autowired
  public DiscoveryCombinedCaseVolTotalsService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository,
      ProbabilityDistributionSumService probabilityDistributionSumService
  ) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.combinedCaseVolumetricTotalsRepository = combinedCaseVolumetricTotalsRepository;
    this.probabilityDistributionSumService = probabilityDistributionSumService;
  }

  public MixedCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new MixedCaseOutputsForm();

    var combinedCaseVolumetricTotal = getCombinedCaseVolumetricTotal(submissionDetail);

    combinedCaseVolumetricTotal.ifPresent(caseVolumetricTotal -> {
      form.setHasAssociatedCondensateRecoverableResources(caseVolumetricTotal.getHasAssociatedCondensateRecoverableResources());
      form.setUseIndustryStandardConversion(caseVolumetricTotal.getUseIndustryStandardConversion());
    });

    form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    ));

    form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    ));

    form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    form.setAssociatedCondensateRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    return form;
  }

  @Transactional
  public void saveDiscoveryCombinedCaseVolumetricTotals(MixedCaseOutputsForm mixedCaseOutputsForm,
                                                        SubmissionDetail submissionDetail) {
    var combinedCaseVolumetricTotal = getCombinedCaseVolumetricTotal(submissionDetail)
        .orElseGet(CombinedCaseVolumetricTotal::new);
    combinedCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(
        mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources());
    combinedCaseVolumetricTotal.setUseIndustryStandardConversion(mixedCaseOutputsForm.getUseIndustryStandardConversion());
    combinedCaseVolumetricTotalsRepository.save(combinedCaseVolumetricTotal);
    saveProbabilityDistributionFields(mixedCaseOutputsForm, submissionDetail);

    if (BooleanUtils.isTrue(mixedCaseOutputsForm.getUseIndustryStandardConversion())) {
      calculateAndSaveIndustryStandardOilEquivalentResources(mixedCaseOutputsForm, submissionDetail);
    }
  }

  @Transactional
  public void saveDiscoveryCombinedCaseVolumetricTotals(CombinedCaseVolumetricTotal combinedCaseVolumetricTotal) {
    combinedCaseVolumetricTotalsRepository.save(combinedCaseVolumetricTotal);
  }

  public Optional<CombinedCaseVolumetricTotal> getCombinedCaseVolumetricTotal(SubmissionDetail submissionDetail) {
    return combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail);
  }

  @Transactional
  public void deleteCombinedCaseVolumetricTotalsForSubmissionDetail(SubmissionDetail submissionDetail) {
    combinedCaseVolumetricTotalsRepository.deleteBySubmissionDetail(submissionDetail);
  }

  private void saveProbabilityDistributionFields(MixedCaseOutputsForm mixedCaseOutputsForm, SubmissionDetail submissionDetail) {
    var oilInPlace = ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        mixedCaseOutputsForm.getOilInPlace(),
        submissionDetail
    );

    var oilRecoverableResources = ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        mixedCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail
    );

    var associatedGasRecoverableResources =
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        associatedGasRecoverableResources,
        mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
        submissionDetail
    );

    var gasInPlace = ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasInPlace,
        mixedCaseOutputsForm.getGasInPlace(),
        submissionDetail
    );

    var gasRecoverableResources = ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoverableResources,
        mixedCaseOutputsForm.getGasRecoverableResources(),
        submissionDetail
    );

    var associatedCondensateRecoverableResources =
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY;
    if (BooleanUtils.isTrue(mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources())) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedCondensateRecoverableResources,
          mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
          submissionDetail
      );
    } else if (BooleanUtils.isFalse(mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          associatedCondensateRecoverableResources,
          submissionDetail
      );
    }

    var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        mixedCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail
    );
  }

  private void calculateAndSaveIndustryStandardOilEquivalentResources(
      MixedCaseOutputsForm mixedCaseOutputsForm,
      SubmissionDetail submissionDetail
  ) {
    var instance =
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getOilRecoverableResources(),
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
        mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources()
    );

    var summedDistribution = probabilityDistributionPersistenceService.getProbabilityDistribution(
            submissionDetail,
            instance
        )
        .orElse(new ProbabilityDistribution());

    probabilityDistributionPersistenceService.saveSummedProbabilityDistribution(
        probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForDiscovery(
            summedDistribution, distributionFormMap),
        instance,
        submissionDetail
    );
  }
}
