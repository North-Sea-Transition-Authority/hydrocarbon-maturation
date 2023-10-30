package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycombinedcasevolumetrictotals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;

@ExtendWith(MockitoExtension.class)
class DiscoveryCombinedCaseVolTotalsServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository;

  @InjectMocks
  private DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService;

  @Test
  void submissionDetailToForm() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var combinedCaseVolumetricTotal = new CombinedCaseVolumetricTotal();
    combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
    combinedCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    when(combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(combinedCaseVolumetricTotal));
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );
    var oilRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var associatedGas = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    );
    var gasRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
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
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    )).thenReturn(oilInPlace);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(oilRecoverableResources);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(associatedGas);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    )).thenReturn(gasInPlace);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(gasRecoverableResources);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(associatedCondensate);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    )).thenReturn(oilEquivalent);

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getHasAssociatedCondensateRecoverableResources()).isTrue();

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getHasAssociatedGasRecoverableResources()).isNull();

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getOilInPlace()).usingRecursiveComparison().isEqualTo(oilInPlace);

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getOilRecoverableResources()).usingRecursiveComparison().isEqualTo(oilRecoverableResources);

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getAssociatedGasRecoverableResources()).usingRecursiveComparison().isEqualTo(associatedGas);

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getGasInPlace()).usingRecursiveComparison().isEqualTo(gasInPlace);

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getGasRecoverableResources()).usingRecursiveComparison().isEqualTo(gasRecoverableResources);

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getAssociatedCondensateRecoverableResources()).usingRecursiveComparison().isEqualTo(associatedCondensate);

    assertThat(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)
        .getOilEquivalentRecoverableResources()).usingRecursiveComparison().isEqualTo(oilEquivalent);
  }

  @Test
  void saveDiscoveryCombinedCaseVolumetricTotals() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var combinedCaseVolumetricTotal = new CombinedCaseVolumetricTotal();
    combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
    combinedCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    when(combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(combinedCaseVolumetricTotal));
    var form = new MixedCaseOutputsForm();
    form.setHasAssociatedCondensateRecoverableResources(true);
    var probDistribution = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setOilInPlace(probDistribution);
    form.setOilRecoverableResources(probDistribution);
    form.setAssociatedGasRecoverableResources(probDistribution);
    form.setGasInPlace(probDistribution);
    form.setGasRecoverableResources(probDistribution);
    form.setAssociatedCondensateRecoverableResources(probDistribution);
    form.setOilEquivalentRecoverableResources(probDistribution);
    discoveryCombinedCaseVolTotalsService.saveDiscoveryCombinedCaseVolumetricTotals(form, submissionDetail);

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

    verify(combinedCaseVolumetricTotalsRepository, times(1)).save(combinedCaseVolumetricTotal);
  }

  @Test
  void saveDiscoveryCombinedCaseVolumetricTotals_noAssociatedCondensate() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var combinedCaseVolumetricTotal = new CombinedCaseVolumetricTotal();
    combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(false);
    combinedCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    when(combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(combinedCaseVolumetricTotal));
    var form = new MixedCaseOutputsForm();
    form.setHasAssociatedCondensateRecoverableResources(false);
    var probDistribution = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setOilInPlace(probDistribution);
    form.setOilRecoverableResources(probDistribution);
    form.setAssociatedGasRecoverableResources(probDistribution);
    form.setGasInPlace(probDistribution);
    form.setGasRecoverableResources(probDistribution);
    form.setAssociatedCondensateRecoverableResources(probDistribution);
    form.setOilEquivalentRecoverableResources(probDistribution);
    discoveryCombinedCaseVolTotalsService.saveDiscoveryCombinedCaseVolumetricTotals(form, submissionDetail);

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

    verify(combinedCaseVolumetricTotalsRepository, times(1)).save(combinedCaseVolumetricTotal);
  }
}
