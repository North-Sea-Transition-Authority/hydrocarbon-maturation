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
  private final ResourceDetailsFormValidator resourceDetailsFormValidator;

  @Autowired
  public ResourceDetailsSubmissionService(ResourceDetailsFormService resourceDetailsFormService,
                                          ResourceDetailsFormValidator resourceDetailsFormValidator) {
    this.resourceDetailsFormService = resourceDetailsFormService;
    this.resourceDetailsFormValidator = resourceDetailsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    if (submissionDetail.isFieldSubmission()) {
      return true;
    }

    var form = resourceDetailsFormService.getResourceDetailsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return resourceDetailsFormValidator.isValid(bindingResult, submissionDetail, ValidationType.FULL, user);
  }

}
