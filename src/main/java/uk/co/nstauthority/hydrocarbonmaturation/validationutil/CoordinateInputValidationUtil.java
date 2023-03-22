package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.math.BigDecimal;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateType;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

public class CoordinateInputValidationUtil {

  private static final String LATITUDE_DEFAULT_NAME = "latitude";
  private static final String LONGITUDE_DEFAULT_NAME = "longitude";
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

  public static void validateMandatoryCoordinateInputWithHemisphere(Errors errors, CoordinateInput coordinateInput,
                                                                    String fieldIdPrefix, String displayPrefix,
                                                                    Hemisphere hemisphere, String hemisphereFieldPrefix,
                                                                    CoordinateType coordinateType) {

    validateCoordinateInputWithHemisphere(
        errors,
        coordinateInput,
        fieldIdPrefix,
        displayPrefix,
        hemisphere,
        hemisphereFieldPrefix,
        coordinateType
    );
  }

  public static void validateMandatoryCoordinateInputNoHemisphere(Errors errors,
                                                                  CoordinateInput coordinateInput,
                                                                  String fieldIdPrefix,
                                                                  String displayPrefix,
                                                                  CoordinateType coordinateType) {

    validateCoordinateInput(errors, coordinateInput, fieldIdPrefix, displayPrefix, coordinateType);
  }

  public static void validateOptionalCoordinateInput(Errors errors,
                                                     CoordinateInput coordinateInput,
                                                     String fieldIdPrefix,
                                                     String displayPrefix,
                                                     CoordinateType coordinateType) {

    var degrees = coordinateInput.getDegrees().getDecimalInputValue();
    var minutes = coordinateInput.getMinutes().getDecimalInputValue();
    var seconds = coordinateInput.getSeconds().getDecimalInputValue();

    // Validate coordinates if any of the details have been entered even on partial form save
    if (!ObjectUtils.allNull(degrees, minutes, seconds)) {

      if (ObjectUtils.anyNull(degrees, minutes, seconds)) {
        addPartialNullValidationErrors(
            errors,
            fieldIdPrefix,
            degrees,
            minutes,
            seconds,
            displayPrefix
        );
      } else if (ObjectUtils.allNotNull(degrees, minutes, seconds)) {
        validateDegreesMinutesSeconds(
            errors,
            fieldIdPrefix,
            displayPrefix,
            coordinateInput,
            OverrideErrorMessage.FALSE,
            OptionalMandatory.OPTIONAL,
            coordinateType
        );
      }

    }


  }

  public static void validateOptionalCoordinateInputForm(Errors errors,
                                                         CoordinateInputForm form,
                                                         String latitudeFieldIdPrefix,
                                                         String longitudeFieldIdPrefix,
                                                         String latDisplayPrefix,
                                                         String longDisplayPrefix) {
    CoordinateInputValidationUtil.validateOptionalCoordinateInput(
        errors,
        form.getLatitudeCoordinates(),
        latitudeFieldIdPrefix,
        Objects.requireNonNullElse(latDisplayPrefix, LATITUDE_DEFAULT_NAME),
        CoordinateType.LATITUDE
    );

    CoordinateInputValidationUtil.validateOptionalCoordinateInput(
        errors,
        form.getLongitudeCoordinates(),
        longitudeFieldIdPrefix,
        Objects.requireNonNullElse(longDisplayPrefix, LONGITUDE_DEFAULT_NAME),
        CoordinateType.LONGITUDE
    );
  }

