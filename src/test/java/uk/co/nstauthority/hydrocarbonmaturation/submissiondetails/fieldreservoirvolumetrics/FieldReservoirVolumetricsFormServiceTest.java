package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class FieldReservoirVolumetricsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @Test
  void getFieldReservoirVolumetricsForm() {
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(oilInPlace);

    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    assertThat(form.getOilInPlace()).isEqualTo(oilInPlace);
  }

  @Test
  void saveFieldReservoirVolumetricsForm_oilCase() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    var form = getForm();
    fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  private FieldReservoirVolumetricsForm getForm() {
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP90(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceForm.setP50(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceForm.setMean(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceForm.setP10(DecimalInput.createDecimalInput(1));

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setCustom(probabilityOfExceedanceForm);

    var form = new FieldReservoirVolumetricsForm();
    form.setOilInPlace(probabilityDistributionForm);

    return form;
  }
}