package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class FieldReservoirVolumetricsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private FieldReservoirVolumetricsRepository fieldReservoirVolumetricsRepository;

  @InjectMocks
  private FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;

  ArgumentCaptor<FieldReservoirVolumetrics> volumetricsCaptor = ArgumentCaptor.forClass(FieldReservoirVolumetrics.class);

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private FieldReservoirVolumetrics fieldReservoirVolumetrics;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    fieldReservoirVolumetrics = new FieldReservoirVolumetrics();
  }

  @Test
  void getFieldReservoirVolumetricsForm_oilCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);

    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(oilInPlace);

    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(Boolean.FALSE);

    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    assertThat(form.getOilInPlace()).isEqualTo(oilInPlace);
    assertThat(form.getInitiallyInPlaceInformationCorrect()).isNull();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getFieldReservoirVolumetricsForm_oilCase_reservoirWasCarriedForward(SubmissionType submissionType) {
    // WHEN fdp, fdpa or csr
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);

    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);

    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(oilInPlace);

    // AND reservoir carried forward
    fieldReservoirVolumetrics.setInitiallyInPlaceCorrect(Boolean.TRUE);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(Boolean.TRUE);
    when(fieldReservoirVolumetricsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(fieldReservoirVolumetrics));

    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    assertThat(form.getOilInPlace()).isEqualTo(oilInPlace);

    // THEN initially in place answer is populated
    assertThat(form.getInitiallyInPlaceInformationCorrect()).isTrue();
  }

  @Test
  void getFieldReservoirVolumetricsForm_oilCase_notFdpFdpaOrCsr_reservoirNotCarriedForward() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);

    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(oilInPlace);

    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    assertThat(form.getOilInPlace()).isEqualTo(oilInPlace);
    assertThat(form.getInitiallyInPlaceInformationCorrect()).isNull();
  }

  @Test
  void getFieldReservoirVolumetricsForm_gasCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);

    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    )).thenReturn(gasInPlace);

    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(Boolean.FALSE);

    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    assertThat(form.getGasInPlace()).isEqualTo(gasInPlace);
    assertThat(form.getInitiallyInPlaceInformationCorrect()).isNull();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getFieldReservoirVolumetricsForm_gasCase_reservoirWasCarriedForward(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);

    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);

    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    )).thenReturn(gasInPlace);

    fieldReservoirVolumetrics.setInitiallyInPlaceCorrect(Boolean.TRUE);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(Boolean.TRUE);
    when(fieldReservoirVolumetricsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(fieldReservoirVolumetrics));

    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    assertThat(form.getGasInPlace()).isEqualTo(gasInPlace);
    assertThat(form.getInitiallyInPlaceInformationCorrect()).isTrue();
  }

  @Test
  void getFieldReservoirVolumetricsForm_combinedCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    )).thenReturn(gasInPlace);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(oilInPlace);

    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(Boolean.FALSE);

    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    assertThat(form.getGasInPlace()).isEqualTo(gasInPlace);
    assertThat(form.getOilInPlace()).isEqualTo(oilInPlace);
    assertThat(form.getInitiallyInPlaceInformationCorrect()).isNull();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getFieldReservoirVolumetricsForm_combinedCase_reservoirWasCarriedForward(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    )).thenReturn(gasInPlace);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(oilInPlace);

    fieldReservoirVolumetrics.setInitiallyInPlaceCorrect(Boolean.TRUE);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(Boolean.TRUE);
    when(fieldReservoirVolumetricsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(fieldReservoirVolumetrics));

    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    assertThat(form.getGasInPlace()).isEqualTo(gasInPlace);
    assertThat(form.getOilInPlace()).isEqualTo(oilInPlace);
    assertThat(form.getInitiallyInPlaceInformationCorrect()).isTrue();
  }

  @Test
  void saveFieldReservoirVolumetricsForm_oilCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(false);

    var form = getForm();
    fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);

    verify(fieldReservoirVolumetricsRepository, never())
        .save(any());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldReservoirVolumetricsForm_oilCase_reservoirWasCarriedForward(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);

    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(true);
    when(fieldReservoirVolumetricsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var form = getForm();
    form.setInitiallyInPlaceInformationCorrect(Boolean.TRUE);
    fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);

    verify(fieldReservoirVolumetricsRepository, times(1))
        .save(volumetricsCaptor.capture());
    assertThat(volumetricsCaptor.getValue())
        .extracting(
            FieldReservoirVolumetrics::getInitiallyInPlaceCorrect,
            FieldReservoirVolumetrics::getReservoirSubmissionDetail
        )
        .isEqualTo(List.of(
            Boolean.TRUE,
            reservoirSubmissionDetail
        ));
  }

  @Test
  void saveFieldReservoirVolumetricsForm_gasCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);

    var form = getForm();
    fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);

    verify(fieldReservoirVolumetricsRepository, never())
        .save(any());
  }

  @Test
  void saveFieldReservoirVolumetricsForm_gasCase_reservoirCarriedForward() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_FDPA);
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(true);

    var form = getForm();
    form.setInitiallyInPlaceInformationCorrect(Boolean.TRUE);
    fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);

    verify(fieldReservoirVolumetricsRepository, times(1))
        .save(volumetricsCaptor.capture());
    assertThat(volumetricsCaptor.getValue())
        .extracting(
            FieldReservoirVolumetrics::getInitiallyInPlaceCorrect,
            FieldReservoirVolumetrics::getReservoirSubmissionDetail
        )
        .isEqualTo(List.of(
            Boolean.TRUE,
            reservoirSubmissionDetail
        ));
  }

  @Test
  void saveFieldReservoirVolumetricsForm_combinedCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    var form = getForm();
    fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);

    verify(fieldReservoirVolumetricsRepository, never())
        .save(any());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldReservoirVolumetricsForm_combinedCase_reservoirCarriedForward(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(true);

    var form = getForm();
    form.setInitiallyInPlaceInformationCorrect(Boolean.TRUE);
    fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            eq(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD),
            any(ProbabilityDistributionForm.class),
            eq(reservoirSubmissionDetail)
        );

    verify(fieldReservoirVolumetricsRepository, times(1))
        .save(volumetricsCaptor.capture());
    assertThat(volumetricsCaptor.getValue())
        .extracting(
            FieldReservoirVolumetrics::getInitiallyInPlaceCorrect,
            FieldReservoirVolumetrics::getReservoirSubmissionDetail
        )
        .isEqualTo(List.of(
            Boolean.TRUE,
            reservoirSubmissionDetail
        ));
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