package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@Service
class RelinquishmentFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return RelinquishmentForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (RelinquishmentForm) target;

    ValidationUtils.rejectIfEmpty(errors, "chosenResourceType",
        "chosenResourceType.required", "Select a resource type for relinquishment");
    if (form.getChosenResourceType() != null && form.getChosenResourceType() == ResourceType.PROSPECT) {
      ValidationUtils.rejectIfEmpty(errors, "resourceSelectedOption",
          "resourceSelectedOption.required", "Select a prospect for relinquishment");
    } else if (form.getChosenResourceType() == ResourceType.DISCOVERY) {
      // todo HM-372 add discovery relinquishments, this conditional branch is just temporary to prevent an
      //  exception when trying to submit a discovery relinquishment which is unfinished
      errors.rejectValue(
          "chosenResourceType",
          "chosenResourceType.notImplemented",
          "Discovery relinquishment is not implemented yet");
    }
  }
}
