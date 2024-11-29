package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotalsRepository;

@Service
public class DiscoveryOilCaseVolumetricTotalsService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final OilCaseVolumetricTotalsRepository oilCaseVolumetricTotalsRepository;
  private final ProbabilityDistributionSumService probabilityDistributionSumService;

  @Autowired
  public DiscoveryOilCaseVolumetricTotalsService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      OilCaseVolumetricTotalsRepository oilCaseVolumetricTotalsRepository,
      ProbabilityDistributionSumService probabilityDistributionSumService
  ) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.oilCaseVolumetricTotalsRepository = oilCaseVolumetricTotalsRepository;
    this.probabilityDistributionSumService = probabilityDistributionSumService;
  }

  public OilCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new OilCaseOutputsForm();

    var useIndustryStandardConversion = getOilCaseVolumetricTotal(submissionDetail)
        .map(OilCaseVolumetricTotal::getUseIndustryStandardConversion)
        .orElse(null);

    form.setUseIndustryStandardConversion(useIndustryStandardConversion);

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

    form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    return form;
  }

  public Optional<OilCaseVolumetricTotal> getOilCaseVolumetricTotal(SubmissionDetail submissionDetail) {
    return oilCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail);
  }

  @Transactional
  public void saveDiscoveryOilCaseVolumetricTotals(OilCaseOutputsForm oilCaseOutputsForm, SubmissionDetail submissionDetail) {
    var oilCaseVolumetricTotals = getOilCaseVolumetricTotal(submissionDetail).orElse(new OilCaseVolumetricTotal());
    oilCaseVolumetricTotals.setSubmissionDetail(submissionDetail);
    oilCaseVolumetricTotals.setUseIndustryStandardConversion(oilCaseOutputsForm.getUseIndustryStandardConversion());

    oilCaseVolumetricTotalsRepository.save(oilCaseVolumetricTotals);

    saveProbabilityDistributionFields(oilCaseOutputsForm, submissionDetail);

    if (BooleanUtils.isTrue(oilCaseOutputsForm.getUseIndustryStandardConversion())) {
      calculateAndSaveIndustryStandardOilEquivalentResources(oilCaseOutputsForm, submissionDetail);
    }
  }

  private void saveProbabilityDistributionFields(
      OilCaseOutputsForm oilCaseOutputsForm,
      SubmissionDetail submissionDetail) {

    var oilInPlace = ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        oilCaseOutputsForm.getOilInPlace(),
        submissionDetail
    );

    var oilRecoverableResources = ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        oilCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail
    );

    var associatedGasRecoverableResources =
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        associatedGasRecoverableResources,
        oilCaseOutputsForm.getAssociatedGasRecoverableResources(),
        submissionDetail
    );

    var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        oilCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail
    );
  }

  private void calculateAndSaveIndustryStandardOilEquivalentResources(
      OilCaseOutputsForm oilCaseOutputsForm,
      SubmissionDetail submissionDetail
  ) {
    var instance =
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;

    var distributionFormMap = Map.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        oilCaseOutputsForm.getOilRecoverableResources(),
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        oilCaseOutputsForm.getAssociatedGasRecoverableResources()
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
