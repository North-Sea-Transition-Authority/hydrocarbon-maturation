package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

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
class RelatedResourcesDuplicationServiceTest {

  @Mock
  private RelatedResourcesPersistenceService relatedResourcePersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private RelatedResourcesDuplicationService relatedResourceDuplicationService;

  @Test
  void duplicateSubmissionSection_RelatedResourceExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var relatedResource = mock(RelatedResource.class);
    when(relatedResourcePersistenceService.getRelatedResource(oldSubmissionDetail))
        .thenReturn(Optional.of(relatedResource));

    relatedResourceDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, relatedResource,
        RelatedResource.class);
  }

  @Test
  void duplicateSubmissionSection_RelatedResourceDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(relatedResourcePersistenceService.getRelatedResource(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    relatedResourceDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(RelatedResource.class), eq(RelatedResource.class));
  }
}