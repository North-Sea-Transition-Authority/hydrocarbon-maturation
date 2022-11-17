package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class SubmissionSummaryFormServiceTest {

  @Mock
  private ProspectSubmissionService prospectSubmissionService;

  @Mock
  private SubmissionSummaryFormValidator submissionSummaryFormValidator;

  @InjectMocks
  private SubmissionSummaryFormService submissionSummaryFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void validate_verifyMethodCall(Boolean submissionSubmittable) {
    var submissionSummaryForm = new SubmissionSummaryForm();
    var bindingResult = new BeanPropertyBindingResult(submissionSummaryForm, "form");
    when(prospectSubmissionService.isSubmissionSubmittable(submissionDetail)).thenReturn(submissionSubmittable);

    submissionSummaryFormService.validate(submissionSummaryForm, bindingResult, submissionDetail);

    verify(submissionSummaryFormValidator, times(1))
        .validate(submissionSummaryForm, bindingResult, submissionSubmittable);
  }
}