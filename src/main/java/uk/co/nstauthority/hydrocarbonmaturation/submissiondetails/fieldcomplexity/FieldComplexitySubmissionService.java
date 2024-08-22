package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FieldComplexitySubmissionService implements SectionSubmissionService {

  private final FieldComplexityFormValidator fieldComplexityFormValidator;
  private final FieldComplexityService fieldComplexityService;

  @Autowired
  public FieldComplexitySubmissionService(FieldComplexityFormValidator fieldComplexityFormValidator,
                                          FieldComplexityService fieldComplexityService) {
    this.fieldComplexityFormValidator = fieldComplexityFormValidator;
    this.fieldComplexityService = fieldComplexityService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    if (!submissionDetail.isFieldSubmission()) {
      return true;
    }
    var form = fieldComplexityService.submissionDetailToForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    return fieldComplexityFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
