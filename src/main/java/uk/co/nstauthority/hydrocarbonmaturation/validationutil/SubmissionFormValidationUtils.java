package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.util.Arrays;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

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

  public static SubmissionType extractSubmissionTypeFromHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(SubmissionType.class))
        .map(SubmissionType.class::cast)
        .findFirst()
        .orElse(null);
  }
}
