package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;

@Service
class GeneralDetailsFormValidator implements SmartValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

  }

  @Override
  public boolean supports(Class<?> clazz) {
    return GeneralDetailsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (GeneralDetailsForm) target;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "prospectName", "prospectName.required", "Enter the prospect name");
    ValidationUtils.rejectIfEmpty(errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");
    ValidationUtils.rejectIfEmpty(errors, "prospectLocatedInUkcs", "prospectLocatedInUkcs.required",
        "Select whether the prospect is wholly located within UKCS");

    if (BooleanUtils.isFalse(form.getProspectLocatedInUkcs())) {
      new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(errors, "percentageLocatedInUkcs.decimalInputValue",
          "The hydrocarbon volume percentage", form.getPercentageLocatedInUkcs())
          .setMaxDecimalPlaces(2)
          .setMinValue(0)
          .setMaxValue(100)
          .validate();
    }

    ValidationUtils.rejectIfEmpty(errors, "informationSource", "informationSource.required", "Select the source of information");

    if (Objects.equals(form.getInformationSource(), InformationSource.OTHER)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherSourceOfInformation", "otherSourceOfInformation.required",
          "Enter the source of information for this prospect submission");
    }
  }
}
