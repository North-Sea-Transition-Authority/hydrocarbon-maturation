package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class DrillStemTestInitialPersistenceServiceTest {

  @Mock
  private DrillStemTestRepository drillStemTestRepository;

  @InjectMocks
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  private static ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeAll
  static void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void saveInitialDrillStemTest_whenGivenAnInitialForm_verifyCreated_Entity() {
    var form = new DrillStemTestInitialForm();
    form.setHasDrillStemTestResult(false);

    var drillStemTestCaptor = ArgumentCaptor.forClass(DrillStemTest.class);
    drillStemTestInitialPersistenceService.saveInitialDrillStemTest(form, reservoirSubmissionDetail);

    verify(drillStemTestRepository, times(1)).save(drillStemTestCaptor.capture());
    var savedDrillStemTest = drillStemTestCaptor.getValue();

    assertThat(savedDrillStemTest)
        .extracting(
            DrillStemTest::getDrillStemTestSummaryOption,
            DrillStemTest::getReservoirSubmissionDetail)
        .containsExactly(
            DrillStemTestSummaryOptions.NO,
            reservoirSubmissionDetail
        );
  }

  @Test
  void saveDrillStemTest_whenGivenSummaryForm() {
    var form = new DrillStemTestSummaryForm();
    form.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);

    var drillStemTestCaptor = ArgumentCaptor.forClass(DrillStemTest.class);
    drillStemTestInitialPersistenceService.saveDrillStemTest(form, reservoirSubmissionDetail);

    verify(drillStemTestRepository, times(1)).save(drillStemTestCaptor.capture());
    var savedDrillStemTest = drillStemTestCaptor.getValue();

    assertThat(savedDrillStemTest)
        .extracting(
            DrillStemTest::getDrillStemTestSummaryOption,
            DrillStemTest::getReservoirSubmissionDetail)
        .containsExactly(
            DrillStemTestSummaryOptions.NO,
            reservoirSubmissionDetail
        );
  }

  @Test
  void saveDrillStemTest_whenGivenEmptySummaryForm() {
    var form = new DrillStemTestSummaryForm();

    var drillStemTestCaptor = ArgumentCaptor.forClass(DrillStemTest.class);
    drillStemTestInitialPersistenceService.saveDrillStemTest(form, reservoirSubmissionDetail);

    verify(drillStemTestRepository, times(1)).save(drillStemTestCaptor.capture());
    var savedDrillStemTest = drillStemTestCaptor.getValue();

    assertThat(savedDrillStemTest)
        .extracting(
            DrillStemTest::getDrillStemTestSummaryOption,
            DrillStemTest::getReservoirSubmissionDetail)
        .containsExactly(
            null,
            reservoirSubmissionDetail
        );
  }

  @Test
  void saveInitialDrillStemTest_whenGivenEmptyInitialForm_verifyCreated_Entity() {
    var form = new DrillStemTestInitialForm();

    var drillStemTestCaptor = ArgumentCaptor.forClass(DrillStemTest.class);
    drillStemTestInitialPersistenceService.saveInitialDrillStemTest(form, reservoirSubmissionDetail);

    verify(drillStemTestRepository, times(1)).save(drillStemTestCaptor.capture());
    var savedDrillStemTest = drillStemTestCaptor.getValue();

    assertThat(savedDrillStemTest)
        .extracting(
            DrillStemTest::getDrillStemTestSummaryOption,
            DrillStemTest::getReservoirSubmissionDetail)
        .containsExactly(
            null,
            reservoirSubmissionDetail
        );
  }

  @Test
  void getDrillStemTestBySubmissionDetail_verifyMethodCall() {
    drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(drillStemTestRepository, times(1)).findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getDrillStemTestBySubmissionDetail_verifyEmpty_WhenNoDrillStemTest() {
    when(drillStemTestRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var result = drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(result).isNotPresent();
  }

  @Test
  void getDrillStemTestBySubmissionDetail_verifyReturned_WhenDrillStemTest() {
    var drillStemTest = mock(DrillStemTest.class);
    when(drillStemTestRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var result = drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(result).isEqualTo(Optional.of(drillStemTest));
  }

  @Test
  void getDrillStemTestBySubmissionDetailOrThrow_verifyReturned() {
    var drillStemTest = mock(DrillStemTest.class);
    when(drillStemTestRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var result = drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail);

    assertThat(result).isEqualTo(drillStemTest);
  }

  @Test
  void getDrillStemTestBySubmissionDetailOrThrow_verifyThrown() {
    when(drillStemTestRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    assertThatThrownBy(()->
        drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Drill stem test not found for submission with id %s"
            .formatted(reservoirSubmissionDetail.getId().toString()));
  }
}