  public static void validateMandatoryCoordinateInputForm(Errors errors,
                                                          CoordinateInputForm form,
                                                          String latitudeFieldIdPrefix,
                                                          String longitudeFieldIdPrefix,
                                                          String latDisplayPrefix,
                                                          String longDisplayPrefix,
                                                          String hemisphereFieldIdPrefix) {
    CoordinateInputValidationUtil.validateMandatoryCoordinateInputNoHemisphere(
        errors,
        form.getLatitudeCoordinates(),
        latitudeFieldIdPrefix,
        Objects.requireNonNullElse(latDisplayPrefix, LATITUDE_DEFAULT_NAME),
        CoordinateType.LATITUDE
    );

    CoordinateInputValidationUtil.validateMandatoryCoordinateInputWithHemisphere(
        errors,
        form.getLongitudeCoordinates(),
        longitudeFieldIdPrefix,
        Objects.requireNonNullElse(longDisplayPrefix, LONGITUDE_DEFAULT_NAME),
        form.getLongitudeHemisphere(),
        hemisphereFieldIdPrefix,
        CoordinateType.LONGITUDE
    );

  }

  /**
   * Validates a coordinate input: degrees, minutes and seconds.
   * @param errors The Errors object to add rejection codes and messages to
   * @param coordinateInput The value input by the user for coordinate degrees, minutes, seconds
   * @param fieldIdPrefix The prefix of the form coordinate fields. E.g. longitudeCoordinates. It's used to bind the errors
   *                      to the form field
   * @param displayPrefix The display version prefix of the form coordinate fields. E.g. for longitudeCoordinates it would be
   *                      longitude. It's used to format the error messages
   */
  private static void validateCoordinateInput(Errors errors,
                                              CoordinateInput coordinateInput,
                                              String fieldIdPrefix,
                                              String displayPrefix,
                                              CoordinateType coordinateType) {

    var degrees = coordinateInput.getDegrees().getDecimalInputValue();
    var minutes = coordinateInput.getMinutes().getDecimalInputValue();
    var seconds = coordinateInput.getSeconds().getDecimalInputValue();


    if (ObjectUtils.allNull(degrees, minutes, seconds)) {
      addAllNullValidationErrors(errors, fieldIdPrefix, degrees, minutes, seconds, displayPrefix);
    } else if (ObjectUtils.anyNull(degrees, minutes, seconds)) {
      addPartialNullValidationErrors(errors, fieldIdPrefix, degrees, minutes, seconds, displayPrefix);
    } else {
      validateDegreesMinutesSeconds(
          errors,
          fieldIdPrefix,
          displayPrefix,
          coordinateInput,
          OverrideErrorMessage.FALSE,
          OptionalMandatory.MANDATORY,
          coordinateType
      );
    }
  }

  /**
   * Validates a coordinate input: degrees, minutes, seconds and hemisphere.
   * @param errors The Errors object to add rejection codes and messages to
   * @param coordinateInput The value input by the user for coordinate degrees, minutes, seconds
   * @param fieldIdPrefix The prefix of the form coordinate fields. E.g. longitudeCoordinates. It's used to bind the errors
   *                      to the form field
   * @param displayPrefix The display version prefix of the form coordinate fields. E.g. for longitudeCoordinates it would be
   *                      longitude. It's used to format the error messages
   * @param hemisphere The value input by the user for coordinate hemisphere
   * @param hemisphereFieldPrefix The prefix of the form hemisphere field.
   */
  private static void validateCoordinateInputWithHemisphere(Errors errors, CoordinateInput coordinateInput, String fieldIdPrefix,
                                                           String displayPrefix, Hemisphere hemisphere,
                                                           String hemisphereFieldPrefix, CoordinateType coordinateType) {

    var degrees = coordinateInput.getDegrees().getDecimalInputValue();
    var minutes = coordinateInput.getMinutes().getDecimalInputValue();
    var seconds = coordinateInput.getSeconds().getDecimalInputValue();

    if (ObjectUtils.allNull(degrees, minutes, seconds)) {
      addAllNullValidationErrors(errors, fieldIdPrefix, degrees, minutes, seconds, displayPrefix);
      validateHemisphere(hemisphere, errors, fieldIdPrefix, hemisphereFieldPrefix,
          displayPrefix);

    } else if (ObjectUtils.anyNull(degrees, minutes, seconds)) {
      addPartialNullValidationErrors(errors, fieldIdPrefix, degrees, minutes, seconds, displayPrefix);
      validateHemisphere(hemisphere, errors, fieldIdPrefix, hemisphereFieldPrefix,
          displayPrefix);

    } else {
      validateHemisphere(hemisphere, errors, fieldIdPrefix, hemisphereFieldPrefix,
          displayPrefix);
      validateDegreesMinutesSeconds(errors, fieldIdPrefix, displayPrefix, coordinateInput,
          OverrideErrorMessage.getFromBoolean(errors.getFieldErrorCount(hemisphereFieldPrefix) != 0),
          OptionalMandatory.MANDATORY, coordinateType);
    }
  }

