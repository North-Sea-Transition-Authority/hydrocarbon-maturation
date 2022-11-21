package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.submission.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ChanceOfSuccessSubmissionService implements SectionSubmissionService {

  private final ChanceOfSuccessFormService chanceOfSuccessFormService;

  @Autowired
  public ChanceOfSuccessSubmissionService(ChanceOfSuccessFormService chanceOfSuccessFormService) {
    this.chanceOfSuccessFormService = chanceOfSuccessFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = chanceOfSuccessFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
