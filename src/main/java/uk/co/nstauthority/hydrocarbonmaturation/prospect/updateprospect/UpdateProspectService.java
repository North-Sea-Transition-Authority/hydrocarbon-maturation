package uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class UpdateProspectService {
  private final SubmissionCreateService submissionCreateService;
  private final UpdateFormValidator updateFormValidator;

  @Autowired
  public UpdateProspectService(SubmissionCreateService submissionCreateService,
                               UpdateFormValidator updateFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.updateFormValidator = updateFormValidator;
  }

  public SubmissionDetail createUpdateProspect(UpdateProspectForm form) {
    return submissionCreateService.createUpdateProspect(form);
  }

  public BindingResult validate(UpdateProspectForm form, BindingResult bindingResult) {
    updateFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}