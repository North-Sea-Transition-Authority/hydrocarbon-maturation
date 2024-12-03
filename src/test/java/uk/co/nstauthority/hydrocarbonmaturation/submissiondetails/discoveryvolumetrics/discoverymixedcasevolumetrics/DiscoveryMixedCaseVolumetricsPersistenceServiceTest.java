package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSumService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class DiscoveryMixedCaseVolumetricsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private MixedCaseOutputsRepository mixedCaseOutputsRepository;

  @Mock
  private ProbabilityDistributionSumService probabilityDistributionSumService;

  @InjectMocks
  private DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void saveMixedCaseOutputs_whenGivenAForm_verifyCreatedEntity() {
    var form = new MixedCaseOutputsForm();
    var probDistribution = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setGasRecoverableResources(probDistribution);
    form.setGasInPlace(probDistribution);
    form.setAssociatedGasRecoverableResources(probDistribution);
    form.setOilInPlace(probDistribution);
    form.setOilRecoverableResources(probDistribution);
    form.setOilEquivalentRecoverableResources(probDistribution);
    form.setAssociatedCondensateRecoverableResources(probDistribution);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setUseIndustryStandardConversion(false);

    discoveryMixedCaseVolumetricsPersistenceService.saveMixedCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            form.getOilInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            form.getGasInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).save(any(MixedCaseOutput.class));
  }

  @Test
  void saveMixedCaseOutputs_whenEmptyForm_verifyCreatedEntity() {
    var form = new MixedCaseOutputsForm();

    discoveryMixedCaseVolumetricsPersistenceService.saveMixedCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            form.getOilInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            form.getGasInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).save(any(MixedCaseOutput.class));
  }

  @Test
  void getMixedCaseOutput_verifyMethodCall() {
    discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void saveMixedCaseOutputs_whenUseIndustryStandardConversion_verifyCreatedEntity() {
    var form = new MixedCaseOutputsForm();
    form.setUseIndustryStandardConversion(true);

    var mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setUseIndustryStandardConversion(true);
    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);

    when(mixedCaseOutputsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail)).thenReturn(Optional.of(mixedCaseOutput));

    var instance = ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;
    var distribution = new ProbabilityDistribution();

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(reservoirSubmissionDetail, instance))
        .thenReturn(Optional.of(distribution));

    when(probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForDiscovery(eq(distribution), anyMap()))
        .thenReturn(distribution);

    discoveryMixedCaseVolumetricsPersistenceService.saveMixedCaseOutputs(form, reservoirSubmissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).save(any(MixedCaseOutput.class));
    verify(probabilityDistributionPersistenceService, times(1)).saveSummedProbabilityDistribution(distribution, instance, reservoirSubmissionDetail);
  }
}