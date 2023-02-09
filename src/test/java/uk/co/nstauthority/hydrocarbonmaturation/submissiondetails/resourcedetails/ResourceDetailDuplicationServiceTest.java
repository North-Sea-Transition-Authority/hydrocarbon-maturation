package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;


@ExtendWith(MockitoExtension.class)
class ResourceDetailDuplicationServiceTest {

  @Mock
  private ResourceDetailsPersistenceService resourceDetailPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private ResourceDetailDuplicationService resourceDetailDuplicationService;

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void duplicateSubmissionSection_ResourceDetailExists_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    var resourceDetail = mock(ResourceDetail.class);
    when(resourceDetailPersistenceService.getResourceDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(resourceDetail));

    resourceDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, resourceDetail,
        ResourceDetail.class);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void duplicateSubmissionSection_ResourceDetailDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    when(resourceDetailPersistenceService.getResourceDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    resourceDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ResourceDetail.class), eq(ResourceDetail.class));
  }
}