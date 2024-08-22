package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;

@ExtendWith(MockitoExtension.class)
class SubmissionPersistenceServiceTest {
  private static final User USER = UserTestUtil.Builder().build();
  private static final Instant WITHDRAWN_INSTANT = Instant.now();
  private static final String WITHDRAW_REASON = "no reason";

  @Mock
  private SubmissionRepository submissionRepository;

  @Mock
  private Clock clock;

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

    verify(submissionRepository, times(1)).save(submissionArgumentCaptor.capture());

    var createdSubmission = submissionArgumentCaptor.getValue();
    assertThat(createdSubmission)
        .extracting(
            Submission::getId,
            Submission::isWithdrawn,
            Submission::getResource)
        .containsExactly(
            submission.getId(),
            null,
            submission.getResource());
  }

  @Test
  void withdrawSubmission() {
    var resource = new Resource();
    var submission = SubmissionTestUtils.createSubmission(resource);

    when(clock.instant()).thenReturn(WITHDRAWN_INSTANT);

    var returnedSubmission = submissionPersistenceService.withdrawSubmission(submission, WITHDRAW_REASON, USER);

    verify(submissionRepository, times(1)).save(returnedSubmission);

    assertThat(returnedSubmission)
        .extracting(
            Submission::getId,
            Submission::getResource,
            Submission::isWithdrawn,
            Submission::getWithdrawalReason,
            Submission::getWithdrawnByWuaId,
            Submission::getWithdrawnTimestamp)
        .containsExactly(
            submission.getId(),
            resource,
            true,
            WITHDRAW_REASON,
            USER.wuaId().intValue(),
            WITHDRAWN_INSTANT);
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
    submission.setWithdrawn(null);
    var submission2 = new Submission();
    when(submissionRepository.findSubmissionsByResource(resource))
        .thenReturn(List.of(submission, submission2));

    assertThat(submissionPersistenceService.getSubmissionsByResource(resource))
        .containsExactly(submission, submission2);
  }

  @Test
  void getSubmissionsByResource_assertWithdrawnNotReturned() {
    var resource = new Resource();
    var submission = new Submission();
    submission.setWithdrawn(true);
    var submission2 = new Submission();
    when(submissionRepository.findSubmissionsByResource(resource))
        .thenReturn(List.of(submission, submission2));

    assertThat(submissionPersistenceService.getSubmissionsByResource(resource))
        .containsExactly(submission2);
  }
}