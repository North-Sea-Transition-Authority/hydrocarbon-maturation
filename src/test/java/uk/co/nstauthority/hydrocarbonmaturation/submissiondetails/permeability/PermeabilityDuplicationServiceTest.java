package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

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
class PermeabilityDuplicationServiceTest {
  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private PermeabilityDuplicationService permeabilityDuplicationService;

  @Test
  void duplicateSubmissionSection_PermeabilityExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var permeability = mock(PermeabilityDetail.class);
    when(permeabilityPersistenceService.getPermeabilityDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(permeability));

    permeabilityDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, permeability,
        PermeabilityDetail.class);
  }

  @Test
  void duplicateSubmissionSection_PermeabilityDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(permeabilityPersistenceService.getPermeabilityDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    permeabilityDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(PermeabilityDetail.class), eq(PermeabilityDetail.class));
  }
}