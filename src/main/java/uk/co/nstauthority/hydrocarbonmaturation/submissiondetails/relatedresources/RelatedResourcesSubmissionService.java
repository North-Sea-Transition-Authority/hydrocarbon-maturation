package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class RelatedResourcesSubmissionService implements SectionSubmissionService {

  private final RelatedResourcesFormService relatedResourcesFormService;
  private final RelatedResourcesFormValidator relatedResourcesFormValidator;

  @Autowired
  public RelatedResourcesSubmissionService(RelatedResourcesFormService relatedResourcesFormService,
                                           RelatedResourcesFormValidator relatedResourcesFormValidator) {
    this.relatedResourcesFormService = relatedResourcesFormService;
    this.relatedResourcesFormValidator = relatedResourcesFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    var form = relatedResourcesFormService.getRelatedResourcesForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return relatedResourcesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
