package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class OpportunityDetailsFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return OpportunityDetailsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (OpportunityDetailsForm) target;

    ValidationUtils.rejectIfEmpty(errors, "opportunityType", "opportunityType.required", "Select an opportunity type");

    if (form.getOpportunityType() == OpportunityType.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherTypeOfOpportunity", "otherTypeOfOpportunity.required",
          "Enter the type of opportunity for this opportunity");
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keyEvaluationWorkCarriedOut", "keyEvaluationWorkCarriedOut.required",
        "Enter the key evaluation work being carried out for this opportunity");
  }
}
