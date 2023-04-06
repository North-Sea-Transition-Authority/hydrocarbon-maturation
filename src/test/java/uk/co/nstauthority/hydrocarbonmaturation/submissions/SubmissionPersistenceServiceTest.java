package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@ExtendWith(MockitoExtension.class)
class SubmissionPersistenceServiceTest {

  @Mock
  private SubmissionRepository submissionRepository;

  @InjectMocks
  private SubmissionPersistenceService submissionPersistenceService;

  @Test
  void getSubmissionById_assertReturn() {
    var submission = new Submission();
    submission.setId(UUID.randomUUID());
    when(submissionRepository.findById(submission.getId()))
        .thenReturn(Optional.of(submission));
    assertThat(submissionPersistenceService.getSubmissionById(submission.getId()))
        .contains(submission);
  }

  @Test
  void getSubmissionById_assertNotPresent() {
    var uuidNotBySubmission = UUID.randomUUID();
    when(submissionRepository.findById(uuidNotBySubmission))
        .thenReturn(Optional.empty());
    assertThat(submissionPersistenceService.getSubmissionById(uuidNotBySubmission))
        .isNotPresent();
  }

  @Test
  void createSubmission() {
    var resource = new Resource();
    var submissionArgumentCaptor = ArgumentCaptor.forClass(Submission.class);
    var submission = submissionPersistenceService.createSubmission(resource);
    submission.setId(UUID.randomUUID());
    verify(submissionRepository, times(1)).save(submissionArgumentCaptor.capture());

    var createdSubmission = submissionArgumentCaptor.getValue();
    assertThat(createdSubmission)
        .extracting(
            Submission::getId,
            Submission::getResource)
        .containsExactly(
            submission.getId(),
            submission.getResource());
  }

  @Test
  void getSubmissionsByResource_assertReturn() {
    var resource = new Resource();
    var submission = new Submission();
    when(submissionRepository.findSubmissionsByResource(resource))
        .thenReturn(List.of(submission));

    assertThat(submissionPersistenceService.getSubmissionsByResource(resource))
        .containsExactly(submission);
  }

  @Test
  void getSubmissionsByResource_assertMultipleReturned() {
    var resource = new Resource();
    var submission = new Submission();
    var submission2 = new Submission();
    when(submissionRepository.findSubmissionsByResource(resource))
        .thenReturn(List.of(submission, submission2));

    assertThat(submissionPersistenceService.getSubmissionsByResource(resource))
        .containsExactly(submission, submission2);
  }
}