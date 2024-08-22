package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class UpdateDiscoveryService {

  private final SubmissionCreateService submissionCreateService;
  private final UpdateDiscoveryFormValidator updateDiscoveryFormValidator;

  @Autowired
  UpdateDiscoveryService(SubmissionCreateService submissionCreateService,
                         UpdateDiscoveryFormValidator updateDiscoveryFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.updateDiscoveryFormValidator = updateDiscoveryFormValidator;
  }

  public SubmissionDetail createUpdateDiscovery(UpdateDiscoveryForm form, User user) {
    return submissionCreateService.createUpdateDiscovery(form, user);
  }

  public BindingResult validate(UpdateDiscoveryForm form, BindingResult bindingResult) {
    updateDiscoveryFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
