package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSumService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotalsRepository;

@ExtendWith(MockitoExtension.class)
class DiscoveryOilCaseVolumetricTotalsServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private OilCaseVolumetricTotalsRepository oilCaseVolumetricTotalsRepository;
  
  @Mock
  private ProbabilityDistributionSumService probabilityDistributionSumService;

  @InjectMocks
  private DiscoveryOilCaseVolumetricTotalsService discoveryOilCaseVolumetricTotalsService;

  @Test
  void submissionDetailToForm() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    
    var oilCaseVolumetricTotal = new OilCaseVolumetricTotal();
    oilCaseVolumetricTotal.setUseIndustryStandardConversion(true);
    oilCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    
    when(oilCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(oilCaseVolumetricTotal));
    
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );
    var recoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var oilEquivalent = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var associatedGas = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    )).thenReturn(oilInPlace);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(recoverableResources);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(associatedGas);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(oilEquivalent);

    var result = discoveryOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);
    
    assertThat(result.getHasAssociatedGasRecoverableResources()).isNull();
    assertThat(result.getUseIndustryStandardConversion()).isTrue();
    assertThat(result.getOilInPlace()).usingRecursiveComparison().isEqualTo(oilInPlace);
    assertThat(result.getOilRecoverableResources()).usingRecursiveComparison().isEqualTo(recoverableResources);
    assertThat(result.getAssociatedGasRecoverableResources()).usingRecursiveComparison().isEqualTo(associatedGas);
    assertThat(result.getOilEquivalentRecoverableResources()).usingRecursiveComparison().isEqualTo(oilEquivalent);
  }

  @Test
  void saveDiscoveryOilCaseVolumetricTotals() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = new OilCaseOutputsForm();
    var probDistribution = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setOilInPlace(probDistribution);
    form.setOilRecoverableResources(probDistribution);
    form.setAssociatedGasRecoverableResources(probDistribution);
    form.setOilEquivalentRecoverableResources(probDistribution);
    discoveryOilCaseVolumetricTotalsService.saveDiscoveryOilCaseVolumetricTotals(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            form.getOilInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);
  }

  @Test
  void saveDiscoveryOilCaseVolumetricTotals_useIndustryStandardConversion() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var oilCaseVolumetricTotal = new OilCaseVolumetricTotal();
    oilCaseVolumetricTotal.setUseIndustryStandardConversion(true);
    oilCaseVolumetricTotal.setSubmissionDetail(submissionDetail);

    when(oilCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(oilCaseVolumetricTotal));

    var form = new OilCaseOutputsForm();
    form.setUseIndustryStandardConversion(true);

    var instance = ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;
    var distribution = new ProbabilityDistribution();

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, instance))
        .thenReturn(Optional.of(distribution));

    when(probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForDiscovery(eq(distribution), anyMap()))
        .thenReturn(distribution);

    discoveryOilCaseVolumetricTotalsService.saveDiscoveryOilCaseVolumetricTotals(form, submissionDetail);

    verify(oilCaseVolumetricTotalsRepository, times(1)).save(oilCaseVolumetricTotal);
    verify(probabilityDistributionPersistenceService, times(1)).saveSummedProbabilityDistribution(distribution, instance, submissionDetail);
  }
}
