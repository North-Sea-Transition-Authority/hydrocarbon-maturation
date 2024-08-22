package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService;

@ExtendWith(MockitoExtension.class)
class SubmissionUpdatePersistenceServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionUpdateRepository submissionUpdateRepository;
  @Mock
  private Clock clock;
  @Mock
  private SubmissionEmailService submissionEmailService;
  @Mock
  private SubmissionCreateService submissionCreateService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @InjectMocks
  private SubmissionUpdatePersistenceService underTest;

  @Nested
  class WhenRequestingUpdate {
    private static final String UPDATE_REASON = "Reason for update here";
    private static final Instant REQUESTED_TIMESTAMP = Instant.parse("2022-12-03T10:15:30.00Z");
    private SubmissionDetail submissionDetail;
    private RequestUpdateForm form;

    @BeforeEach
    void setup() {
      when(clock.instant()).thenReturn(REQUESTED_TIMESTAMP);
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      form = new RequestUpdateForm();
    }

    @Test
    void withReason_assertAllValues() {
      form.setUpdateReason(UPDATE_REASON);
      var captor = ArgumentCaptor.forClass(SubmissionUpdate.class);
          underTest.requestUpdate(submissionDetail, form, USER);

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
      underTest.getLatestUnresolvedSubmissionUpdateBySubmission(submission);
      verify(submissionUpdateRepository, times(1))
          .findFirstBySubmissionAndResolvedFalseOrderByRequestedTimestampDesc(submission);
    }

    @Test
    void assertReturn() {
      when(submissionUpdateRepository.findFirstBySubmissionAndResolvedFalseOrderByRequestedTimestampDesc(submission))
          .thenReturn(Optional.of(submissionUpdate));
      var result = underTest.getLatestUnresolvedSubmissionUpdateBySubmission(submission);
      assertThat(result).contains(submissionUpdate);
    }
  }

  @Nested
  class WhenGettingSubmissionUpdate {

    @Mock
    private Submission submission;

    @Mock
    private SubmissionUpdate submissionUpdate;

    @Test
    void getLatestSubmissionUpdateForSubmission() {
      when(submissionUpdateRepository.findFirstBySubmissionAndResolvedFalseOrderByRequestedTimestampDesc(submission))
          .thenReturn(Optional.of(submissionUpdate));

      assertThat(underTest.getLatestUnresolvedSubmissionUpdateBySubmission(submission)).contains(submissionUpdate);
    }
  }

  @Nested
  class WhenStartingUpdate {

    @Mock
    private SubmissionDetail submissionDetail;

    @Mock
    private SubmissionDetail updateSubmissionDetail;

    @Test
    void startUpdate() {
      when(submissionCreateService.createUpdateSubmissionDetail(submissionDetail, USER))
          .thenReturn(updateSubmissionDetail);

      assertThat(underTest.startUpdate(submissionDetail, USER)).isEqualTo(updateSubmissionDetail);
    }
  }

  @Nested
  class WhenGettingDraftUpdateSubmissionDetail {
    @Mock
    private Submission submission;

    private SubmissionDetail submissionDetailSubmitted;
    private SubmissionDetail submissionDetailUpdateDraft;
    private SubmissionDetail submissionDetailSubmitted2;

    @BeforeEach
    void setUp() {
      submissionDetailSubmitted = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetailSubmitted.setStatus(SubmissionDetailStatus.SUBMITTED);
      submissionDetailUpdateDraft = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetailUpdateDraft.setStatus(SubmissionDetailStatus.DRAFT);
      submissionDetailSubmitted2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetailSubmitted2.setStatus(SubmissionDetailStatus.SUBMITTED);
    }

    @Test
    void whenSingleSubmissionDetailOnSubmission_assertEmptyOptional() {
      when(submissionDetailService.getSubmissionDetailsBySubmission(submission))
          .thenReturn(List.of(submissionDetailSubmitted));

      assertThat(underTest.getDraftUpdateSubmissionDetail(submission)).isNotPresent();
    }

    @Test
    void whenOneDraftAndSubmittedSubmissionDetailOnSubmission_assertDraftReturned() {
      when(submissionDetailService.getSubmissionDetailsBySubmission(submission))
          .thenReturn(List.of(submissionDetailSubmitted, submissionDetailUpdateDraft));

      assertThat(underTest.getDraftUpdateSubmissionDetail(submission)).contains(submissionDetailUpdateDraft);
    }

    @Test
    void whenTwoSubmittedSubmissionDetailOnSubmission_assertEmptyOptional() {
      when(submissionDetailService.getSubmissionDetailsBySubmission(submission))
          .thenReturn(List.of(submissionDetailSubmitted, submissionDetailSubmitted2));

      assertThat(underTest.getDraftUpdateSubmissionDetail(submission)).isNotPresent();
    }
  }

  @Nested
  class WhenResolvingSubmissionUpdate {

    private static final Instant RESOLVED_TIMESTAMP = Instant.now();
    private static final Instant REQUESTED_TIMESTAMP = Instant.now();
    private static final Submission SUBMISSION = SubmissionTestUtils.createSubmission(mock(Resource.class));
    private static final String REASON = "reason for update";
    private static final Integer REQUESTED_BY_WUA_ID = 4;

    @Test
    void whenPassingEmptySubmissionUpdate_assertResolvedFields() {
      var submissionUpdate = new SubmissionUpdate();
      var captor = ArgumentCaptor.forClass(SubmissionUpdate.class);

      when(clock.instant())
          .thenReturn(RESOLVED_TIMESTAMP);

      underTest.resolveUpdate(submissionUpdate, USER);

      verify(submissionUpdateRepository, times(1))
          .save(captor.capture());

      assertThat(captor.getValue())
          .hasAllNullFieldsOrPropertiesExcept(
              "resolved", "resolvedTimestamp", "resolvedByWuaId")
          .extracting(
              SubmissionUpdate::getResolved,
              SubmissionUpdate::getResolvedTimestamp,
              SubmissionUpdate::getResolvedByWuaId)
          .containsExactly(
              true,
              RESOLVED_TIMESTAMP,
              USER.wuaId().intValue());
    }

    @Test
    void whenPassinFilledSubmissionUpdate_assertResolvedFields() {
      var submissionUpdate = new SubmissionUpdate();
      submissionUpdate.setSubmission(SUBMISSION);
      submissionUpdate.setResolved(false);
      submissionUpdate.setReason(REASON);
      submissionUpdate.setRequestedTimestamp(REQUESTED_TIMESTAMP);
      submissionUpdate.setRequestedByWuaId(REQUESTED_BY_WUA_ID);

      var captor = ArgumentCaptor.forClass(SubmissionUpdate.class);

      when(clock.instant())
          .thenReturn(RESOLVED_TIMESTAMP);

      underTest.resolveUpdate(submissionUpdate, USER);

      verify(submissionUpdateRepository, times(1))
          .save(captor.capture());

      assertThat(captor.getValue())
          .extracting(
              SubmissionUpdate::getSubmission,
              SubmissionUpdate::getRequestedByWuaId,
              SubmissionUpdate::getRequestedTimestamp,
              SubmissionUpdate::getResolved,
              SubmissionUpdate::getResolvedTimestamp,
              SubmissionUpdate::getResolvedByWuaId)
          .containsExactly(
              SUBMISSION,
              REQUESTED_BY_WUA_ID,
              REQUESTED_TIMESTAMP,
              true,
              RESOLVED_TIMESTAMP,
              USER.wuaId().intValue()
          );
    }
  }
}