package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class ProspectRelinquishmentService {

  private final SubmissionCreateService submissionCreateService;
  private final ProspectRelinquishmentFormValidator prospectRelinquishmentFormValidator;

  @Autowired
  public ProspectRelinquishmentService(SubmissionCreateService submissionCreateService,
                                       ProspectRelinquishmentFormValidator prospectRelinquishmentFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.prospectRelinquishmentFormValidator = prospectRelinquishmentFormValidator;
  }

  public SubmissionDetail createProspectRelinquishment(ProspectRelinquishmentForm form) {
    return submissionCreateService.createProspectRelinquishment(form);
  }

  public BindingResult validate(ProspectRelinquishmentForm form, BindingResult bindingResult) {
    prospectRelinquishmentFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
