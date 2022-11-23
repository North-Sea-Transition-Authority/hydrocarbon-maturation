package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsDuplicationServiceTest {
  @Mock
  private DryWellDetailsPersistenceService dryWellDetailsPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private DryWellDetailsDuplicationService dryWellDetailsDuplicationService;

  @Test
  void duplicateSubmissionSection_DryWellDetailExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var dryWellDetail = mock(DryWellDetail.class);
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(dryWellDetail));

    dryWellDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, dryWellDetail,
        DryWellDetail.class);
  }

  @Test
  void duplicateSubmissionSection_DryWellDetailDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    dryWellDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(DryWellDetail.class), eq(DryWellDetail.class));
  }
}