package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.submission.ProspectSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class TrapPropertiesSubmissionService implements ProspectSectionSubmissionService {

  private final TrapPropertiesFormService trapPropertiesFormService;

  @Autowired
  public TrapPropertiesSubmissionService(TrapPropertiesFormService trapPropertiesFormService) {
    this.trapPropertiesFormService = trapPropertiesFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = trapPropertiesFormService.getTrapPropertiesForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = trapPropertiesFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
