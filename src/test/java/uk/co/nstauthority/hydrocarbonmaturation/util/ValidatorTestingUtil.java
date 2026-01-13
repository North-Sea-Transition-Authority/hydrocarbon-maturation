package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Provides access to common methods for testing Spring validators.
 */
public class ValidatorTestingUtil {

  private ValidatorTestingUtil() {
    throw new IllegalStateException("ValidatorTestingUtil is a util class and should not be instantiated");
  }

  /**
   * Return a map of field id -> set of field error codes for a BindingResult.
   */
  public static Map<String, Set<String>> extractErrors(BindingResult bindingResult) {

    return bindingResult.getFieldErrors().stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            LinkedHashMap::new,
            Collectors.mapping(FieldError::getCode, Collectors.toSet())
        ));

  }

  public static Map<String, Set<String>> extractErrorMessages(BindingResult bindingResult) {

    return bindingResult.getFieldErrors().stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            LinkedHashMap::new,
            Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())
        ));

  }

  public static void assertErrorExists(Errors errors,
                                       String fieldName,
                                       String expectedFieldCode,
                                       String expectedErrorMessage
  ) {
    var fieldError = errors.getFieldErrors().stream()
        .filter(error -> error.getField().equals(fieldName))
        .findFirst();

    if (fieldError.isEmpty()) {
      throw new AssertionError("Expected error for field '" + fieldName + "' but none found.");
    }

    assertEquals(fieldError.get().getCode(), expectedFieldCode);
    assertEquals(fieldError.get().getDefaultMessage(), expectedErrorMessage);

  }
}
