package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.math.BigDecimal;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

public class CoordinateInputValidationUtil {

  private static final String DEGREES_FIELD_SUFFIX = "degrees.decimalInputValue";
  private static final String MINUTES_FIELD_SUFFIX = "minutes.decimalInputValue";
  private static final String SECONDS_FIELD_SUFFIX = "seconds.decimalInputValue";
  private static final String PARTIAL_COORDINATE_PROVIDED_ERROR_MESSAGE = "Enter a complete %s";
  private static final String COORDINATE_REQUIRED_ERROR_MESSAGE = "Enter a %s";
  private static final String HEMISPHERE_REQUIRED_ERROR_MESSAGE = "Select a %s hemisphere";
  
  private static final BigDecimal UKCS_LONGITUDE_MIN_DEGREES = BigDecimal.valueOf(0);
  private static final BigDecimal UKCS_LONGITUDE_MAX_DEGREES = BigDecimal.valueOf(30);
  private static final BigDecimal UKCS_LATITUDE_MIN_DEGREES = BigDecimal.valueOf(45);
  private static final BigDecimal UKCS_LATITUDE_MAX_DEGREES = BigDecimal.valueOf(64);

  private CoordinateInputValidationUtil() {
    throw new IllegalStateException("CoordinateInputValidationUtil is a util class and should not be instantiated");
  }

  public static void validateCoordinateInput(Errors errors, CoordinateInput coordinateInput, String fieldIdPrefix,
                                             String displayPrefix) {

    var degrees = coordinateInput.getDegrees().getDecimalInputValue();
    var minutes = coordinateInput.getMinutes().getDecimalInputValue();
    var seconds = coordinateInput.getSeconds().getDecimalInputValue();

    if (ObjectUtils.allNull(degrees, minutes, seconds)) {
      addAllNullValidationErrors(errors, fieldIdPrefix, degrees, minutes, seconds, displayPrefix);

    } else if (ObjectUtils.anyNull(degrees, minutes, seconds)) {
      addPartialNullValidationErrors(errors, fieldIdPrefix, degrees, minutes, seconds, displayPrefix);
      validateDegreesMinutesSeconds(errors, fieldIdPrefix, displayPrefix, coordinateInput, OverrideErrorMessage.TRUE);

    } else {
      validateDegreesMinutesSeconds(errors, fieldIdPrefix, displayPrefix, coordinateInput, OverrideErrorMessage.FALSE);
    }
  }

  public static void validateCoordinateInputWithHemisphere(Errors errors, CoordinateInput coordinateInput, String fieldIdPrefix,
                                                           String displayPrefix, Hemisphere hemisphere,
                                                           String hemisphereFieldSuffix) {

    var degrees = coordinateInput.getDegrees().getDecimalInputValue();
    var minutes = coordinateInput.getMinutes().getDecimalInputValue();
    var seconds = coordinateInput.getSeconds().getDecimalInputValue();

    if (ObjectUtils.allNull(degrees, minutes, seconds)) {
      addAllNullValidationErrors(errors, fieldIdPrefix, degrees, minutes, seconds, displayPrefix);
      validateHemisphere(hemisphere, errors, fieldIdPrefix, hemisphereFieldSuffix,
          displayPrefix);

    } else if (ObjectUtils.anyNull(degrees, minutes, seconds)) {
      addPartialNullValidationErrors(errors, fieldIdPrefix, degrees, minutes, seconds, displayPrefix);
      validateHemisphere(hemisphere, errors, fieldIdPrefix, hemisphereFieldSuffix,
          displayPrefix);
      validateDegreesMinutesSeconds(errors, fieldIdPrefix, displayPrefix, coordinateInput, OverrideErrorMessage.TRUE);

    } else {
      validateHemisphere(hemisphere, errors, fieldIdPrefix, hemisphereFieldSuffix,
          displayPrefix);
      validateDegreesMinutesSeconds(errors, fieldIdPrefix, displayPrefix, coordinateInput,
          OverrideErrorMessage.getFromBoolean(errors.getFieldErrorCount(hemisphereFieldSuffix) != 0));
    }
  }

  private static void validateDegreesMinutesSeconds(Errors errors, String fieldIdPrefix, String displayPrefix,
                                                    CoordinateInput coordinateInput, OverrideErrorMessage overrideErrorMessage) {

    validateDegrees(errors, fieldIdPrefix, displayPrefix, coordinateInput.getDegrees(), overrideErrorMessage);
    validateMinutes(errors, fieldIdPrefix, displayPrefix, coordinateInput.getMinutes(), overrideErrorMessage);
    validateSeconds(errors, fieldIdPrefix, displayPrefix, coordinateInput.getSeconds(), overrideErrorMessage);
  }

