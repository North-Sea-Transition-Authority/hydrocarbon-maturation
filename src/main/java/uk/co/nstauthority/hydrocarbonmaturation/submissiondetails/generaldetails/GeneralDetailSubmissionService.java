package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralDetailSubmissionService implements SectionSubmissionService {

  private final GeneralDetailsFormService generalDetailsFormService;
  private final GeneralDetailsFormValidator generalDetailsFormValidator;

  @Autowired
  public GeneralDetailSubmissionService(GeneralDetailsFormService generalDetailsFormService,
                                        GeneralDetailsFormValidator generalDetailsFormValidator) {
    this.generalDetailsFormService = generalDetailsFormService;
    this.generalDetailsFormValidator = generalDetailsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = generalDetailsFormService.getGeneralDetailsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
