package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotalsRepository;

@Service
public class DiscoveryGasCaseVolumetricTotalsService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GasCaseVolumetricTotalsRepository gasCaseVolumetricTotalsRepository;
  private final ProbabilityDistributionSumService probabilityDistributionSumService;

  @Autowired
  public DiscoveryGasCaseVolumetricTotalsService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      GasCaseVolumetricTotalsRepository gasCaseVolumetricTotalsRepository,
      ProbabilityDistributionSumService probabilityDistributionSumService
  ) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.gasCaseVolumetricTotalsRepository = gasCaseVolumetricTotalsRepository;
    this.probabilityDistributionSumService = probabilityDistributionSumService;
  }

  public GasCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new GasCaseOutputsForm();

    var gasCaseVolumetricTotal = getGasCaseVolumetricTotal(submissionDetail);

    gasCaseVolumetricTotal.ifPresent(caseVolumetricTotal -> {
      form.setHasAssociatedCondensateRecoverableResources(caseVolumetricTotal.getHasAssociatedCondensateRecoverableResources());
      form.setUseIndustryStandardConversion(caseVolumetricTotal.getUseIndustryStandardConversion());
    });

    form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    ));

    form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    form.setAssociatedCondensateRecoverableResources(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
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
  public void saveDiscoveryGasCaseVolumetricTotals(GasCaseOutputsForm gasCaseOutputsForm, SubmissionDetail submissionDetail) {
    var gasCaseVolumetricTotal = getGasCaseVolumetricTotal(submissionDetail).orElseGet(GasCaseVolumetricTotal::new);
    gasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    gasCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(
        gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources());
    gasCaseVolumetricTotal.setUseIndustryStandardConversion(gasCaseOutputsForm.getUseIndustryStandardConversion());
    gasCaseVolumetricTotalsRepository.save(gasCaseVolumetricTotal);
    saveProbabilityDistributionFields(gasCaseOutputsForm, submissionDetail);

    if (BooleanUtils.isTrue(gasCaseOutputsForm.getUseIndustryStandardConversion())) {
      calculateAndSaveIndustryStandardOilEquivalentResources(gasCaseOutputsForm, submissionDetail);
    }
  }

  @Transactional
  public void saveDiscoveryGasCaseVolumetricTotals(GasCaseVolumetricTotal gasCaseVolumetricTotal) {
    gasCaseVolumetricTotalsRepository.save(gasCaseVolumetricTotal);
  }

  public Optional<GasCaseVolumetricTotal> getGasCaseVolumetricTotal(SubmissionDetail submissionDetail) {
    return gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail);
  }

  @Transactional
  public void deleteGasCaseVolumetricTotalsForSubmissionDetail(SubmissionDetail submissionDetail) {
    gasCaseVolumetricTotalsRepository.deleteBySubmissionDetail(submissionDetail);
  }

  private void saveProbabilityDistributionFields(
      GasCaseOutputsForm gasCaseOutputsForm,
      SubmissionDetail submissionDetail) {
    var gasInPlace = ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasInPlace,
        gasCaseOutputsForm.getGasInPlace(),
        submissionDetail
    );

    var gasRecoverableResources = ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoverableResources,
        gasCaseOutputsForm.getGasRecoverableResources(),
        submissionDetail
    );

    var associatedCondensateRecoverableResources =
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY;
    if (BooleanUtils.isTrue(gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources())) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          associatedCondensateRecoverableResources,
          gasCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
          submissionDetail
      );
    } else if (BooleanUtils.isFalse(gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          associatedCondensateRecoverableResources,
          submissionDetail
      );
    }

    var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        gasCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail
    );
  }

  private void calculateAndSaveIndustryStandardOilEquivalentResources(
      GasCaseOutputsForm gasCaseOutputsForm,
      SubmissionDetail submissionDetail
  ) {
    var instance =
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        gasCaseOutputsForm.getGasRecoverableResources(),
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
        gasCaseOutputsForm.getAssociatedCondensateRecoverableResources()
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
