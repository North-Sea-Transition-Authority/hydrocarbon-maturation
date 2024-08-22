package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ResourceDetailsSubmissionService implements SectionSubmissionService {

  private final ResourceDetailsFormService resourceDetailsFormService;

  @Autowired
  public ResourceDetailsSubmissionService(ResourceDetailsFormService resourceDetailsFormService) {
    this.resourceDetailsFormService = resourceDetailsFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    if (submissionDetail.isFieldSubmission()) {
      return true;
    }

    var form = resourceDetailsFormService.getResourceDetailsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = resourceDetailsFormService.validate(form, bindingResult,
        ValidationType.FULL, submissionDetail, user);
    return !bindingResult.hasErrors();
  }

}
