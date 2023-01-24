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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.RelatedProspect;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.RelatedProspectsDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.RelatedProspectsPersistenceService;

@ExtendWith(MockitoExtension.class)
class RelatedProspectsDuplicationServiceTest {

  @Mock
  private RelatedProspectsPersistenceService relatedProspectPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private RelatedProspectsDuplicationService relatedProspectDuplicationService;

  @Test
  void duplicateSubmissionSection_RelatedProspectExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var relatedProspect = mock(RelatedProspect.class);
    when(relatedProspectPersistenceService.getRelatedProspect(oldSubmissionDetail))
        .thenReturn(Optional.of(relatedProspect));

    relatedProspectDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, relatedProspect,
        RelatedProspect.class);
  }

  @Test
  void duplicateSubmissionSection_RelatedProspectDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(relatedProspectPersistenceService.getRelatedProspect(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    relatedProspectDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(RelatedProspect.class), eq(RelatedProspect.class));
  }
}