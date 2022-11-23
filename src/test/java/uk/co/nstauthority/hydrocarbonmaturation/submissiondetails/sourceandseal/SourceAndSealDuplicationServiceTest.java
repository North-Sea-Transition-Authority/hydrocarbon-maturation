package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

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
class SourceAndSealDuplicationServiceTest {
  @Mock
  private SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private SourceAndSealDuplicationService sourceAndSealDuplicationService;

  @Test
  void duplicateSubmissionSection_SourceAndSealExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var sourceAndSeal = mock(SourceAndSeal.class);
    when(sourceAndSealPersistenceService.getSourceAndSeal(oldSubmissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    sourceAndSealDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, sourceAndSeal,
        SourceAndSeal.class);
  }

  @Test
  void duplicateSubmissionSection_SourceAndSealDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(sourceAndSealPersistenceService.getSourceAndSeal(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    sourceAndSealDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(SourceAndSeal.class), eq(SourceAndSeal.class));
  }
}