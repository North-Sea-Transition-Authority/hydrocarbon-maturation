package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.submission.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DiscoveryWellDetailsSubmissionService implements SectionSubmissionService {

  private final DiscoveryWellDetailsFormService discoveryWellDetailsFormService;

  @Autowired
  public DiscoveryWellDetailsSubmissionService(DiscoveryWellDetailsFormService discoveryWellDetailsFormService) {
    this.discoveryWellDetailsFormService = discoveryWellDetailsFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = discoveryWellDetailsFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
