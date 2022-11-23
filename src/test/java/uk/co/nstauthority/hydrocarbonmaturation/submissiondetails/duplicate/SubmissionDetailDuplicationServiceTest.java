package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailDuplicationServiceTest {

  @Mock
  private SubmissionSectionDuplicationService submissionSectionDuplicationService;

  private SubmissionDetailDuplicationService submissionDetailDuplicationService;

  @BeforeEach
  void setUp() {
    submissionDetailDuplicationService = new SubmissionDetailDuplicationService(List.of(submissionSectionDuplicationService));
  }

  @Test
  void duplicateSubmissionDetailSections() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);
    submissionDetailDuplicationService.duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionSectionDuplicationService).duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);
  }
}