package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.util.Arrays;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class SubmissionFormValidationUtils {

  private SubmissionFormValidationUtils() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static ValidationType extractValidationTypeFromHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ValidationType.class))
        .map(ValidationType.class::cast)
        .findFirst()
        .orElse(null);
  }
}