  private static void validateHemisphere(Hemisphere hemisphere, Errors errors, String fieldIdPrefix, String hemisphereFieldSuffix,
                                         String displayPrefix) {
    if (hemisphere == null) {
      if (errors.getFieldErrorCount("%s.minutes.decimalInputValue".formatted(fieldIdPrefix)) == 0
          && errors.getFieldErrorCount("%s.degrees.decimalInputValue".formatted(fieldIdPrefix)) == 0
          && errors.getFieldErrorCount("%s.degrees.decimalInputValue".formatted(fieldIdPrefix)) == 0) {
        errors.rejectValue(
            hemisphereFieldSuffix,
            "%s.required".formatted(hemisphereFieldSuffix),
            HEMISPHERE_REQUIRED_ERROR_MESSAGE.formatted(displayPrefix)
        );
      } else {
        errors.rejectValue(
            hemisphereFieldSuffix,
            "%s.required".formatted(hemisphereFieldSuffix),
            ""
        );
      }
    }
  }

  private static void addPartialNullValidationErrors(Errors errors, String fieldIdPrefix, String degrees, String minutes,
                                                     String seconds, String displayPrefix) {

    rejectFieldsAndSetMessage(fieldIdPrefix,
        PARTIAL_COORDINATE_PROVIDED_ERROR_MESSAGE.formatted(displayPrefix),
        degrees == null ? "%s.%s.required".formatted(fieldIdPrefix, DEGREES_FIELD_SUFFIX) : null,
        minutes == null ? "%s.%s.required".formatted(fieldIdPrefix, MINUTES_FIELD_SUFFIX) : null,
        seconds == null ? "%s.%s.required".formatted(fieldIdPrefix, SECONDS_FIELD_SUFFIX) : null,
        errors);
  }

  private static void addAllNullValidationErrors(Errors errors, String fieldIdPrefix, String degrees, String minutes,
                                              String seconds, String displayPrefix) {

    rejectFieldsAndSetMessage(fieldIdPrefix,
        COORDINATE_REQUIRED_ERROR_MESSAGE.formatted(displayPrefix),
        degrees == null ? "%s.%s.required".formatted(fieldIdPrefix, DEGREES_FIELD_SUFFIX) : null,
        minutes == null ? "%s.%s.required".formatted(fieldIdPrefix, MINUTES_FIELD_SUFFIX) : null,
        seconds == null ? "%s.%s.required".formatted(fieldIdPrefix, SECONDS_FIELD_SUFFIX) : null,
        errors);
  }

  /**
   * Validates the individual degrees field for coordinate inputs.
   *
   * @param errors The Errors object to add rejection codes and messages to
   * @param fieldIdPrefix The prefix of the form coordinate fields. E.g. longitudeCoordinates. It's used to bind the errors
   *                      to the form field
   * @param displayPrefix The display version prefix of the form coordinate fields. E.g. for longitudeCoordinates it would be
   *                      longitude. It's used to format the error messages
   * @param degrees       The value input by the user for coordinate degrees
   * @param overrideErrorMessage A boolean to determine whether the error message should be overridden.
   */
  private static void validateDegrees(Errors errors, String fieldIdPrefix, String displayPrefix, DecimalInput degrees,
                                      OverrideErrorMessage overrideErrorMessage) {

    var degreesValidator =
        DecimalInputValidator.configureValidator(errors, "%s.%s".formatted(fieldIdPrefix, DEGREES_FIELD_SUFFIX),
          "The %s degrees".formatted(displayPrefix), degrees)
          .setMaxValue(displayPrefix.equals("latitude") ? UKCS_LATITUDE_MAX_DEGREES : UKCS_LONGITUDE_MAX_DEGREES)
          .setMinValue(displayPrefix.equals("latitude") ? UKCS_LATITUDE_MIN_DEGREES : UKCS_LONGITUDE_MIN_DEGREES)
          .setMaxDecimalPlaces(0);

    if (!degrees.hasContent() && overrideErrorMessage.equals(OverrideErrorMessage.TRUE)) {
      degreesValidator.overrideDefaultErrorMessage("");
    }

    degreesValidator.validate();
  }

  /**
   * Validates the individual minutes field for coordinate inputs. It checks whether the degrees fields have errors to
   * determine what the appropriate error message should be.
   *
   * @param errors The Errors object to add rejection codes and messages to
   * @param fieldIdPrefix The prefix of the form coordinate fields. E.g. longitudeCoordinates. It's used to bind the errors
   *                      to the form field
   * @param displayPrefix The display version prefix of the form coordinate fields. E.g. for longitudeCoordinates it would be
   *                      longitude. It's used to format the error messages
   * @param minutes       The value input by the user for coordinate minutes
   * @param overrideErrorMessage A boolean to determine whether the error message should be overridden.
   */
  private static void validateMinutes(Errors errors, String fieldIdPrefix, String displayPrefix, DecimalInput minutes,
                                      OverrideErrorMessage overrideErrorMessage) {

    var minutesValidator =
        DecimalInputValidator.configureValidator(errors, "%s.%s".formatted(fieldIdPrefix, MINUTES_FIELD_SUFFIX),
          "The %s minutes".formatted(displayPrefix), minutes)
          .setMaxValue(new BigDecimal("59"))
          .setMinValue(new BigDecimal("0"))
          .setMaxDecimalPlaces(0);

    if (errors.getFieldErrorCount("%s.degrees.decimalInputValue".formatted(fieldIdPrefix)) != 0 && !minutes.hasContent()
        && overrideErrorMessage.equals(OverrideErrorMessage.TRUE)) {
      minutesValidator.overrideDefaultErrorMessage("");
    }

    minutesValidator.validate();
  }

