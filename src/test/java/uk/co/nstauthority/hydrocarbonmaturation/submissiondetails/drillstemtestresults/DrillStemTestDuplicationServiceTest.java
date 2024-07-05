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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class DrillStemTestDuplicationServiceTest {

  @Mock
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @Mock
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private DrillStemTestDuplicationService drillStemTestDuplicationService;

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class)
  void duplicateSubmissionSection_DiscoveryUpdate_DrillStemTestResultExists_DoesDuplicate(
      SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var oldSubmissionDetail =
        ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var newSubmissionDetail =
        ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var oldDrillStemTest = mock(DrillStemTest.class);
    var newDrillStemTest = mock(DrillStemTest.class);


    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(oldDrillStemTest));

    var drillStemTestResult = mock(DrillStemTestResult.class);
    var drillStemTestResult2 = mock(DrillStemTestResult.class);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(oldDrillStemTest))
        .thenReturn(List.of(drillStemTestResult, drillStemTestResult2));
    when(reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, oldDrillStemTest, DrillStemTest.class))
        .thenReturn(newDrillStemTest);

    drillStemTestDuplicationService.duplicateReservoirSection(oldSubmissionDetail, newSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, oldDrillStemTest,
        DrillStemTest.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newDrillStemTest, drillStemTestResult,
        DrillStemTestResult.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newDrillStemTest, drillStemTestResult2,
        DrillStemTestResult.class);
  }

  @Test
  void duplicateSubmissionSection_DiscoveryUpdate_DrillStemTestDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var newSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    drillStemTestDuplicationService.duplicateReservoirSection(oldSubmissionDetail, newSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(any(), any(DrillStemTestResult.class), eq(DrillStemTestResult.class));
  }
}