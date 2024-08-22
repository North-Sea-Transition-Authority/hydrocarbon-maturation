package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ConsideringCcusSubmissionService implements SectionSubmissionService {

  private final ConsideringCcusFormValidator consideringCcusFormValidator;
  private final ConsideringCcusFormService consideringCcusFormService;

  @Autowired
  public ConsideringCcusSubmissionService(ConsideringCcusFormValidator consideringCcusFormValidator,
                                          ConsideringCcusFormService consideringCcusFormService) {
    this.consideringCcusFormValidator = consideringCcusFormValidator;
    this.consideringCcusFormService = consideringCcusFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    if (!submissionDetail.isFieldSubmission()) {
      return true;
    }
    var form = consideringCcusFormService.submissionDetailToForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    return consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