  /**
   *  Validates the individual seconds field for coordinate inputs. It checks whether the degrees and minutes fields have errors
   *  to determine what the appropriate error message should be.
   *
   * @param errors The Errors object to add rejection codes and messages to
   * @param fieldIdPrefix The prefix of the form coordinate fields. E.g. longitudeCoordinates. It's used to bind the errors
   *                      to the form field
   * @param displayPrefix The display version prefix of the form coordinate fields. E.g. for longitudeCoordinates it would be
   *                      longitude. It's used to format the error messages
   * @param seconds       The value input by the user for coordinate seconds
   * @param overrideErrorMessage A boolean to determine whether the error message should be overridden.
   */
  private static void validateSeconds(Errors errors, String fieldIdPrefix, String displayPrefix, DecimalInput seconds,
                                      OverrideErrorMessage overrideErrorMessage) {

    var secondsValidator =
        DecimalInputValidator.configureValidator(errors, "%s.%s".formatted(fieldIdPrefix, SECONDS_FIELD_SUFFIX),
          "The %s seconds".formatted(displayPrefix), seconds)
          .setMaxValue(new BigDecimal("59"))
          .setMinValue(new BigDecimal("0"))
          .setMaxDecimalPlaces(3);

    if (errors.getFieldErrorCount("%s.minutes.decimalInputValue".formatted(fieldIdPrefix)) != 0
        && errors.getFieldErrorCount("%s.degrees.decimalInputValue".formatted(fieldIdPrefix)) != 0
        && seconds.hasContent() && overrideErrorMessage.equals(OverrideErrorMessage.TRUE)) {
      secondsValidator.overrideDefaultErrorMessage("");
    }

    secondsValidator.validate();
  }

  /**
   * Rejects the degrees/minutes/seconds fields with the given codes and adds the error message to the 'first' rejected field for
   * compatibility with FDS.
   * A null code indicates that field should not be rejected.
   * At least one of the degrees, minutes or seconds codes must be not null.
   *
   * @param fieldPrefix    The prefix of the form coordinate fields. E.g. longitudeCoordinates
   * @param errorMessage   The error message to attach to the first field with an error
   * @param degreesErrorCode   An error code string to attach to the degrees field. This should be null if the degrees
   *                          field has no error
   * @param minutesErrorCode An error code string to attach to the minutes field. This should be null if the minutes
   *                         field has no error
   * @param secondsErrorCode  An error code string to attach to the seconds field. This should be null if the seconds
   *                          field has no error
   * @param errors         The Errors object to add rejection codes and messages to
   */
  private static void rejectFieldsAndSetMessage(String fieldPrefix, String errorMessage, String degreesErrorCode,
                                                String minutesErrorCode, String secondsErrorCode, Errors errors) {

    if (ObjectUtils.allNull(degreesErrorCode, minutesErrorCode, secondsErrorCode)) {
      throw new IllegalArgumentException("At least one of degrees, minutes or seconds codes must be provided.");
    }

    if (degreesErrorCode != null) {
      errors.rejectValue(
          "%s.%s".formatted(fieldPrefix, DEGREES_FIELD_SUFFIX),
          degreesErrorCode,
          errorMessage
      );
    }

    if (minutesErrorCode != null) {
      if (degreesErrorCode == null) {
        errors.rejectValue(
            "%s.%s".formatted(fieldPrefix, MINUTES_FIELD_SUFFIX),
            minutesErrorCode,
            errorMessage
        );
      } else {
        errors.rejectValue(
            "%s.%s".formatted(fieldPrefix, MINUTES_FIELD_SUFFIX),
            minutesErrorCode,
            ""
        );
      }
    }

    if (secondsErrorCode != null) {
      if (degreesErrorCode == null && minutesErrorCode == null) {
        errors.rejectValue(
            "%s.%s".formatted(fieldPrefix, SECONDS_FIELD_SUFFIX),
            secondsErrorCode,
            errorMessage
        );
      } else {
        errors.rejectValue(
            "%s.%s".formatted(fieldPrefix, SECONDS_FIELD_SUFFIX),
            secondsErrorCode,
            ""
        );
      }
    }
  }
}
