package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @InjectMocks
  private OilCaseOutputsFormService oilCaseOutputsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getOilCaseOutputsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(new OilCaseOutput()));

    assertThat(oilCaseOutputsFormService.getOilCaseOutputsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            oilCaseOutputsForm -> oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean().asBigDecimal()
        ).containsExactly(
            null,
            null,
            null,
            null,
            Optional.empty()
        );
  }

  @Test
  void getOilCaseOutputsForm_existingProspectEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);

    OilCaseOutput oilCaseOutput = new OilCaseOutput();
    oilCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);
    when(oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));

    assertThat(oilCaseOutputsFormService.getOilCaseOutputsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getComments(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getCustom().getConstant(),
            form -> form.getOilRecoverableResources().getCustom().getComments(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getCustom().getConstant(),
            form -> form.getOilInPlace().getCustom().getComments(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getCustom().getConstant(),
            form -> form.getAssociatedGasRecoverableResources().getCustom().getComments(),
            oilCaseOutputsForm -> oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean().asBigDecimal().get()
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
            BigDecimal.ONE
        );
  }

  @Test
  void getOilCaseOutputsForm_existingDiscoveryEntity_thenPopulatedForm() {
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
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);

    OilCaseOutput oilCaseOutput = new OilCaseOutput();
    oilCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);
    when(oilCaseOutputsPersistenceService.getOilCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));

    assertThat(oilCaseOutputsFormService.getOilCaseOutputsForm(discoveryReservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getComments(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getCustom().getConstant(),
            form -> form.getOilRecoverableResources().getCustom().getComments(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getCustom().getConstant(),
            form -> form.getOilInPlace().getCustom().getComments(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getCustom().getConstant(),
            form -> form.getAssociatedGasRecoverableResources().getCustom().getComments(),
            oilCaseOutputsForm -> oilCaseOutputsForm.getRiskedOilEquivalentRecoverableMean().asBigDecimal().get()
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
            BigDecimal.ONE
        );
  }
}