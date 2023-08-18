package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

public class ProbabilityDistributionValidationUtil {

  private static final String ERROR_CODE = ".error";
  private static final String DECIMAL_INPUT_VALUE_PATH = ".decimalInputValue";

  public static final String GENERAL_ERROR_MESSAGE =
      "%s - all mandatory probability of exceedence inputs must be provided. " +
          "All inputs must be numbers with up to 2 decimal places.";

  public static final String PERCENTAGE_ERROR_MESSAGE =
      GENERAL_ERROR_MESSAGE + " Percentages must not be more than 100 percent.";

  public static final String MIN_MAX_ERROR_MESSAGE =
      "%s - min value must be less than max value";

  public static final String P99_TO_P1_ERROR_MESSAGE =
      "%s - P99 to P1 values must be in the order of P99 < P90 < P50 < P10 < P1";

  public static final String ONE_C_TO_THREE_C_ERROR_MESSAGE =
      "%s - 1C to 3C values must be in the order of 1C < 2C < 3C";

  private ProbabilityDistributionValidationUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static void validatePartial(ProbabilityDistributionForm probabilityDistributionForm,
                                     Errors errors,
                                     String formField,
                                     String label) {
    validate(probabilityDistributionForm, errors, formField, label, null, ValidationType.PARTIAL, null);
  }

  public static void validateCustomPartial(ProbabilityDistributionForm probabilityDistributionForm,
                                           Errors errors,
                                           String formField,
                                           String label,
                                           ProbabilityDistributionFieldInstance instance) {
    validate(probabilityDistributionForm, errors, formField, label, null, ValidationType.PARTIAL, instance);
  }

  public static void validatePartialPercentage(ProbabilityDistributionForm probabilityDistributionForm,
                                               Errors errors,
                                               String formField,
                                               String label) {
    validate(probabilityDistributionForm, errors, formField, label, ProbabilityOfExceedanceUnits.PERCENT,
        ValidationType.PARTIAL,
        null);
  }

  public static void validateCustomPartialPercentage(ProbabilityDistributionForm probabilityDistributionForm,
                                                     Errors errors,
                                                     String formField,
                                                     String label,
                                                     ProbabilityDistributionFieldInstance instance) {
    validate(probabilityDistributionForm, errors, formField, label, ProbabilityOfExceedanceUnits.PERCENT,
        ValidationType.PARTIAL,
        instance);
  }

  public static void validateFull(ProbabilityDistributionForm probabilityDistributionForm,
                                  Errors errors,
                                  String formField,
                                  String label) {
    validate(probabilityDistributionForm, errors, formField, label, null, ValidationType.FULL, null);
  }

  public static void validateFullPercentage(ProbabilityDistributionForm probabilityDistributionForm,
                                            Errors errors,
                                            String formField,
                                            String label) {
    validate(probabilityDistributionForm, errors, formField, label, ProbabilityOfExceedanceUnits.PERCENT,
        ValidationType.FULL,
        null);
  }

  public static void validateCustomFullPercentage(ProbabilityDistributionForm probabilityDistributionForm,
                                                  Errors errors,
                                                  String formField,
                                                  String label,
                                                  ProbabilityDistributionFieldInstance instance) {
    validate(probabilityDistributionForm, errors, formField, label, ProbabilityOfExceedanceUnits.PERCENT,
        ValidationType.FULL,
        instance);
  }

  public static void validateCustomFull(ProbabilityDistributionForm probabilityDistributionForm,
                                        Errors errors,
                                        String formField,
                                        String label,
                                        ProbabilityDistributionFieldInstance instance) {
    validate(probabilityDistributionForm, errors, formField, label, null, ValidationType.FULL, instance);
  }

