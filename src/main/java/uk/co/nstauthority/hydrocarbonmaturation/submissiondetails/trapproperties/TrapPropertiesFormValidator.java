package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.CoordinateInputValidationUtil;

@Service
class TrapPropertiesFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return TrapPropertiesForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (TrapPropertiesForm) target;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "trapDescription", "trapDescription.required",
        "Enter a trap description");

    ValidationUtils.rejectIfEmpty(errors, "trapType", "trapType.required",
        "Select a trap type");

    if (Objects.equals(form.getTrapType(), TrapType.OTHER)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherTrapType", "otherTrapType.required",
          "Enter an alternative trap type");
    }

    CoordinateInputValidationUtil.validateCoordinateInput(errors, form.getLatitudeCoordinates(),
        "latitudeCoordinates", "latitude");

    CoordinateInputValidationUtil.validateCoordinateInputWithHemisphere(errors, form.getLongitudeCoordinates(),
        "longitudeCoordinates", "longitude", form.getLongitudeHemisphere(), "longitudeHemisphere");
  }
}
