package uk.co.nstauthority.hydrocarbonmaturation.newresource;

import java.util.Arrays;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;

@Service
public class NewResourceFormValidator implements SmartValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var hints = extractNewResourceFormValidatorHints(validationHints);
    var isRegulator = hints.isRegulator();
    var resourceType = hints.getResourceType();

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "resourceName",
        "resourceName.required",
        "Enter the %s name".formatted(resourceType.getDisplayName().toLowerCase()));
    if (!isRegulator) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors,
          "responsibleOrganisationId",
          "responsibleOrganisationId.required",
          "Enter the responsible organisation");
    }

    ValidationUtils.rejectIfEmpty(
        errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return NewResourceForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("You must provide NewResourceFormValidatorHints");
  }

  private NewResourceFormValidatorHints extractNewResourceFormValidatorHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(NewResourceFormValidatorHints.class::isInstance)
        .map(NewResourceFormValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get NewResourceFormValidatorHints"));
  }
}
