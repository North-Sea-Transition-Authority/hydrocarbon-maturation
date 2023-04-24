package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class SubmissionUpdatePersistenceServiceTest {

  @Mock
  private SubmissionUpdateRepository submissionUpdateRepository;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private Clock clock;
  @Mock
  private SubmissionEmailService submissionEmailService;
  @InjectMocks
  private SubmissionUpdatePersistenceService underTest;

  @Nested
  class WhenRequestingUpdate {
    private static final String UPDATE_REASON = "Reason for update here";
    private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();
    private static final Instant REQUESTED_TIMESTAMP = Instant.parse("2022-12-03T10:15:30.00Z");
    private SubmissionDetail submissionDetail;
    private RequestUpdateForm form;

    @BeforeEach
    void setup() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(clock.instant()).thenReturn(REQUESTED_TIMESTAMP);
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      form = new RequestUpdateForm();
    }

    @Test
    void withReason_assertAllValues() {
      form.setUpdateReason(UPDATE_REASON);
      var captor = ArgumentCaptor.forClass(SubmissionUpdate.class);
          underTest.requestUpdate(submissionDetail, form);

      verify(submissionUpdateRepository, times(1)).save(captor.capture());
      verify(submissionEmailService, times(1))
          .sendReturnedForUpdateEmails(submissionDetail);

      assertThat(captor.getValue())
          .extracting(
              SubmissionUpdate::getReason,
              SubmissionUpdate::getRequestedByWuaId,
              SubmissionUpdate::getRequestedTimestamp,
              SubmissionUpdate::getResolved,
              SubmissionUpdate::getSubmission)
          .containsExactly(
              form.getUpdateReason(),
              USER.wuaId().intValue(),
              REQUESTED_TIMESTAMP,
              false,
              submissionDetail.getSubmission()
          );

    }
  }

  @Nested
  class WhenGettingLatestSubmissionUpdateForSubmission {

    @Mock
    private Submission submission;

    @Mock
    private SubmissionUpdate submissionUpdate;

    @Test
    void verifyMethodCalls() {
      underTest.getLatestSubmissionUpdateForSubmission(submission);
      verify(submissionUpdateRepository, times(1))
          .findFirstBySubmissionOrderByRequestedTimestampDesc(submission);
    }

    @Test
    void assertReturn() {
      when(submissionUpdateRepository.findFirstBySubmissionOrderByRequestedTimestampDesc(submission))
          .thenReturn(Optional.of(submissionUpdate));
      var result = underTest.getLatestSubmissionUpdateForSubmission(submission);
      assertThat(result).contains(submissionUpdate);
    }
  }
}