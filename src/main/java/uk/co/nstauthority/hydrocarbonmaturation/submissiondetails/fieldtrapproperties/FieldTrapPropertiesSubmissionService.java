package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FieldTrapPropertiesSubmissionService implements SectionSubmissionService {

  private final FieldTrapPropertiesFormService fieldTrapPropertiesFormService;
  private final FieldTrapPropertiesFormValidator fieldTrapPropertiesFormValidator;

  @Autowired
  public FieldTrapPropertiesSubmissionService(FieldTrapPropertiesFormService fieldTrapPropertiesFormService,
                                              FieldTrapPropertiesFormValidator fieldTrapPropertiesFormValidator) {
    this.fieldTrapPropertiesFormService = fieldTrapPropertiesFormService;
    this.fieldTrapPropertiesFormValidator = fieldTrapPropertiesFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isFieldSubmission()) {
      var form = fieldTrapPropertiesFormService.submissionDetailToForm(submissionDetail);
      BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
      return fieldTrapPropertiesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
    }

    return true;
  }
}
