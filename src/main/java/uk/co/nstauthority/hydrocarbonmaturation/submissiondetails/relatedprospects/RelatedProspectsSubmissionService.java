package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedprospects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class RelatedProspectsSubmissionService implements SectionSubmissionService {

  private final RelatedProspectsFormService relatedProspectsFormService;

  @Autowired
  public RelatedProspectsSubmissionService(RelatedProspectsFormService relatedProspectsFormService) {
    this.relatedProspectsFormService = relatedProspectsFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = relatedProspectsFormService.getRelatedProspectsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = relatedProspectsFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