  private static void validateDegreesMinutesSeconds(Errors errors, String fieldIdPrefix, String displayPrefix,
                                                    CoordinateInput coordinateInput, OverrideErrorMessage overrideErrorMessage,
                                                    OptionalMandatory optionalMandatory, CoordinateType coordinateType) {

    validateDegrees(
        errors,
        fieldIdPrefix,
        displayPrefix,
        coordinateInput.getDegrees(),
        overrideErrorMessage,
        optionalMandatory,
        coordinateType
    );
    validateMinutes(errors, fieldIdPrefix, displayPrefix, coordinateInput.getMinutes(), overrideErrorMessage, optionalMandatory);
    validateSeconds(errors, fieldIdPrefix, displayPrefix, coordinateInput.getSeconds(), overrideErrorMessage, optionalMandatory);
  }

  private static void validateHemisphere(Hemisphere hemisphere, Errors errors, String fieldIdPrefix, String hemisphereFieldPrefix,
                                         String displayPrefix) {
    if (hemisphere == null) {
      if (errors.getFieldErrorCount("%s.minutes.decimalInputValue".formatted(fieldIdPrefix)) == 0
          && errors.getFieldErrorCount("%s.degrees.decimalInputValue".formatted(fieldIdPrefix)) == 0
          && errors.getFieldErrorCount("%s.seconds.decimalInputValue".formatted(fieldIdPrefix)) == 0) {
        errors.rejectValue(
            hemisphereFieldPrefix,
            "%s.required".formatted(hemisphereFieldPrefix),
            HEMISPHERE_REQUIRED_ERROR_MESSAGE.formatted(displayPrefix)
        );
      } else {
        errors.rejectValue(
            hemisphereFieldPrefix,
            "%s.required".formatted(hemisphereFieldPrefix),
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
   *  @param errors The Errors object to add rejection codes and messages to
   * @param fieldIdPrefix The prefix of the form coordinate fields. E.g. longitudeCoordinates. It's used to bind the errors
   *                      to the form field
   * @param displayPrefix The display version prefix of the form coordinate fields. E.g. for longitudeCoordinates it would be
 *                      longitude. It's used to format the error messages
   * @param degrees       The value input by the user for coordinate degrees
   * @param overrideErrorMessage A boolean to determine whether the error message should be overridden.
   * @param optionalMandatory Determines whether the degrees are optional or mandatory
   */
  private static void validateDegrees(Errors errors, String fieldIdPrefix, String displayPrefix, DecimalInput degrees,
                                      OverrideErrorMessage overrideErrorMessage,
                                      OptionalMandatory optionalMandatory, CoordinateType coordinateType) {

    var degreesValidator =
        DecimalInputValidator.configureValidator(errors, "%s.%s".formatted(fieldIdPrefix, DEGREES_FIELD_SUFFIX),
            "The %s degrees".formatted(displayPrefix), degrees)
            .setMaxValue(CoordinateType.LATITUDE.equals(coordinateType) ? UKCS_LATITUDE_MAX_DEGREES : UKCS_LONGITUDE_MAX_DEGREES)
            .setMinValue(CoordinateType.LATITUDE.equals(coordinateType) ? UKCS_LATITUDE_MIN_DEGREES : UKCS_LONGITUDE_MIN_DEGREES)
            .setMaxDecimalPlaces(0);

    if (!degrees.hasContent() || overrideErrorMessage.equals(OverrideErrorMessage.TRUE)) {
      degreesValidator.overrideDefaultErrorMessage("");
    }

    if (optionalMandatory == OptionalMandatory.OPTIONAL) {
      degreesValidator.isOptional();
    }

    degreesValidator.validate();
  }

  /**
   * Validates the individual minutes field for coordinate inputs. It checks whether the degrees fields have errors to
   * determine what the appropriate error message should be.
   *  @param errors The Errors object to add rejection codes and messages to
   * @param fieldIdPrefix The prefix of the form coordinate fields. E.g. longitudeCoordinates. It's used to bind the errors
   *                      to the form field
   * @param displayPrefix The display version prefix of the form coordinate fields. E.g. for longitudeCoordinates it would be
 *                      longitude. It's used to format the error messages
   * @param minutes       The value input by the user for coordinate minutes
   * @param overrideErrorMessage A boolean to determine whether the error message should be overridden.
   * @param optionalMandatory Determines whether the minutes are optional or mandatory
   */
  private static void validateMinutes(Errors errors, String fieldIdPrefix, String displayPrefix, DecimalInput minutes,
                                      OverrideErrorMessage overrideErrorMessage,
                                      OptionalMandatory optionalMandatory) {

    var minutesValidator =
        DecimalInputValidator.configureValidator(errors, "%s.%s".formatted(fieldIdPrefix, MINUTES_FIELD_SUFFIX),
            "The %s minutes".formatted(displayPrefix), minutes)
          .setMaxValue(new BigDecimal("59"))
          .setMinValue(new BigDecimal("0"))
          .setMaxDecimalPlaces(0);

    if (errors.hasFieldErrors("%s.degrees.decimalInputValue".formatted(fieldIdPrefix)) || !minutes.hasContent()
        || overrideErrorMessage.equals(OverrideErrorMessage.TRUE)) {

      minutesValidator.overrideDefaultErrorMessage("");
    }

    if (optionalMandatory == OptionalMandatory.OPTIONAL) {
      minutesValidator.isOptional();
    }

    minutesValidator.validate();
  }

  /**
   *  Validates the individual seconds field for coordinate inputs. It checks whether the degrees and minutes fields have errors
   *  to determine what the appropriate error message should be.
   *  @param errors The Errors object to add rejection codes and messages to
   * @param fieldIdPrefix The prefix of the form coordinate fields. E.g. longitudeCoordinates. It's used to bind the errors
   *                      to the form field
   * @param displayPrefix The display version prefix of the form coordinate fields. E.g. for longitudeCoordinates it would be
 *                      longitude. It's used to format the error messages
   * @param seconds       The value input by the user for coordinate seconds
   * @param overrideErrorMessage A boolean to determine whether the error message should be overridden.
   * @param optionalMandatory Determines whether the seconds are optional or mandatory
   */
  private static void validateSeconds(Errors errors, String fieldIdPrefix, String displayPrefix, DecimalInput seconds,
                                      OverrideErrorMessage overrideErrorMessage,
                                      OptionalMandatory optionalMandatory) {

    var secondsValidator =
        DecimalInputValidator.configureValidator(errors, "%s.%s".formatted(fieldIdPrefix, SECONDS_FIELD_SUFFIX),
            "The %s seconds".formatted(displayPrefix), seconds)
            .setMaxValue(new BigDecimal("59.999"))
            .setMinValue(new BigDecimal("0"))
            .setMaxDecimalPlaces(3);

    if (errors.hasFieldErrors("%s.minutes.decimalInputValue".formatted(fieldIdPrefix))
        || errors.hasFieldErrors("%s.degrees.decimalInputValue".formatted(fieldIdPrefix))
        || !seconds.hasContent() || overrideErrorMessage.equals(OverrideErrorMessage.TRUE)) {

      secondsValidator.overrideDefaultErrorMessage("");
    }

    if (optionalMandatory == OptionalMandatory.OPTIONAL) {
      secondsValidator.isOptional();
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
