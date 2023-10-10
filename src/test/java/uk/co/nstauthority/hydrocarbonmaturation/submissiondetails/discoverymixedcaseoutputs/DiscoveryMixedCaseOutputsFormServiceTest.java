package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverymixedcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
class DiscoveryMixedCaseOutputsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private DiscoveryMixedCaseOutputsPersistenceService discoveryMixedCaseOutputsPersistenceService;

  @InjectMocks
  private DiscoveryMixedCaseOutputsFormService discoveryMixedCaseOutputsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
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
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());

    when(discoveryMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(new MixedCaseOutput()));

    assertThat(discoveryMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getRiskedOilEquivalentRecoverableMean().asBigDecimal(),
            MixedCaseOutputsForm::getHasAssociatedGasRecoverableResources)
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            Optional.empty(),
            null
        );
  }

  @Test
  void getMixedCaseOutputsForm_existingDiscoveryEntity_thenPopulatedForm() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    when(discoveryMixedCaseOutputsPersistenceService.getMixedCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    assertThat(discoveryMixedCaseOutputsFormService.getMixedCaseOutputsForm(discoveryReservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getComments(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getCustom().getConstant(),
            form -> form.getAssociatedGasRecoverableResources().getCustom().getComments(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getCustom().getConstant(),
            form -> form.getOilInPlace().getCustom().getComments(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom().getConstant(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom().getComments(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getCustom().getConstant(),
            form -> form.getOilRecoverableResources().getCustom().getComments(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getCustom().getConstant(),
            form -> form.getGasInPlace().getCustom().getComments(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getCustom().getConstant(),
            form -> form.getGasRecoverableResources().getCustom().getComments(),
            form -> form.getRiskedOilEquivalentRecoverableMean().asBigDecimal().get(),
            MixedCaseOutputsForm::getHasAssociatedGasRecoverableResources)
        .containsExactly(
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            BigDecimal.ONE,
            true
        );
  }
}