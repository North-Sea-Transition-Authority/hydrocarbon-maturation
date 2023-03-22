package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateType;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class CoordinateInputValidationUtilTest {

  @Test
  void validateCoordinate_allNull() {
    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(new CoordinateInput(), null);
    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, new CoordinateInput(), CoordinateType.LATITUDE);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.required")),
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.required")),
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.required"))
        );

    var errorList = bindingResult.getAllErrors();

    assertThat(errorList.get(0).getDefaultMessage()).isEqualTo("Enter a coordinate");
    assertThat(errorList.get(1).getDefaultMessage()).isEmpty();
    assertThat(errorList.get(2).getDefaultMessage()).isEmpty();
    assertThat(errorList).hasSize(3);
  }

  @Test
  void validateCoordinate_partiallyNull() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("5"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, CoordinateType.LATITUDE);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.required"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("Enter a complete coordinate");
  }

  @Test
  void validateCoordinate_allNull_withHemisphere() {
    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(new CoordinateInput(), null);
    var bindingResult = validateMandatoryCoordinateInputWithHemisphere(form, new CoordinateInput(), null, CoordinateType.LATITUDE);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.required")),
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.required")),
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.required")),
            entry("hemisphere",
                Set.of("hemisphere.required"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("Enter a coordinate");
  }

  @Test
  void validateCoordinate_partiallyNull_withHemisphere() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("5"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputWithHemisphere(form, coordinateInput, null, CoordinateType.LATITUDE);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.required")),
            entry("hemisphere",
                Set.of("hemisphere.required"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("Enter a complete coordinate");
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_validDegreesMinutesSeconds(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45" : "5"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_validDegreesMinutesSecondsHemisphere(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45" : "5"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, Hemisphere.EAST);

    var bindingResult = validateMandatoryCoordinateInputWithHemisphere(form, coordinateInput, Hemisphere.EAST, coordinateType);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_degreesLessThanMin(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("-1"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.lessThanMinValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo(
        CoordinateType.LATITUDE.equals(coordinateType)
            ? "The coordinate degrees must be 45 or more"
            : "The coordinate degrees must be 0 or more"
    );
  }


  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_degreesMoreThanMax(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "65" : "31"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.moreThanMaxValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo(
        CoordinateType.LATITUDE.equals(coordinateType)
            ? "The coordinate degrees must be 64 or less"
            : "The coordinate degrees must be 30 or less"
    );
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_degreesMoreThanMaxDecimalPlaces(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45.2" : "15.2"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.incorrectDecimalPlaces"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate degrees must have no decimal places");
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_minutesLessThanMin(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45" : "15"));
    coordinateInput.setMinutes(new DecimalInput("-1"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.lessThanMinValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate minutes must be 0 or more");
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_minutesMoreThanMax(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45" : "15"));
    coordinateInput.setMinutes(new DecimalInput("60"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.moreThanMaxValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate minutes must be 59 or less");
  }

  @ParameterizedTest
  @CsvSource({
      "15, 59, 56",
      "15, 15, 0",
      "15, 59, 59.999",
      "15, 0, 56",
      "30, 56, 56",
      "0, 56, 56",
  })
  void validateLongitudeCoordinate_withValidInputs(String degrees, String minutes, String seconds) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(degrees));
    coordinateInput.setMinutes(new DecimalInput(minutes));
    coordinateInput.setSeconds(new DecimalInput(seconds));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, CoordinateType.LONGITUDE);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
      "50, 59, 56",
      "50, 15, 0",
      "50, 59, 59.999",
      "50, 0, 56",
      "45, 56, 56",
      "64, 56, 56",
  })
  void validateLatitudeCoordinate_withValidInputs(String degrees, String minutes, String seconds) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(degrees));
    coordinateInput.setMinutes(new DecimalInput(minutes));
    coordinateInput.setSeconds(new DecimalInput(seconds));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, CoordinateType.LATITUDE);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_minutesMoreThanMaxDecimalPlaces(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45" : "15"));
    coordinateInput.setMinutes(new DecimalInput("15.1"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.incorrectDecimalPlaces"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate minutes must have no decimal places");
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_secondsLessThanMin(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45" : "15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("-1"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.lessThanMinValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate seconds must be 0 or more");
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_secondsMoreThanMax(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45" : "15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("60"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.moreThanMaxValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate seconds must be 59.999 or less");
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_secondsMoreThanMaxDecimalPlaces(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput(CoordinateType.LATITUDE.equals(coordinateType) ? "45" : "15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("54.1234"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateMandatoryCoordinateInputNoHemisphere(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.incorrectDecimalPlaces"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage())
        .isEqualTo("The coordinate seconds must have no more than 3 decimal places");
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_optional_nullCoordinates_valid(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateOptionalCoordinateInput(form, coordinateInput, coordinateType);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_optional_withNoCoordinatesEntered_thenValid(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateOptionalCoordinateInput(form, coordinateInput, coordinateType);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @ParameterizedTest
  @EnumSource(CoordinateType.class)
  void validateCoordinate_optional_withCoordinatesPartiallyEntered_thenInvalid(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("15"));
    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateOptionalCoordinateInput(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.required"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("Enter a complete coordinate");
  }

  @ParameterizedTest
  @EnumSource(value = CoordinateType.class)
  void validateCoordinate_optional_invalidCoordinates(CoordinateType coordinateType) {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("dsazfvsfa"));
    coordinateInput.setMinutes(new DecimalInput("123"));
    coordinateInput.setSeconds(new DecimalInput("54.1234"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateOptionalCoordinateInput(form, coordinateInput, coordinateType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.notNumeric")),
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.moreThanMaxValue")),
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  private BindingResult validateOptionalCoordinateInput(CoordinateInputValidationTestForm form, CoordinateInput coordinateInput, CoordinateType coordinateType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    CoordinateInputValidationUtil.validateOptionalCoordinateInput(
        bindingResult,
        coordinateInput,
        "coordinateInput",
        "coordinate",
        coordinateType
    );
    return bindingResult;
  }

  private BindingResult validateMandatoryCoordinateInputNoHemisphere(CoordinateInputValidationTestForm form, CoordinateInput coordinateInput, CoordinateType coordinateType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    CoordinateInputValidationUtil.validateMandatoryCoordinateInputNoHemisphere(
        bindingResult,
        coordinateInput,
        "coordinateInput",
        "coordinate",
        coordinateType
    );
    return bindingResult;
  }

  private BindingResult validateMandatoryCoordinateInputWithHemisphere(CoordinateInputValidationTestForm form,
                                                                       CoordinateInput coordinateInput,
                                                                       Hemisphere hemisphere,
                                                                       CoordinateType coordinateType) {

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    CoordinateInputValidationUtil.validateMandatoryCoordinateInputWithHemisphere(
        bindingResult,
        coordinateInput,
        "coordinateInput",
        "coordinate",
        hemisphere,
        "hemisphere",
        coordinateType);
    return bindingResult;
  }

  private static class CoordinateInputValidationTestForm {

    private CoordinateInput coordinateInput;
    private Hemisphere hemisphere;

    CoordinateInputValidationTestForm(CoordinateInput coordinateInput, Hemisphere hemisphere) {
      this.coordinateInput = coordinateInput;
      this.hemisphere = hemisphere;
    }

    public CoordinateInput getCoordinateInput() {
      return coordinateInput;
    }

    public void setCoordinateInput(CoordinateInput coordinateInput) {
      this.coordinateInput = coordinateInput;
    }

    public Hemisphere getHemisphere() {
      return hemisphere;
    }

    public void setHemisphere(Hemisphere hemisphere) {
      this.hemisphere = hemisphere;
    }
  }

}