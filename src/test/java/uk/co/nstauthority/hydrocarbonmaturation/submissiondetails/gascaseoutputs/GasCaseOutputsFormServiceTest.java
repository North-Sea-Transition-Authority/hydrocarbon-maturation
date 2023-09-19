package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class GasCaseOutputsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  @InjectMocks
  private GasCaseOutputsFormService gasCaseOutputsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getGasCaseOutputsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(new GasCaseOutput()));

    assertThat(gasCaseOutputsFormService.getGasCaseOutputsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            GasCaseOutputsForm::getHasAssociatedCondensateRecoverableResources
        ).containsExactly(
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getGasCaseOutputsForm_existingProspectEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);

    GasCaseOutput gasCaseOutput = new GasCaseOutput();
    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
    when(gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));

    assertThat(gasCaseOutputsFormService.getGasCaseOutputsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getComments(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getCustom().getConstant(),
            form -> form.getGasRecoverableResources().getCustom().getComments(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getCustom().getConstant(),
            form -> form.getGasInPlace().getCustom().getComments(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom().getConstant(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom().getComments(),
            GasCaseOutputsForm::getHasAssociatedCondensateRecoverableResources
        )
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
            true
        );
  }

  @Test
  void getGasCaseOutputsForm_existingDiscoveryEntity_thenPopulatedForm() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);

    GasCaseOutput gasCaseOutput = new GasCaseOutput();
    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
    when(gasCaseOutputsPersistenceService.getGasCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));

    assertThat(gasCaseOutputsFormService.getGasCaseOutputsForm(discoveryReservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getComments(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getCustom().getConstant(),
            form -> form.getGasRecoverableResources().getCustom().getComments(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getCustom().getConstant(),
            form -> form.getGasInPlace().getCustom().getComments(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom().getConstant(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom().getComments(),
            GasCaseOutputsForm::getHasAssociatedCondensateRecoverableResources
        )
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
            true
        );
  }
}