  private static void validate(ProbabilityDistributionForm probabilityDistributionForm,
                               Errors errors,
                               String formField,
                               String label,
                               ProbabilityOfExceedanceUnits units,
                               ValidationType validationType,
                               @Nullable ProbabilityDistributionFieldInstance instance
  ) {
    var errorCountBefore = errors.getErrorCount();
    if (!Objects.isNull(probabilityDistributionForm.getProbabilityDistributionType())) {
      var probabilityDistributionType = probabilityDistributionForm.getProbabilityDistributionType();
      var typePath = probabilityDistributionType.getPath();
      var behaviour = ProbabilityDistributionTypeBehaviourUtil.getBehaviour(instance, probabilityDistributionType);
      var probabilityOfExceedanceForm = probabilityDistributionForm.getProbabilityOfExceedanceForm();

      // validate Min input
      validateInput(
          behaviour.isMinAvailable(),
          behaviour.isMinRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.MIN.getPath(),
          probabilityOfExceedanceForm.getMin(),
          units,
          validationType,
          errors
      );

      // validate P99 input
      validateInput(
          behaviour.isP99Available(),
          behaviour.isP99Required(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.P99.getPath(),
          probabilityOfExceedanceForm.getP99(),
          units,
          validationType,
          errors
      );

      // validate P90 input
      validateInput(
          behaviour.isP90Available(),
          behaviour.isP90Required(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.P90.getPath(),
          probabilityOfExceedanceForm.getP90(),
          units,
          validationType,
          errors
      );

      // validate 3C input
      validateInput(
          behaviour.is3CAvailable(),
          behaviour.is3CRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.THREE_C.getPath(),
          probabilityOfExceedanceForm.getThreeC(),
          units,
          validationType,
          errors
      );

      // validate Mode input
      validateInput(
          behaviour.isModeAvailable(),
          behaviour.isModeRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.MODE.getPath(),
          probabilityOfExceedanceForm.getMode(),
          units,
          validationType,
          errors
      );

      // validate P50 input
      validateInput(
          behaviour.isP50Available(),
          behaviour.isP50Required(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.P50.getPath(),
          probabilityOfExceedanceForm.getP50(),
          units,
          validationType,
          errors
      );

      // validate 2C input
      validateInput(
          behaviour.is2CAvailable(),
          behaviour.is2CRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.TWO_C.getPath(),
          probabilityOfExceedanceForm.getTwoC(),
          units,
          validationType,
          errors
      );

      // validate Mean input
      validateInput(
          behaviour.isMeanAvailable(),
          behaviour.isMeanRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.MEAN.getPath(),
          probabilityOfExceedanceForm.getMean(),
          units,
          validationType,
          errors
      );

      // validate P10 input
      validateInput(
          behaviour.isP10Available(),
          behaviour.isP10Required(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.P10.getPath(),
          probabilityOfExceedanceForm.getP10(),
          units,
          validationType,
          errors
      );

      // validate 1C input
      validateInput(
          behaviour.is1CAvailable(),
          behaviour.is1CRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.ONE_C.getPath(),
          probabilityOfExceedanceForm.getOneC(),
          units,
          validationType,
          errors
      );

      // validate P1 input
      validateInput(
          behaviour.isP1Available(),
          behaviour.isP1Required(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.P1.getPath(),
          probabilityOfExceedanceForm.getP1(),
          units,
          validationType,
          errors
      );

      //validate most likely input
      validateInput(
          behaviour.isMostLikelyAvailable(),
          behaviour.isMostLikelyRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.MOST_LIKELY.getPath(),
          probabilityOfExceedanceForm.getMostLikely(),
          units,
          validationType,
          errors
      );

      // validate Max input
      validateInput(
          behaviour.isMaxAvailable(),
          behaviour.isMaxRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.MAX.getPath(),
          probabilityOfExceedanceForm.getMax(),
          units,
          validationType,
          errors
      );

      // validate Constant input
      validateInput(
          behaviour.isConstantAvailable(),
          behaviour.isConstantRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.CONSTANT.getPath(),
          probabilityOfExceedanceForm.getConstant(),
          units,
          validationType,
          errors
      );

      var formTypePath = formField + "." + typePath + ".";
      var errorPath = cleanPath(formTypePath + "error");
      var errorCountAfter = errors.getErrorCount();

      if (errorCountAfter > errorCountBefore) {
        errors.rejectValue(
            errorPath,
            errorPath + ERROR_CODE,
            units == ProbabilityOfExceedanceUnits.PERCENT
                ? PERCENTAGE_ERROR_MESSAGE.formatted(label)
                : GENERAL_ERROR_MESSAGE.formatted(label)
        );
      }

      /* Min and max validation */
      // Validate min and max values are in correct order if they have been provided
      // and no other errors have been found so far
      errorCountAfter = errors.getErrorCount();
      if (probabilityOfExceedanceForm.getMin().hasContent()
          && probabilityOfExceedanceForm.getMax().hasContent()
          && errorCountAfter == errorCountBefore
      ) {

        var minPath = cleanPath(formTypePath + ProbabilityOfExceedanceType.MIN.getPath() + DECIMAL_INPUT_VALUE_PATH);
        var maxPath = cleanPath(formTypePath + ProbabilityOfExceedanceType.MAX.getPath() + DECIMAL_INPUT_VALUE_PATH);

        var minMaxValues = new ArrayList<>(
            Arrays.asList(
                probabilityOfExceedanceForm.getMin(),
                probabilityOfExceedanceForm.getMax()
            )
        );

        var minMaxPaths = new ArrayList<>(
            Arrays.asList(
                minPath,
                maxPath
            )
        );

        validateProbabilityFieldOrder(minMaxValues, minMaxPaths, errors);

        errorCountAfter = errors.getErrorCount();
        if (errorCountAfter > errorCountBefore) {
          errors.rejectValue(
              errorPath,
              errorPath + ERROR_CODE,
              MIN_MAX_ERROR_MESSAGE.formatted(label)
          );
        }
      }

      /* P99 to P1 validation */
      var probabilityValues = List.of(
          probabilityOfExceedanceForm.getP99(),
          probabilityOfExceedanceForm.getP90(),
          probabilityOfExceedanceForm.getP50(),
          probabilityOfExceedanceForm.getP10(),
          probabilityOfExceedanceForm.getP1()
      );
      var filteredProbabilityValues = probabilityValues.stream()
          .filter(DecimalInput::hasContent)
          .collect(Collectors.toCollection(ArrayList::new));

      var pathList = new ArrayList<String>();
      if (probabilityOfExceedanceForm.getP1().hasContent()) {
        var p1Path = cleanPath(formTypePath + ProbabilityOfExceedanceType.P1.getPath() + DECIMAL_INPUT_VALUE_PATH);
        pathList.add(p1Path);
      }
      if (probabilityOfExceedanceForm.getP10().hasContent()) {
        var p10Path = cleanPath(formTypePath + ProbabilityOfExceedanceType.P10.getPath() + DECIMAL_INPUT_VALUE_PATH);
        pathList.add(p10Path);
      }
      if (probabilityOfExceedanceForm.getP50().hasContent()) {
        var p50Path = cleanPath(formTypePath + ProbabilityOfExceedanceType.P50.getPath() + DECIMAL_INPUT_VALUE_PATH);
        pathList.add(p50Path);
      }
      if (probabilityOfExceedanceForm.getP90().hasContent()) {
        var p90Path = cleanPath(formTypePath + ProbabilityOfExceedanceType.P90.getPath() + DECIMAL_INPUT_VALUE_PATH);
        pathList.add(p90Path);
      }
      if (probabilityOfExceedanceForm.getP99().hasContent()) {
        var p99Path = cleanPath(formTypePath + ProbabilityOfExceedanceType.P99.getPath() + DECIMAL_INPUT_VALUE_PATH);
        pathList.add(p99Path);
      }

      errorCountAfter = errors.getErrorCount();

      // validate P99 to P1 values if any have been entered and no other errors have been found so far
      if (!filteredProbabilityValues.isEmpty() && errorCountAfter == errorCountBefore) {
        validateProbabilityFieldOrder(filteredProbabilityValues, pathList, errors);

        errorCountAfter = errors.getErrorCount();
        if (errorCountAfter > errorCountBefore) {
          errors.rejectValue(
              errorPath,
              errorPath + ERROR_CODE,
              P99_TO_P1_ERROR_MESSAGE.formatted(label)
          );
        }
      }

      /* 1C to 3C validation */
      probabilityValues = List.of(
          probabilityOfExceedanceForm.getOneC(),
          probabilityOfExceedanceForm.getTwoC(),
          probabilityOfExceedanceForm.getThreeC()
          );
      filteredProbabilityValues = probabilityValues.stream()
          .filter(DecimalInput::hasContent)
          .collect(Collectors.toCollection(ArrayList::new));

      pathList = new ArrayList<String>();
      if (probabilityOfExceedanceForm.getOneC().hasContent()) {
        var oneCPath = cleanPath(formTypePath + ProbabilityOfExceedanceType.ONE_C.getPath() + DECIMAL_INPUT_VALUE_PATH);
        pathList.add(oneCPath);
      }
      if (probabilityOfExceedanceForm.getTwoC().hasContent()) {
        var twoCPath = cleanPath(formTypePath + ProbabilityOfExceedanceType.TWO_C.getPath() + DECIMAL_INPUT_VALUE_PATH);
        pathList.add(twoCPath);
      }
      if (probabilityOfExceedanceForm.getThreeC().hasContent()) {
        var threeCPath = cleanPath(formTypePath + ProbabilityOfExceedanceType.THREE_C.getPath() + DECIMAL_INPUT_VALUE_PATH);
        pathList.add(threeCPath);
      }

      errorCountAfter = errors.getErrorCount();

      // validate 3C to 1C values if any have been entered and no other errors have been found so far
      if (!filteredProbabilityValues.isEmpty() && errorCountAfter == errorCountBefore) {
        validateProbabilityFieldOrder(filteredProbabilityValues, pathList, errors);

        errorCountAfter = errors.getErrorCount();
        if (errorCountAfter > errorCountBefore) {
          errors.rejectValue(
              errorPath,
              errorPath + ERROR_CODE,
              ONE_C_TO_THREE_C_ERROR_MESSAGE.formatted(label)
          );
        }
      }
    }
  }

  static void validateInput(
      boolean isAvailable,
      boolean isRequired,
      String formField,
      String typePath,
      String inputPath,
      DecimalInput input,
      ProbabilityOfExceedanceUnits units,
      ValidationType validationType,
      Errors errors
  ) {
    if (!isAvailable) {
      return;
    }
    var pathToExceedance = cleanPath(formField + "." + typePath + "." + inputPath + DECIMAL_INPUT_VALUE_PATH);
    var validator = ProbabilityDistributionValidationUtil.configureProbabilityOfExceedanceInputValidator(
        input,
        pathToExceedance,
        units,
        errors
    );
    if (validationType.equals(ValidationType.FULL)) {
      if (isRequired) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, pathToExceedance, pathToExceedance + ".required", "");
        validator.validate();
      } else {
        validator.isOptional().validate();
      }
    } else if (validationType.equals(ValidationType.PARTIAL)) {
      validator.isOptional().validate();
    }
  }

  private static DecimalInputValidator configureProbabilityOfExceedanceInputValidator(
      DecimalInput input,
      String fieldPath,
      ProbabilityOfExceedanceUnits units,
      Errors errors
  ) {
    var decimalInputValidator = DecimalInputValidator.configureValidator(errors, fieldPath, "", input)
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.valueOf(0))
        .overrideDefaultErrorMessage("");

    if (units == ProbabilityOfExceedanceUnits.PERCENT) {
      decimalInputValidator.setMaxValue(BigDecimal.valueOf(100));
    }

    return decimalInputValidator;
  }

  /** Validates order of inputs and highlights fields with error in error styling.
   * Blank error message as general error message is provided separately.
   * @param inputs the probability inputs to compare
   * @param paths the paths to inputs to compare
   * @param errors the existing error list
   */
  private static void validateProbabilityFieldOrder(
      ArrayList<DecimalInput> inputs,
      ArrayList<String> paths,
      Errors errors
  ) {
    if (!isInOrder(inputs)) {
      paths.forEach(p -> errors.rejectValue(p, p + ".invalid", ""));
    }

  }

  /** Takes an already ordered list of inputs and validates that the decimal values are in ascending order.
   * @param inputs the input values to check
   * @return whether the decimal input list is in the correct order
   */
  private static boolean isInOrder(
      ArrayList<DecimalInput> inputs
  ) {
    for (int i = 0; i < inputs.size() - 1; i++) {
      if (inputs.get(i).createBigDecimalOrNull().compareTo(inputs.get(i + 1).createBigDecimalOrNull()) > 0) {
        return false;
      }
    }
    return true;
  }

  private static String cleanPath(String path) {
    return path.replaceAll("[\n\r\t]", "_");
  }
}
