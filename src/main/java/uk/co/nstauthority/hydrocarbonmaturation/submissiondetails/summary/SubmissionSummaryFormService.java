package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class SubmissionSummaryFormService {
  private final SubmissionReadyService submissionReadyService;
  private final SubmissionSummaryFormValidator submissionSummaryFormValidator;

  @Autowired
  SubmissionSummaryFormService(SubmissionReadyService submissionReadyService,
                               SubmissionSummaryFormValidator submissionSummaryFormValidator) {
    this.submissionReadyService = submissionReadyService;
    this.submissionSummaryFormValidator = submissionSummaryFormValidator;
  }

  public BindingResult validate(SubmissionSummaryForm form,
                                BindingResult bindingResult,
                                SubmissionDetail submissionDetail) {
    submissionSummaryFormValidator.validate(
        form,
        bindingResult,
        submissionReadyService.isSubmissionSubmittable(submissionDetail)
    );
    return bindingResult;
  }
}
