package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DryWellDetailsSubmissionService implements SectionSubmissionService {

  private final DryWellDetailsFormService dryWellDetailsFormService;

  @Autowired
  public DryWellDetailsSubmissionService(DryWellDetailsFormService dryWellDetailsFormService) {
    this.dryWellDetailsFormService = dryWellDetailsFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = dryWellDetailsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
