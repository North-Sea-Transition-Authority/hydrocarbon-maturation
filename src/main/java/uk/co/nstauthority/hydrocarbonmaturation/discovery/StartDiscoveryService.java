package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class StartDiscoveryService {

  private final SubmissionCreateService submissionCreateService;
  private final StartDiscoveryFormValidator startDiscoveryFormValidator;

  @Autowired
  StartDiscoveryService(SubmissionCreateService submissionCreateService,
                        StartDiscoveryFormValidator startDiscoveryFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.startDiscoveryFormValidator = startDiscoveryFormValidator;
  }

  SubmissionDetail createNewDiscovery(StartDiscoveryForm form) {
    return submissionCreateService.createNewDiscovery(form);
  }

  public BindingResult validate(StartDiscoveryForm form, BindingResult bindingResult) {
    startDiscoveryFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
