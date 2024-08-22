package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class TrapPropertiesSubmissionService implements SectionSubmissionService {

  private final TrapPropertiesFormService trapPropertiesFormService;
  private final TrapPropertiesFormValidator trapPropertiesFormValidator;

  @Autowired
  public TrapPropertiesSubmissionService(TrapPropertiesFormService trapPropertiesFormService,
                                         TrapPropertiesFormValidator trapPropertiesFormValidator) {
    this.trapPropertiesFormService = trapPropertiesFormService;
    this.trapPropertiesFormValidator = trapPropertiesFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    var form = trapPropertiesFormService.getTrapPropertiesForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return trapPropertiesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
