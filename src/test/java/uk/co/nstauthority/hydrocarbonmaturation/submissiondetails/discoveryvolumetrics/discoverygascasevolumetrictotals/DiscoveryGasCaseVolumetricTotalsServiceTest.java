package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotalsRepository;

@ExtendWith(MockitoExtension.class)
class DiscoveryGasCaseVolumetricTotalsServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private GasCaseVolumetricTotalsRepository gasCaseVolumetricTotalsRepository;

  @Mock
  private ProbabilityDistributionSumService probabilityDistributionSumService;

  @InjectMocks
  private DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;

  @Test
  void submissionDetailToForm() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseVolumetricTotal.setUseIndustryStandardConversion(true);
    gasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(gasCaseVolumetricTotal));
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    );
    var recoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var oilEquivalent = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var associatedCondensate = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    )).thenReturn(gasInPlace);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(recoverableResources);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(associatedCondensate);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(oilEquivalent);

    var result = discoveryGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);

    assertThat(result.getHasAssociatedCondensateRecoverableResources()).isTrue();
    assertThat(result.getUseIndustryStandardConversion()).isTrue();
    assertThat(result.getGasInPlace()).usingRecursiveComparison().isEqualTo(gasInPlace);
    assertThat(result.getGasRecoverableResources()).usingRecursiveComparison().isEqualTo(recoverableResources);
    assertThat(result.getAssociatedCondensateRecoverableResources()).usingRecursiveComparison().isEqualTo(associatedCondensate);
    assertThat(result.getOilEquivalentRecoverableResources()).usingRecursiveComparison().isEqualTo(oilEquivalent);
  }

  @Test
  void saveDiscoveryGasCaseVolumetricTotals() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseVolumetricTotal.setUseIndustryStandardConversion(false);
    gasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(gasCaseVolumetricTotal));
    var form = new GasCaseOutputsForm();
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setUseIndustryStandardConversion(false);
    var probDistribution = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setGasInPlace(probDistribution);
    form.setGasRecoverableResources(probDistribution);
    form.setAssociatedCondensateRecoverableResources(probDistribution);
    form.setOilEquivalentRecoverableResources(probDistribution);
    discoveryGasCaseVolumetricTotalsService.saveDiscoveryGasCaseVolumetricTotals(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            form.getGasInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(gasCaseVolumetricTotalsRepository, times(1)).save(gasCaseVolumetricTotal);
  }

  @Test
  void saveDiscoveryGasCaseVolumetricTotals_noAssociatedCondensate() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(false);
    gasCaseVolumetricTotal.setUseIndustryStandardConversion(false);
    gasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(gasCaseVolumetricTotal));
    var form = new GasCaseOutputsForm();
    form.setHasAssociatedCondensateRecoverableResources(false);
    form.setUseIndustryStandardConversion(false);
    var probDistribution = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setGasInPlace(probDistribution);
    form.setGasRecoverableResources(probDistribution);
    form.setAssociatedCondensateRecoverableResources(probDistribution);
    form.setOilEquivalentRecoverableResources(probDistribution);
    discoveryGasCaseVolumetricTotalsService.saveDiscoveryGasCaseVolumetricTotals(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            form.getGasInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(gasCaseVolumetricTotalsRepository, times(1)).save(gasCaseVolumetricTotal);
  }

  @Test
  void saveDiscoveryGasCaseVolumetricTotals_useIndustryStandardConversion() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setUseIndustryStandardConversion(true);
    gasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);

    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(gasCaseVolumetricTotal));

    var form = new GasCaseOutputsForm();
    form.setUseIndustryStandardConversion(true);

    var instance = ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;
    var distribution = new ProbabilityDistribution();

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, instance))
        .thenReturn(Optional.of(distribution));

    when(probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForDiscovery(eq(distribution), anyMap()))
        .thenReturn(distribution);

    discoveryGasCaseVolumetricTotalsService.saveDiscoveryGasCaseVolumetricTotals(form, submissionDetail);

    verify(gasCaseVolumetricTotalsRepository, times(1)).save(gasCaseVolumetricTotal);
    verify(probabilityDistributionPersistenceService, times(1)).saveSummedProbabilityDistribution(distribution, instance, submissionDetail);
  }
}
