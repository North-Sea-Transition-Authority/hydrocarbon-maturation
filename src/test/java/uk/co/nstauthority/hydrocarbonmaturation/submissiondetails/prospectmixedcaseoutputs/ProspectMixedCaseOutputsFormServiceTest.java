package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

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
class ProspectMixedCaseOutputsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService;

  @InjectMocks
  private ProspectMixedCaseOutputsFormService prospectMixedCaseOutputsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getMixedCaseOutputsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());

    when(prospectMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(new MixedCaseOutput()));

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail))
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
            MixedCaseOutputsForm::getHasAssociatedCondensateRecoverableResources)
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
            null
        );
  }

  @Test
  void getMixedCaseOutputsForm_existingProspectEntity_thenPopulatedForm() {
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT
    );
    var oilRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT
    );
    var associatedGas = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
    );
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT
    );
    var gasRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT
    );
    var oilEquivalent = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
    );
    var associatedCondensate = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT))
        .thenReturn(oilInPlace);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(oilRecoverableResources);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(associatedGas);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT))
        .thenReturn(gasInPlace);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(gasRecoverableResources);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(associatedCondensate);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(oilEquivalent);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    when(prospectMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getHasAssociatedCondensateRecoverableResources()).isTrue();

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getHasAssociatedGasRecoverableResources()).isNull();

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getOilInPlace()).usingRecursiveComparison().isEqualTo(oilInPlace);

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getOilRecoverableResources()).usingRecursiveComparison().isEqualTo(oilRecoverableResources);

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getAssociatedGasRecoverableResources()).usingRecursiveComparison().isEqualTo(associatedGas);

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getGasInPlace()).usingRecursiveComparison().isEqualTo(gasInPlace);

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getGasRecoverableResources()).usingRecursiveComparison().isEqualTo(gasRecoverableResources);

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getAssociatedCondensateRecoverableResources()).usingRecursiveComparison().isEqualTo(associatedCondensate);

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)
        .getOilEquivalentRecoverableResources()).usingRecursiveComparison().isEqualTo(oilEquivalent);
  }
}