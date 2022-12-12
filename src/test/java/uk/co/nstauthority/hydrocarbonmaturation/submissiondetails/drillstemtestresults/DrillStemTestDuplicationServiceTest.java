package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class DrillStemTestDuplicationServiceTest {

  @Mock
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @Mock
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private DrillStemTestDuplicationService drillStemTestDuplicationService;

  @Test
  void duplicateSubmissionSection_DiscoveryUpdate_DrillStemTestResultExists_DoesDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var newSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    var oldDrillStemTest = mock(DrillStemTest.class);
    var newDrillStemTest = mock(DrillStemTest.class);


    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(oldDrillStemTest));

    var drillStemTestResult = mock(DrillStemTestResult.class);
    var drillStemTestResult2 = mock(DrillStemTestResult.class);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(oldDrillStemTest))
        .thenReturn(List.of(drillStemTestResult, drillStemTestResult2));
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, oldDrillStemTest, DrillStemTest.class))
        .thenReturn(newDrillStemTest);

    drillStemTestDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, oldDrillStemTest,
        DrillStemTest.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newDrillStemTest, drillStemTestResult,
        DrillStemTestResult.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newDrillStemTest, drillStemTestResult2,
        DrillStemTestResult.class);
  }

  @Test
  void duplicateSubmissionSection_DiscoveryUpdate_DrillStemTestDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var newSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();


    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    drillStemTestDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(DiscoveryWellDetail.class), eq(DiscoveryWellDetail.class));
  }
}