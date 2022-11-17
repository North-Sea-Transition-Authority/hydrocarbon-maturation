package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class SubmissionSummaryFormService {
  private final ProspectSubmissionService prospectSubmissionService;
  private final SubmissionSummaryFormValidator submissionSummaryFormValidator;

  @Autowired
  SubmissionSummaryFormService(ProspectSubmissionService prospectSubmissionService,
                               SubmissionSummaryFormValidator submissionSummaryFormValidator) {
    this.prospectSubmissionService = prospectSubmissionService;
    this.submissionSummaryFormValidator = submissionSummaryFormValidator;
  }

  public BindingResult validate(SubmissionSummaryForm form,
                                BindingResult bindingResult,
                                SubmissionDetail submissionDetail) {
    submissionSummaryFormValidator.validate(
        form,
        bindingResult,
        prospectSubmissionService.isSubmissionSubmittable(submissionDetail)
    );
    return bindingResult;
  }
}
