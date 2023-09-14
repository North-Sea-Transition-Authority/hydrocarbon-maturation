package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class FieldGasVolumetricsServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @InjectMocks
  private FieldGasVolumetricsService fieldGasVolumetricsService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getGasVolumetricsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD))
        .thenReturn(new ProbabilityDistributionForm());

    assertThat(fieldGasVolumetricsService.getGasVolumetricsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getGasFormationVolumeFactor().getProbabilityDistributionType()
        ).containsExactly(
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getGasCaseVolumetricsForm_existingEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD))
        .thenReturn(probabilityDistributionForm);

    assertThat(fieldGasVolumetricsService.getGasVolumetricsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getGasFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getGasFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getGasFormationVolumeFactor().getConstant().getComments(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getConstant().getConstant(),
            form -> form.getGasRecoveryFactor().getConstant().getComments(),
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getGasHydrocarbonSaturation().getCustom().getConstant(),
            form -> form.getGasHydrocarbonSaturation().getCustom().getComments(),
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getConstant(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getComments())
        .containsExactly(
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments()
        );
  }

  @Test
  void saveGasCaseVolumetrics_whenGivenAForm_verifyCreatedEntity() {
    var form = new GasCaseVolumetricsForm();
    form.setGasHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setGasFormationVolumeFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setCondensateGasRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    fieldGasVolumetricsService.saveGasVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD,
            form.getGasHydrocarbonSaturation(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD,
            form.getGasFormationVolumeFactor(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD,
            form.getGasRecoveryFactor(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT,
            form.getCondensateGasRatioLifeOfField(),
            submissionDetail,
            reservoirSubmissionDetail);
  }
}
