package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class DiscoveryRelinquishmentService {

  private final SubmissionCreateService submissionCreateService;
  private final DiscoveryRelinquishmentFormValidator discoveryRelinquishmentFormValidator;

  @Autowired
  public DiscoveryRelinquishmentService(SubmissionCreateService submissionCreateService,
                                        DiscoveryRelinquishmentFormValidator discoveryRelinquishmentFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.discoveryRelinquishmentFormValidator = discoveryRelinquishmentFormValidator;
  }

  public SubmissionDetail createDiscoveryRelinquishment(DiscoveryRelinquishmentForm form) {
    return submissionCreateService.createDiscoveryRelinquishment(form);
  }

  public BindingResult validate(DiscoveryRelinquishmentForm form, BindingResult bindingResult) {
    discoveryRelinquishmentFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
