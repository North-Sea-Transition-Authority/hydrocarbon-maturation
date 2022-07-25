package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Service
public class OpportunityDetailsFormValidator implements SmartValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

  }

  @Override
  public boolean supports(Class<?> clazz) {
    return OpportunityDetailsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (OpportunityDetailsForm) target;

    ValidationUtils.rejectIfEmpty(errors, "opportunityType", "opportunityType.required", "Select an opportunity type");

    if (Optional.ofNullable(form.getOpportunityType()).filter(ot -> ot == OpportunityType.OTHER).isPresent()) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherTypeOfOpportunity", "otherTypeOfOpportunity.required",
          "Provide the type of opportunity for this opportunity detail");
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keyEvaluationWorkCarriedOut", "keyEvaluationWorkCarriedOut.required",
        "Provide the key evaluation work being carried out for this opportunity detail");

  }
}
