package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class RelinquishmentService {

  private final SubmissionCreateService submissionCreateService;
  private final RelinquishmentFormValidator relinquishmentFormValidator;

  @Autowired
  public RelinquishmentService(SubmissionCreateService submissionCreateService,
                               RelinquishmentFormValidator relinquishmentFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.relinquishmentFormValidator = relinquishmentFormValidator;
  }

  public SubmissionDetail createRelinquishment(RelinquishmentForm form) {
    return submissionCreateService.createRelinquishment(form);
  }

  public BindingResult validate(RelinquishmentForm form, BindingResult bindingResult) {
    relinquishmentFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
