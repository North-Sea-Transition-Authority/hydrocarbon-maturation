package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.util.Arrays;

public class SubmissionFormValidationUtils {

  public static ValidationType extractValidationTypeFromHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ValidationType.class))
        .map(ValidationType.class::cast)
        .findFirst()
        .orElse(null);
  }
}
