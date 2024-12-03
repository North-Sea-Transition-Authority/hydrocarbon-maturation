package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private OilCaseOutputsRepository oilCaseOutputsRepository;

  @Mock
  private ProbabilityDistributionSumService probabilityDistributionSumService;

  @InjectMocks
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void saveOilCaseOutputs_whenGivenAForm_whenProspect_verifyCreatedEntity() {
    var form = new OilCaseOutputsForm();
    form.setOilInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setRiskedOilEquivalentRecoverableMean(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setHasAssociatedGasRecoverableResources(true);

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
            form.getOilInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT,
            form.getRiskedOilEquivalentRecoverableMean(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
  }

  @Test
  void saveGasCaseOutputs_whenUseIndustryStandard_verifyDoCalculation() {
    var form = new OilCaseOutputsForm();
    form.setUseIndustryStandardConversion(true);

    var instance = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT;

    var existingDistribution = new ProbabilityDistribution();

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(
        reservoirSubmissionDetail,
        instance
    ))
        .thenReturn(Optional.of(existingDistribution));

    when(probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForProspect(eq(existingDistribution), anyMap()))
        .thenReturn(existingDistribution);

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, reservoirSubmissionDetail);

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
    verify(probabilityDistributionPersistenceService).saveSummedProbabilityDistribution(existingDistribution, instance, reservoirSubmissionDetail);
  }

  @Test
  void saveGasCaseOutputs_whenNotUseIndustryStandard_verifyNotDoCalculation() {
    var form = new OilCaseOutputsForm();
    form.setUseIndustryStandardConversion(false);

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionSumService, never())
        .calculateIndustryStandardOilEquivalentResourcesForProspect(any(), any());

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
  }

  @Test
  void saveOilCaseOutputs_whenGivenAForm_whenDiscovery_verifyCreatedEntity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var form = new OilCaseOutputsForm();
    form.setOilInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setHasAssociatedGasRecoverableResources(true);

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            form.getOilInPlace(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedGasRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);
  }

  @Test
  void saveOilCaseOutputs_whenGivenAForm_whenDiscovery_whenUseIndustryStandardConversion_verifyCreatedEntity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var form = new OilCaseOutputsForm();
    form.setUseIndustryStandardConversion(true);

    var oilCaseOutput = new OilCaseOutput();
    oilCaseOutput.setUseIndustryStandardConversion(true);
    oilCaseOutput.setReservoirSubmissionDetail(discoveryReservoirSubmissionDetail);

    when(oilCaseOutputsRepository.findByReservoirSubmissionDetail(discoveryReservoirSubmissionDetail)).thenReturn(Optional.of(oilCaseOutput));

    var instance = ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY;
    var distribution = new ProbabilityDistribution();

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(discoveryReservoirSubmissionDetail, instance))
        .thenReturn(Optional.of(distribution));

    when(probabilityDistributionSumService.calculateIndustryStandardOilEquivalentResourcesForDiscovery(eq(distribution), anyMap()))
        .thenReturn(distribution);

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, discoveryReservoirSubmissionDetail);

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
    verify(probabilityDistributionPersistenceService, times(1)).saveSummedProbabilityDistribution(distribution, instance, discoveryReservoirSubmissionDetail);
  }

  @Test
  void saveOilCaseOutputs_whenGivenAFormAndOptionalFieldFalse_verifyCreatedEntity() {
    var form = new OilCaseOutputsForm();
    form.setOilInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setRiskedOilEquivalentRecoverableMean(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setHasAssociatedGasRecoverableResources(false);

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
            form.getOilInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT,
            form.getRiskedOilEquivalentRecoverableMean(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
  }

  @Test
  void saveOilCaseOutputs_whenGivenEmptyForm_verifyCreatedEntity() {
    var form = new OilCaseOutputsForm();

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
            form.getOilInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT,
            form.getRiskedOilEquivalentRecoverableMean(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
  }
}