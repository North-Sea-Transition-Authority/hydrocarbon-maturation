package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetricparameters;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class FieldOilVolumetricParametersServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @InjectMocks
  private FieldOilVolumetricParametersService fieldOilVolumetricParametersService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getOilVolumetricsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD))
        .thenReturn(new ProbabilityDistributionForm());

    assertThat(fieldOilVolumetricParametersService.getOilVolumetricsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getOilFormationVolumeFactor().getProbabilityDistributionType()
        ).containsExactly(
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getOilCaseVolumetricsForm_existingEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD))
        .thenReturn(probabilityDistributionForm);

    assertThat(fieldOilVolumetricParametersService.getOilVolumetricsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getOilFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getOilFormationVolumeFactor().getConstant().getComments(),
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getOilRecoveryFactor().getConstant().getConstant(),
            form -> form.getOilRecoveryFactor().getConstant().getComments(),
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getOilHydrocarbonSaturation().getCustom().getConstant(),
            form -> form.getOilHydrocarbonSaturation().getCustom().getComments(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getConstant().getConstant(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getConstant().getComments())
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
  void saveOilCaseVolumetrics_whenGivenAForm_verifyCreatedEntity() {
    var form = new OilCaseVolumetricParametersForm();
    form.setOilHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilFormationVolumeFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setOilRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setAssociatedGasOilRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    fieldOilVolumetricParametersService.saveOilVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD,
            form.getOilHydrocarbonSaturation(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD,
            form.getOilRecoveryFactor(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT,
            form.getAssociatedGasOilRatioLifeOfField(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD,
            form.getOilFormationVolumeFactor(),
            submissionDetail,
            reservoirSubmissionDetail);
  }
}
