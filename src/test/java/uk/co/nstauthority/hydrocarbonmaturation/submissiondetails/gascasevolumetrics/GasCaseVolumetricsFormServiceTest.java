package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
class GasCaseVolumetricsFormServiceTest {

  @Mock
  private GasCaseVolumetricsFormValidator formValidator;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @InjectMocks
  private GasCaseVolumetricsFormService gasCaseVolumetricsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getGasCaseVolumetricsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(reservoirSubmissionDetail), any())).thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());

    assertThat(gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getGasFormationVolumeFactor().getProbabilityDistributionType()
        )
        .containsExactly(
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getGasCaseVolumetricsForm_existingProspectEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(reservoirSubmissionDetail), any())).thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT))
        .thenReturn(probabilityDistributionForm);

    assertThat(gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getConstant(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getComments(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getConstant().getConstant(),
            form -> form.getGasRecoveryFactor().getConstant().getComments(),
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getGasHydrocarbonSaturation().getConstant().getConstant(),
            form -> form.getGasHydrocarbonSaturation().getConstant().getComments(),
            form -> form.getGasFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getGasFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getGasFormationVolumeFactor().getConstant().getComments()
            )
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
  void getGasCaseVolumetricsForm_existingDiscoveryEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(discoveryReservoirSubmissionDetail), any())).thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoveryReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY))
        .thenReturn(probabilityDistributionForm);

    assertThat(gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(discoveryReservoirSubmissionDetail))
        .extracting(
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getConstant(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getComments(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getConstant().getConstant(),
            form -> form.getGasRecoveryFactor().getConstant().getComments(),
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getGasHydrocarbonSaturation().getConstant().getConstant(),
            form -> form.getGasHydrocarbonSaturation().getConstant().getComments(),
            form -> form.getGasFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getGasFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getGasFormationVolumeFactor().getConstant().getComments()
        )
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