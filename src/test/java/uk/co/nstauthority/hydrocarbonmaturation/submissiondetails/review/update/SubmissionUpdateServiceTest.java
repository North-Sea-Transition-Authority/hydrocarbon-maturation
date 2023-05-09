package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class SubmissionUpdateServiceTest {

  @Mock
  private RequestUpdateFormValidator requestUpdateFormValidator;
  @Mock
  private SubmissionUpdatePersistenceService submissionUpdatePersistenceService;
  @InjectMocks
  private SubmissionUpdateService underTest;

  @Nested
  class WhenValidating {
    @Mock
    private BindingResult bindingResult;
    @Mock
    private RequestUpdateForm form;

    @Test
    void verifyMethodCall() {
      assertThat(underTest.validate(bindingResult, form)).isEqualTo(bindingResult);
      verify(requestUpdateFormValidator, times(1))
          .validate(form, bindingResult);
    }
  }

  @Nested
  class WhenCheckingIfCanRequestUpdateOnSubmission {

    private SubmissionDetail submissionDetail;
    private SubmissionUpdate submissionUpdate;

    @BeforeEach
    void setup() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
      submissionUpdate = new SubmissionUpdate();
    }

    @Test
    void AndSubmissionUpdateResolvedAndSubmitted_assertTrue() {
      submissionUpdate.setResolved(true);
      when(submissionUpdatePersistenceService.getLatestSubmissionUpdateBySubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.of(submissionUpdate));

      var result = underTest.canRequestUpdateOnSubmission(submissionDetail);

      assertThat(result).isTrue();
    }

    @Test
    void AndSubmissionUpdateNotResolvedAndSubmitted_assertTrue() {
      submissionUpdate.setResolved(false);
      when(submissionUpdatePersistenceService.getLatestSubmissionUpdateBySubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.of(submissionUpdate));

      var result = underTest.canRequestUpdateOnSubmission(submissionDetail);

      assertThat(result).isFalse();
    }

    @Test
    void AndSubmissionUpdateNullResolvedAndSubmitted_assertTrue() {
      submissionUpdate.setResolved(null);
      when(submissionUpdatePersistenceService.getLatestSubmissionUpdateBySubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.of(submissionUpdate));

      var result = underTest.canRequestUpdateOnSubmission(submissionDetail);

      assertThat(result).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = SubmissionDetailStatus.class, names = {"SUBMITTED"}, mode = EnumSource.Mode.EXCLUDE)
    void AndNotSubmittedStatus_assertFalse(SubmissionDetailStatus status) {
      submissionDetail.setStatus(status);
      submissionUpdate.setResolved(true);

      var result = underTest.canRequestUpdateOnSubmission(submissionDetail);

      assertThat(result).isFalse();
    }

    @Test
    void AndNotFoundUpdateAndSubmittedStatus_assertTrue() {
      when(submissionUpdatePersistenceService.getLatestSubmissionUpdateBySubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.empty());

      var result = underTest.canRequestUpdateOnSubmission(submissionDetail);

      assertThat(result).isTrue();
    }
  }

  @Nested
  class WhenCheckingIfHasAnUnresolvedUpdateOnSubmission {
    @Mock
    private Submission submission;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void hasUnresolvedUpdate(boolean hasUnresolvedUpdate) {
      when(submissionUpdatePersistenceService.submissionHasAnUnresolvedUpdate(submission))
          .thenReturn(hasUnresolvedUpdate);

      assertThat(underTest.hasAnUnresolvedUpdateOnSubmission(submission))
          .isEqualTo(hasUnresolvedUpdate);
    }
  }

  @Nested
  class WhenGettingDraftUpdateSubmissionDetailBySubmission {
    @Mock
    private Submission submission;

    @Mock
    private SubmissionDetail submissionDetail;

    @Test
    void whenFound() {
      when(submissionUpdatePersistenceService.getDraftUpdateSubmissionDetail(submission))
          .thenReturn(Optional.of(submissionDetail));

      assertThat(underTest.getDraftUpdateSubmissionDetailBySubmission(submission)).contains(submissionDetail);
    }

    @Test
    void whenNotFound() {
      when(submissionUpdatePersistenceService.getDraftUpdateSubmissionDetail(submission))
          .thenReturn(Optional.empty());

      assertThat(underTest.getDraftUpdateSubmissionDetailBySubmission(submission)).isNotPresent();
    }
  }

  @Nested
  class WhenCheckingIfCanStartUpdateSubmissionDetailOnSubmission {

    @Mock
    private Submission submission;

    @Test
    void whenDoesNotHaveUnresolvedUpdate() {
      when(submissionUpdatePersistenceService.submissionHasAnUnresolvedUpdate(submission))
          .thenReturn(false);

      assertThat(underTest.canStartUpdateSubmissionDetailOnSubmission(submission)).isFalse();
    }

    @Test
    void whenDoesNotHaveDraftUpdate() {
      when(submissionUpdatePersistenceService.submissionHasAnUnresolvedUpdate(submission))
          .thenReturn(true);

      when(submissionUpdatePersistenceService.getDraftUpdateSubmissionDetail(submission))
          .thenReturn(Optional.empty());

      assertThat(underTest.canStartUpdateSubmissionDetailOnSubmission(submission)).isTrue();
    }

    @Test
    void whenHasUnresolvedUpdateAndDraftUpdate_assertFalse() {
      when(submissionUpdatePersistenceService.submissionHasAnUnresolvedUpdate(submission))
          .thenReturn(true);

      when(submissionUpdatePersistenceService.getDraftUpdateSubmissionDetail(submission))
          .thenReturn(Optional.of(mock(SubmissionDetail.class)));

      assertThat(underTest.canStartUpdateSubmissionDetailOnSubmission(submission)).isFalse();
    }
  }

  @Nested
  class WhenResolvingUpdateOnSubmission {

    private SubmissionDetail submissionDetail;

    @Mock
    private SubmissionUpdate submissionUpdate;

    @BeforeEach
    void setUp() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    }

    @Test
    void resolveUpdateOnSubmission_whenUnresolvedSubmissionUpdatePresent_verifyMethodCall() {
      when(submissionUpdatePersistenceService.getUnresolvedSubmissionUpdateBySubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.of(submissionUpdate));

      underTest.resolveUpdateOnSubmission(submissionDetail);

      verify(submissionUpdatePersistenceService, times(1)).resolveUpdate(submissionUpdate);
    }

    @Test
    void resolveUpdateOnSubmission_whenSubmissionUpdateNotPresent_verifyMethodCall() {
      when(submissionUpdatePersistenceService.getUnresolvedSubmissionUpdateBySubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> underTest.resolveUpdateOnSubmission(submissionDetail))
          .hasMessage("Could not find an unresolved update on submission %s"
              .formatted(submissionDetail.getSubmission().getId()))
          .isExactlyInstanceOf(IllegalArgumentException.class);

      verify(submissionUpdatePersistenceService, never()).resolveUpdate(submissionUpdate);
    }
  }
}