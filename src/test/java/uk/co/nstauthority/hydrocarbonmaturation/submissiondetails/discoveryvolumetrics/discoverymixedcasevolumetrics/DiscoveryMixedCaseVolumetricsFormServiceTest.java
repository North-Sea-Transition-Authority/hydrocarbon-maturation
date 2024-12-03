package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class DiscoveryMixedCaseVolumetricsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService;

  @InjectMocks
  private DiscoveryMixedCaseVolumetricsFormService discoveryMixedCaseVolumetricsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getMixedCaseOutputsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());

    when(discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(new MixedCaseOutput()));

    assertThat(discoveryMixedCaseVolumetricsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getRiskedOilEquivalentRecoverableMean().getProbabilityDistributionType(),
            MixedCaseOutputsForm::getHasAssociatedGasRecoverableResources,
            MixedCaseOutputsForm::getHasAssociatedCondensateRecoverableResources,
            MixedCaseOutputsForm::getUseIndustryStandardConversion
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getMixedCaseOutputsForm_existingDiscoveryEntity_thenPopulatedForm() {
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );
    var oilRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var associatedGas = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    );
    var gasRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var oilEquivalent = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var associatedCondensate = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY))
        .thenReturn(oilInPlace);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(oilRecoverableResources);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(associatedGas);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY))
        .thenReturn(gasInPlace);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(gasRecoverableResources);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(associatedCondensate);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(oilEquivalent);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
    mixedCaseOutput.setUseIndustryStandardConversion(true);
    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    when(discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    var result = discoveryMixedCaseVolumetricsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail);

    assertThat(result.getHasAssociatedCondensateRecoverableResources()).isTrue();
    assertThat(result.getUseIndustryStandardConversion()).isTrue();
    assertThat(result.getHasAssociatedGasRecoverableResources()).isNull();
    assertThat(result.getOilInPlace()).usingRecursiveComparison().isEqualTo(oilInPlace);
    assertThat(result.getOilRecoverableResources()).usingRecursiveComparison().isEqualTo(oilRecoverableResources);
    assertThat(result.getAssociatedGasRecoverableResources()).usingRecursiveComparison().isEqualTo(associatedGas);
    assertThat(result.getGasInPlace()).usingRecursiveComparison().isEqualTo(gasInPlace);
    assertThat(result.getGasRecoverableResources()).usingRecursiveComparison().isEqualTo(gasRecoverableResources);
    assertThat(result.getAssociatedCondensateRecoverableResources()).usingRecursiveComparison().isEqualTo(associatedCondensate);
    assertThat(result.getOilEquivalentRecoverableResources()).usingRecursiveComparison().isEqualTo(oilEquivalent);
  }
}