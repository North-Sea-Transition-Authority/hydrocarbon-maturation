package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class RelatedResourcesSubmissionService implements SectionSubmissionService {

  private final RelatedResourcesFormService relatedResourcesFormService;

  @Autowired
  public RelatedResourcesSubmissionService(RelatedResourcesFormService relatedResourcesFormService) {
    this.relatedResourcesFormService = relatedResourcesFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = relatedResourcesFormService.getRelatedResourcesForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = relatedResourcesFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
