package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OpportunityDetailsFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return OpportunityDetailsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (OpportunityDetailsForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {

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
  }
}
