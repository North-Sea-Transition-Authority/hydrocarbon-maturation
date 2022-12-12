package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class DrillStemTestInitialFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return DrillStemTestInitialForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "hasDrillStemTestResult",
        "hasDrillStemTestResult.required",
        "`Select yes if you have a drill stem test result"
    );
  }
}
