package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;

@Service
public class UpdateFormValidator implements SmartValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return UpdateProspectForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate((SubmissionForm) target, errors);
  }

  public void validate(SubmissionForm form, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "prospectSelectedOption", "prospectOption.required", "Select a prospect to update");
    ValidationUtils.rejectIfEmpty(errors, "informationSource", "informationSource.required", "Select the source of information");

    var updateProspectForm = (UpdateProspectForm) form;

    if (updateProspectForm.getInformationSource() == InformationSource.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherSourceOfInformation", "otherSourceOfInformation.required",
          "Enter the source of information for this prospect submission");
    }
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    validate((SubmissionForm) target, errors);
  }
}
