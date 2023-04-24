package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

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
      when(submissionUpdatePersistenceService.getLatestSubmissionUpdateForSubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.of(submissionUpdate));

      var result = underTest.canRequestUpdateOnSubmission(submissionDetail);

      assertThat(result).isTrue();
    }

    @Test
    void AndSubmissionUpdateNotResolvedAndSubmitted_assertTrue() {
      submissionUpdate.setResolved(false);
      when(submissionUpdatePersistenceService.getLatestSubmissionUpdateForSubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.of(submissionUpdate));

      var result = underTest.canRequestUpdateOnSubmission(submissionDetail);

      assertThat(result).isFalse();
    }

    @Test
    void AndSubmissionUpdateNullResolvedAndSubmitted_assertTrue() {
      submissionUpdate.setResolved(null);
      when(submissionUpdatePersistenceService.getLatestSubmissionUpdateForSubmission(submissionDetail.getSubmission()))
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
      when(submissionUpdatePersistenceService.getLatestSubmissionUpdateForSubmission(submissionDetail.getSubmission()))
          .thenReturn(Optional.empty());

      var result = underTest.canRequestUpdateOnSubmission(submissionDetail);

      assertThat(result).isTrue();
    }
  }
}