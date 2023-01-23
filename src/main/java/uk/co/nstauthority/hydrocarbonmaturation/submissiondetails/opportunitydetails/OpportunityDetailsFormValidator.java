package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class OpportunityDetailsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return OpportunityDetailsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    //no fields require partial validation
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "opportunityType", "opportunityType.required", "Select an opportunity type");

    var opportunityDetailsForm = (OpportunityDetailsForm) form;

    if (Objects.equals(opportunityDetailsForm.getOpportunityType(), OpportunityType.OTHER)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherTypeOfOpportunity", "otherTypeOfOpportunity.required",
          "Enter the other opportunity type");
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keyEvaluationWorkCarriedOut", "keyEvaluationWorkCarriedOut.required",
        "Enter the key evaluation work being carried out for this opportunity");

    var mapOfOpportunity = opportunityDetailsForm.getMapOfOpportunity();

    if (mapOfOpportunity.isEmpty()) {
      errors.rejectValue("mapOfOpportunity", "mapOfOpportunity.required", "Upload a map of opportunity");
    }

    FileUploadValidationUtils.rejectIfFileDescriptionsAreEmptyOrWhitespace(errors, mapOfOpportunity, "mapOfOpportunity");
  }
}
