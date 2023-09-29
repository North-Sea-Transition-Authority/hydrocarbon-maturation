package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class DrillStemTestResultPersistenceServiceTest {

  @Mock
  private DrillStemTestResultRepository drillStemTestResultRepository;

  @Mock
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @InjectMocks
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  private static ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void saveDrillStemTestResult_whenGivenAForm_verifyCreatedEntity() {
    var drillStemTest = mock(DrillStemTest.class);

    var form = getFilledDrillStemTestResultForm();

    var randomId = UUID.randomUUID();

    when(drillStemTestResultRepository.findById(randomId)).thenReturn(Optional.empty());

    var drillStemTestResultArgumentCaptor = ArgumentCaptor.forClass(DrillStemTestResult.class);
    drillStemTestResultPersistenceService.saveDrillStemTestResult(form, drillStemTest, randomId);

    verify(drillStemTestResultRepository, times(1)).save(drillStemTestResultArgumentCaptor.capture());
    var savedDrillStemTestResult = drillStemTestResultArgumentCaptor.getValue();
    assertThat(savedDrillStemTestResult)
        .extracting(
            DrillStemTestResult::getDrillStemTest,
            DrillStemTestResult::getCommentsInterpretation,
            DrillStemTestResult::getBhfp,
            DrillStemTestResult::getBottomDepth,
            DrillStemTestResult::getFlowDuration,
            DrillStemTestResult::getFormationName,
            DrillStemTestResult::getGasFlowrate,
            DrillStemTestResult::getTopDepth,
            DrillStemTestResult::getOilFlowrate,
            DrillStemTestResult::getWaterFlowrate,
            DrillStemTestResult::getWhfp,
            DrillStemTestResult::getTestNumber)
        .containsExactly(
            drillStemTest,
            "comment",
            form.getBhfp().createIntegerOrNull(),
            form.getBottomDepth().createBigDecimalOrNull(),
            form.getFlowDuration().createBigDecimalOrNull(),
            form.getFormationName(),
            form.getGasFlowrate().createBigDecimalOrNull(),
            form.getTopDepth().createBigDecimalOrNull(),
            form.getOilFlowrate().createBigDecimalOrNull(),
            form.getWaterFlowrate().createBigDecimalOrNull(),
            form.getWhfp().createIntegerOrNull(),
            form.getTestNumber()
        );
  }

  @Test
  void saveDrillStemTestResult_whenGivenEmptyForm_verifyCreatedEntity() {
    var drillStemTest = mock(DrillStemTest.class);

    var form = new DrillStemTestResultForm();

    var randomId = UUID.randomUUID();

    when(drillStemTestResultRepository.findById(randomId)).thenReturn(Optional.empty());

    var drillStemTestResultArgumentCaptor = ArgumentCaptor.forClass(DrillStemTestResult.class);
    drillStemTestResultPersistenceService.saveDrillStemTestResult(form, drillStemTest, randomId);

    verify(drillStemTestResultRepository, times(1)).save(drillStemTestResultArgumentCaptor.capture());
    var savedDrillStemTestResult = drillStemTestResultArgumentCaptor.getValue();
    assertThat(savedDrillStemTestResult)
        .hasAllNullFieldsOrPropertiesExcept("drillStemTest");
    assertThat(savedDrillStemTestResult.getDrillStemTest()).isEqualTo(drillStemTest);
  }

  @Test
  void saveDrillStemTestResult_whenGivenEmptyForm_whenEntity_verifyCreatedEntity() {
    var drillStemTest = mock(DrillStemTest.class);

    var form = new DrillStemTestResultForm();

    var randomId = UUID.randomUUID();

    when(drillStemTestResultRepository.findById(randomId)).thenReturn(Optional.of(new DrillStemTestResult()));

    var drillStemTestResultArgumentCaptor = ArgumentCaptor.forClass(DrillStemTestResult.class);
    drillStemTestResultPersistenceService.saveDrillStemTestResult(form, drillStemTest, randomId);

    verify(drillStemTestResultRepository, times(1)).save(drillStemTestResultArgumentCaptor.capture());
    var savedDrillStemTestResult = drillStemTestResultArgumentCaptor.getValue();
    assertThat(savedDrillStemTestResult)
        .hasAllNullFieldsOrPropertiesExcept("drillStemTest");
    assertThat(savedDrillStemTestResult.getDrillStemTest()).isEqualTo(drillStemTest);
  }

  @Test
  void getDrillStemTestBySubmissionDetailOrThrow() {
    var drillStemTest = mock(DrillStemTest.class);

    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(drillStemTest));
    var result = drillStemTestResultPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail);

    assertThat(drillStemTest).isEqualTo(result);
  }

  @Test
  void getDrillStemTestBySubmissionDetailOrThrow_verifyThrown() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    assertThatThrownBy(() ->
        drillStemTestResultPersistenceService
            .getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class);
  }

  @Test
  void getDrillStemTestResultByIdOrThrow() {
    var drillStemTestResult = mock(DrillStemTestResult.class);

    when(drillStemTestResultRepository.findById(any()))
        .thenReturn(Optional.of(drillStemTestResult));
    var result = drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(UUID.randomUUID());

    assertThat(drillStemTestResult).isEqualTo(result);
  }

  @Test
  void getDrillStemTestResultByIdOrThrow_verifyThrown() {
    when(drillStemTestResultRepository.findById(any()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
        drillStemTestResultPersistenceService
            .getDrillStemTestResultByIdOrThrow(UUID.randomUUID()))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class);
  }

  @Test
  void getDrillStemTestResultsByDrillStemTest_verifyMethodCall() {
    var drillStemTest = mock(DrillStemTest.class);
    drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest);

    verify(drillStemTestResultRepository, times(1))
        .findAllByDrillStemTest(drillStemTest);
  }

  @Test
  void getDrillStemTestResultsBySubmissionDetail_foundDrillStemTest() {
    var drillStemTest = mock(DrillStemTest.class);
    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(drillStemTestResultRepository, times(1))
        .findAllByDrillStemTest(drillStemTest);
  }

  @Test
  void getDrillStemTestResultsBySubmissionDetail_notFoundDrillStemTest() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var result = drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail);
    assertThat(result).isEmpty();
    verifyNoInteractions(drillStemTestResultRepository);
  }

  @Test
  void getDrillStemTestResultById_verifyMethodCall() {
    var id = UUID.randomUUID();
    drillStemTestResultPersistenceService.getDrillStemTestResultById(id);

    verify(drillStemTestResultRepository, times(1))
        .findById(id);
  }

  @Test
  void createNewDrillStemTestResult_verifySave() {
    var drillStemTest = mock(DrillStemTest.class);

    var result = drillStemTestResultPersistenceService.createNewDrillStemTestResult(drillStemTest);

    assertThat(result)
        .extracting(
            DrillStemTestResult::getDrillStemTest,
            DrillStemTestResult::getCommentsInterpretation,
            DrillStemTestResult::getBhfp,
            DrillStemTestResult::getBottomDepth,
            DrillStemTestResult::getFlowDuration,
            DrillStemTestResult::getFormationName,
            DrillStemTestResult::getGasFlowrate,
            DrillStemTestResult::getTopDepth,
            DrillStemTestResult::getOilFlowrate,
            DrillStemTestResult::getWaterFlowrate,
            DrillStemTestResult::getWhfp,
            DrillStemTestResult::getTestNumber)
        .containsOnly(drillStemTest, null);
    verify(drillStemTestResultRepository, times(1))
        .save(result);
  }

  @Test
  void deleteDrillStemTestResult_verifyMethodCall() {
    var drillStemTestResult = mock(DrillStemTestResult.class);

    drillStemTestResultPersistenceService.deleteDrillStemTestResult(drillStemTestResult);

    verify(drillStemTestResultRepository, times(1)).delete(drillStemTestResult);
  }

  public static DrillStemTestResultForm getFilledDrillStemTestResultForm() {
    var form = new DrillStemTestResultForm();
    form.setBhfp(DecimalInput.createDecimalInput(1));
    form.setBottomDepth(DecimalInput.createDecimalInput(BigDecimal.valueOf(1.55)));
    form.setFlowDuration(DecimalInput.createDecimalInput(BigDecimal.valueOf(1.22)));
    form.setGasFlowrate(DecimalInput.createDecimalInput(BigDecimal.valueOf(1.2)));
    form.setOilFlowrate(DecimalInput.createDecimalInput(BigDecimal.valueOf(1.2)));
    form.setWaterFlowrate(DecimalInput.createDecimalInput(BigDecimal.valueOf(1.1)));
    form.setWhfp(DecimalInput.createDecimalInput(1));
    form.setTopDepth(DecimalInput.createDecimalInput(BigDecimal.valueOf(1.22)));
    form.setCommentsInterpretation("comment");
    form.setFormationName("name");
    form.setTestNumber("2a");
    return form;
  }
}