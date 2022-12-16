package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class DrillStemTestInitialPersistenceServiceTest {

  @Mock
  private DrillStemTestRepository drillStemTestRepository;

  @InjectMocks
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void saveInitialDrillStemTest_whenGivenAnInitialForm_verifyCreated_Entity() {
    var form = new DrillStemTestInitialForm();
    form.setHasDrillStemTestResult(false);

    var drillStemTestCaptor = ArgumentCaptor.forClass(DrillStemTest.class);
    drillStemTestInitialPersistenceService.saveInitialDrillStemTest(form, submissionDetail);

    verify(drillStemTestRepository, times(1)).save(drillStemTestCaptor.capture());
    var savedDrillStemTest = drillStemTestCaptor.getValue();

    assertThat(savedDrillStemTest)
        .extracting(
            DrillStemTest::getDrillStemTestSummaryOption,
            DrillStemTest::getSubmissionDetail)
        .containsExactly(
            DrillStemTestSummaryOptions.NO,
            submissionDetail
        );
  }

  @Test
  void saveDrillStemTest_whenGivenSummaryForm() {
    var form = new DrillStemTestSummaryForm();
    form.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);

    var drillStemTestCaptor = ArgumentCaptor.forClass(DrillStemTest.class);
    drillStemTestInitialPersistenceService.saveDrillStemTest(form, submissionDetail);

    verify(drillStemTestRepository, times(1)).save(drillStemTestCaptor.capture());
    var savedDrillStemTest = drillStemTestCaptor.getValue();

    assertThat(savedDrillStemTest)
        .extracting(
            DrillStemTest::getDrillStemTestSummaryOption,
            DrillStemTest::getSubmissionDetail)
        .containsExactly(
            DrillStemTestSummaryOptions.NO,
            submissionDetail
        );
  }

  @Test
  void saveDrillStemTest_whenGivenEmptySummaryForm() {
    var form = new DrillStemTestSummaryForm();

    var drillStemTestCaptor = ArgumentCaptor.forClass(DrillStemTest.class);
    drillStemTestInitialPersistenceService.saveDrillStemTest(form, submissionDetail);

    verify(drillStemTestRepository, times(1)).save(drillStemTestCaptor.capture());
    var savedDrillStemTest = drillStemTestCaptor.getValue();

    assertThat(savedDrillStemTest)
        .extracting(
            DrillStemTest::getDrillStemTestSummaryOption,
            DrillStemTest::getSubmissionDetail)
        .containsExactly(
            null,
            submissionDetail
        );
  }

  @Test
  void saveInitialDrillStemTest_whenGivenEmptyInitialForm_verifyCreated_Entity() {
    var form = new DrillStemTestInitialForm();

    var drillStemTestCaptor = ArgumentCaptor.forClass(DrillStemTest.class);
    drillStemTestInitialPersistenceService.saveInitialDrillStemTest(form, submissionDetail);

    verify(drillStemTestRepository, times(1)).save(drillStemTestCaptor.capture());
    var savedDrillStemTest = drillStemTestCaptor.getValue();

    assertThat(savedDrillStemTest)
        .extracting(
            DrillStemTest::getDrillStemTestSummaryOption,
            DrillStemTest::getSubmissionDetail)
        .containsExactly(
            null,
            submissionDetail
        );
  }

  @Test
  void getDrillStemTestBySubmissionDetail_verifyMethodCall() {
    drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail);

    verify(drillStemTestRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void getDrillStemTestBySubmissionDetail_verifyEmpty_WhenNoDrillStemTest() {
    when(drillStemTestRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var result = drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail);

    assertThat(result).isNotPresent();
  }

  @Test
  void getDrillStemTestBySubmissionDetail_verifyReturned_WhenDrillStemTest() {
    var drillStemTest = mock(DrillStemTest.class);
    when(drillStemTestRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var result = drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail);

    assertThat(result).isEqualTo(Optional.of(drillStemTest));
  }
}