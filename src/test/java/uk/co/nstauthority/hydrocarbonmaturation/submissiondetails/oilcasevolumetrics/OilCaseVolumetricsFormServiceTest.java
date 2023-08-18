package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
class OilCaseVolumetricsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @InjectMocks
  private OilCaseVolumetricsFormService oilCaseVolumetricsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getOilCaseVolumetricsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());

    assertThat(oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(reservoirSubmissionDetail))
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
  void getOilCaseVolumetricsForm_existingProspectEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT))
        .thenReturn(probabilityDistributionForm);

    assertThat(oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getOilFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getOilFormationVolumeFactor().getConstant().getComments(),
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getOilRecoveryFactor().getConstant().getConstant(),
            form -> form.getOilRecoveryFactor().getConstant().getComments(),
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getConstant(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getComments(),
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
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments()
        );
  }

  @Test
  void getOilCaseVolumetricsForm_existingDiscoveryEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY))
        .thenReturn(probabilityDistributionForm);

    assertThat(oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(discoveryReservoirSubmissionDetail))
        .extracting(
            form -> form.getOilFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getOilFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getOilFormationVolumeFactor().getConstant().getComments(),
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getOilRecoveryFactor().getConstant().getConstant(),
            form -> form.getOilRecoveryFactor().getConstant().getComments(),
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getConstant(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getComments(),
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
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments()
        );
  }
}