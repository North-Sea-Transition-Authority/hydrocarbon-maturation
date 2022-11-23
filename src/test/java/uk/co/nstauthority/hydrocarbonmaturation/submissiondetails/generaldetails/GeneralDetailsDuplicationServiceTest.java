package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsDuplicationServiceTest {
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private GeneralDetailsDuplicationService generalDetailsDuplicationService;

  @Test
  void duplicateSubmissionSection_GeneralDetailExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var generalDetail = mock(GeneralDetail.class);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(generalDetail));

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, generalDetail,
        GeneralDetail.class);
  }

  @Test
  void duplicateSubmissionSection_GeneralDetailDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(GeneralDetail.class), eq(GeneralDetail.class));
  }

  @Test
  void duplicateSubmissionSection_QuadrantBlocksExist_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);
    var generalDetail = mock(GeneralDetail.class);
    var newGeneralDetail = mock(GeneralDetail.class);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var quadrantAndBlock1 = mock(QuadrantAndBlock.class);
    var quadrantAndBlock2 = mock(QuadrantAndBlock.class);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantAndBlock1, quadrantAndBlock2));

    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, generalDetail,
        GeneralDetail.class)).thenReturn(newGeneralDetail);

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newGeneralDetail, quadrantAndBlock1, QuadrantAndBlock.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newGeneralDetail, quadrantAndBlock2, QuadrantAndBlock.class);
  }

  @Test
  void duplicateSubmissionSection_QuadrantBlocksDoNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);
    var generalDetail = mock(GeneralDetail.class);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(generalDetail));

    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(Collections.emptyList());

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(generalDetail), any(QuadrantAndBlock.class), eq(QuadrantAndBlock.class));
  }
}