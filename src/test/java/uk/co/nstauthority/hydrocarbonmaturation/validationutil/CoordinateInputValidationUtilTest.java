package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class CoordinateInputValidationUtilTest {

  @Test
  void validateCoordinate_allNull() {
    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(new CoordinateInput(), null);
    var bindingResult = validateCoordinateInput(form, new CoordinateInput());

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
    assertThat(errorList.size()).isEqualTo(3);
  }

  @Test
  void validateCoordinate_partiallyNull() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("5"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
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
    var bindingResult = validateCoordinateInputWithHemisphere(form, new CoordinateInput(), null);

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

    var bindingResult = validateCoordinateInputWithHemisphere(form, coordinateInput, null);
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

  @Test
  void validateCoordinate_validDegreesMinutesSeconds() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("5"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validateCoordinate_validDegreesMinutesSecondsHemisphere() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("5"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, Hemisphere.EAST);

    var bindingResult = validateCoordinateInputWithHemisphere(form, coordinateInput, Hemisphere.EAST);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validateCoordinate_degreesLessThanMin() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("-1"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.lessThanMinValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate degrees must be 0 or more");
  }

  @Test
  void validateCoordinate_degreesEqualToMin() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("0"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateCoordinateInput(form, coordinateInput);

    assertThat(bindingResult.hasErrors()).isFalse();
  }


  @Test
  void validateCoordinate_degreesMoreThanMax() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("31"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.moreThanMaxValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate degrees must be 30 or less");
  }

  @Test
  void validateCoordinate_degreesEqualToMax() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("30"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateCoordinateInput(form, coordinateInput);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validateCoordinate_degreesMoreThanMaxDecimalPlaces() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15.2"));
    coordinateInput.setMinutes(new DecimalInput("56"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.degrees.decimalInputValue",
                Set.of("coordinateInput.degrees.decimalInputValue.incorrectDecimalPlaces"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate degrees must have no decimal places");
  }

  @Test
  void validateCoordinate_minutesLessThanMin() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("-1"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.lessThanMinValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate minutes must be 0 or more");
  }

  @Test
  void validateCoordinate_minutesEqualToMin() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("0"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateCoordinateInput(form, coordinateInput);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validateCoordinate_minutesMoreThanMax() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("60"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.moreThanMaxValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate minutes must be 59 or less");
  }

  @Test
  void validateCoordinate_minutesEqualToMax() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("59"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateCoordinateInput(form, coordinateInput);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validateCoordinate_minutesMoreThanMaxDecimalPlaces() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("15.1"));
    coordinateInput.setSeconds(new DecimalInput("56"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.minutes.decimalInputValue",
                Set.of("coordinateInput.minutes.decimalInputValue.incorrectDecimalPlaces"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate minutes must have no decimal places");
  }

  @Test
  void validateCoordinate_secondsLessThanMin() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("-1"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.lessThanMinValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate seconds must be 0 or more");
  }

  @Test
  void validateCoordinate_secondsEqualToMin() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("0"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateCoordinateInput(form, coordinateInput);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validateCoordinate_secondsMoreThanMax() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("60"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.moreThanMaxValue"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo("The coordinate seconds must be 59 or less");
  }

  @Test
  void validateCoordinate_secondsEqualToMax() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("59"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);
    var bindingResult = validateCoordinateInput(form, coordinateInput);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validateCoordinate_secondsMoreThanMaxDecimalPlaces() {
    var coordinateInput = new CoordinateInput();

    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("54.1234"));

    var form = new CoordinateInputValidationUtilTest.CoordinateInputValidationTestForm(coordinateInput, null);

    var bindingResult = validateCoordinateInput(form, coordinateInput);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("coordinateInput.seconds.decimalInputValue",
                Set.of("coordinateInput.seconds.decimalInputValue.incorrectDecimalPlaces"))
        );

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage())
        .isEqualTo("The coordinate seconds must have no more than 3 decimal places");
  }

  private BindingResult validateCoordinateInput(CoordinateInputValidationTestForm form, CoordinateInput coordinateInput) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    CoordinateInputValidationUtil.validateCoordinateInput(bindingResult, coordinateInput,
        "coordinateInput", "coordinate");
    return bindingResult;
  }

  private BindingResult validateCoordinateInputWithHemisphere(CoordinateInputValidationTestForm form,
                                                              CoordinateInput coordinateInput, Hemisphere hemisphere) {

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    CoordinateInputValidationUtil.validateCoordinateInputWithHemisphere(bindingResult, coordinateInput,
        "coordinateInput", "coordinate", hemisphere, "hemisphere");
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