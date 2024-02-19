package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

public class ProbabilityDistributionValidationUtil {

  private static final String ERROR_CODE = ".error";
  private static final String DECIMAL_INPUT_VALUE_PATH = ".decimalInputValue";

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

      // validate 1P input
      validateInput(
          behaviour.is1PAvailable(),
          behaviour.is1PRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.ONE_P.getPath(),
          probabilityOfExceedanceForm.getOneP(),
          units,
          validationType,
          errors
      );

      // validate 2P input
      validateInput(
          behaviour.is2PAvailable(),
          behaviour.is2PRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.TWO_P.getPath(),
          probabilityOfExceedanceForm.getTwoP(),
          units,
          validationType,
          errors
      );

      // validate 3P input
      validateInput(
          behaviour.is3PAvailable(),
          behaviour.is3PRequired(),
          formField,
          typePath,
          ProbabilityOfExceedanceType.THREE_P.getPath(),
          probabilityOfExceedanceForm.getThreeP(),
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
                ? ProbabilityDistributionValidationError.PERCENTAGE_ERROR.getErrorMessage().formatted(label)
                : ProbabilityDistributionValidationError.GENERAL_ERROR.getErrorMessage().formatted(label)
        );
      }

      var allowEquivalentValues = behaviour.allowEquivalentValues();

      validateMinMax(
          behaviour,
          probabilityOfExceedanceForm,
          formTypePath,
          label,
          allowEquivalentValues,
          errors,
          errorPath,
          errorCountBefore
      );

      validateP99ToP1(
          behaviour,
          probabilityOfExceedanceForm,
          formTypePath,
          label,
          allowEquivalentValues,
          errors,
          errorPath,
          errorCountBefore
      );

      validateP90ToP10(
          behaviour,
          probabilityOfExceedanceForm,
          formTypePath,
          label,
          allowEquivalentValues,
          errors,
          errorPath,
          errorCountBefore);

      validate1CTo3C(
          behaviour,
          probabilityOfExceedanceForm,
          formTypePath,
          label,
          allowEquivalentValues,
          errors,
          errorPath,
          errorCountBefore);

      validate1PTo3P(
          behaviour,
          probabilityOfExceedanceForm,
          formTypePath,
          label,
          allowEquivalentValues,
          errors,
          errorPath,
          errorCountBefore
      );
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
      boolean allowEquivalentValues,
      Errors errors
  ) {
    if (!isInOrder(inputs, allowEquivalentValues)) {
      paths.forEach(p -> errors.rejectValue(p, p + ".invalid", ""));
    }

  }

  /** Takes an already ordered list of inputs and validates that the decimal values are in ascending order.
   * @param inputs the input values to check
   * @return whether the decimal input list is in the correct order
   */
  private static boolean isInOrder(
      ArrayList<DecimalInput> inputs,
      boolean allowEquivalentValues
  ) {
    for (int i = 0; i < inputs.size() - 1; i++) {
      var inputComparison = inputs.get(i).createBigDecimalOrNull().compareTo(inputs.get(i + 1).createBigDecimalOrNull());
      if (
          (!allowEquivalentValues && inputComparison >= 0)
          ||
          (allowEquivalentValues && inputComparison > 0)
      ) {
        return false;
      }
    }
    return true;
  }

  private static String cleanPath(String path) {
    return path.replaceAll("[\n\r\t]", "_");
  }

  /** Min and max validation
   * Validate min and max values are in correct order if they have been provided
   * and no other errors have been found so far.
   * Do not validate if the min and max values can be the same based on the allowEquivalentValues behaviour
   */
  private static void validateMinMax(
      ProbabilityDistributionTypeBehaviour behaviour,
      ProbabilityOfExceedanceForm probabilityOfExceedanceForm,
      String formTypePath,
      String label,
      boolean allowEquivalentValues,
      Errors errors,
      String errorPath,
      int errorCountBefore) {

    if (Objects.isNull(behaviour.getMinMaxError())) {
      return;
    }

    var errorCountAfter = errors.getErrorCount();
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

      validateProbabilityFieldOrder(minMaxValues, minMaxPaths, allowEquivalentValues, errors);

      errorCountAfter = errors.getErrorCount();
      if (errorCountAfter > errorCountBefore) {
        errors.rejectValue(
            errorPath,
            errorPath + ERROR_CODE,
            behaviour.getMinMaxError().getErrorMessage().formatted(label)
        );
      }
    }
  }

  private static void validateP99ToP1(
      ProbabilityDistributionTypeBehaviour behaviour,
      ProbabilityOfExceedanceForm probabilityOfExceedanceForm,
      String formTypePath,
      String label,
      boolean allowEquivalentValues,
      Errors errors,
      String errorPath,
      int errorCountBefore) {

    if (Objects.isNull(behaviour.getP99ToP1Error())) {
      return;
    }

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

    var errorCountAfter = errors.getErrorCount();
    // validate P99 to P1 values if any have been entered and no other errors have been found so far
    if (!filteredProbabilityValues.isEmpty() && errorCountAfter == errorCountBefore) {
      validateProbabilityFieldOrder(filteredProbabilityValues, pathList, allowEquivalentValues, errors);

      errorCountAfter = errors.getErrorCount();
      if (errorCountAfter > errorCountBefore) {
        errors.rejectValue(
            errorPath,
            errorPath + ERROR_CODE,
            behaviour.getP99ToP1Error().getErrorMessage().formatted(label)
        );
      }
    }
  }

  private static void validateP90ToP10(
      ProbabilityDistributionTypeBehaviour behaviour,
      ProbabilityOfExceedanceForm probabilityOfExceedanceForm,
      String formTypePath,
      String label,
      boolean allowEquivalentValues,
      Errors errors,
      String errorPath,
      int errorCountBefore) {

    if (Objects.isNull(behaviour.getP90ToP10Error())) {
      return;
    }

    var probabilityValues = List.of(
        probabilityOfExceedanceForm.getP90(),
        probabilityOfExceedanceForm.getP50(),
        probabilityOfExceedanceForm.getP10()
    );
    var filteredProbabilityValues = probabilityValues.stream()
        .filter(DecimalInput::hasContent)
        .collect(Collectors.toCollection(ArrayList::new));

    var pathList = new ArrayList<String>();
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

    var errorCountAfter = errors.getErrorCount();
    // validate P99 to P1 values if any have been entered and no other errors have been found so far
    if (!filteredProbabilityValues.isEmpty() && errorCountAfter == errorCountBefore) {
      validateProbabilityFieldOrder(filteredProbabilityValues, pathList, allowEquivalentValues, errors);

      errorCountAfter = errors.getErrorCount();
      if (errorCountAfter > errorCountBefore) {
        errors.rejectValue(
            errorPath,
            errorPath + ERROR_CODE,
            behaviour.getP90ToP10Error().getErrorMessage().formatted(label)
        );
      }
    }
  }

  private static void validate1CTo3C(
      ProbabilityDistributionTypeBehaviour behaviour,
      ProbabilityOfExceedanceForm probabilityOfExceedanceForm,
      String formTypePath,
      String label,
      boolean allowEquivalentValues,
      Errors errors,
      String errorPath,
      int errorCountBefore
  ) {

    if (Objects.isNull(behaviour.get1CTo3CError())) {
      return;
    }

    var probabilityValues = List.of(
        probabilityOfExceedanceForm.getOneC(),
        probabilityOfExceedanceForm.getTwoC(),
        probabilityOfExceedanceForm.getThreeC()
    );
    var filteredProbabilityValues = probabilityValues.stream()
        .filter(DecimalInput::hasContent)
        .collect(Collectors.toCollection(ArrayList::new));

    var pathList = new ArrayList<String>();
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

    var errorCountAfter = errors.getErrorCount();
    // validate 3C to 1C values if any have been entered and no other errors have been found so far
    if (!filteredProbabilityValues.isEmpty() && errorCountAfter == errorCountBefore) {
      validateProbabilityFieldOrder(filteredProbabilityValues, pathList, allowEquivalentValues, errors);

      errorCountAfter = errors.getErrorCount();
      if (errorCountAfter > errorCountBefore) {
        errors.rejectValue(
            errorPath,
            errorPath + ERROR_CODE,
            behaviour.get1CTo3CError().getErrorMessage().formatted(label)
        );
      }
    }
  }

  private static void validate1PTo3P(
      ProbabilityDistributionTypeBehaviour behaviour,
      ProbabilityOfExceedanceForm probabilityOfExceedanceForm,
      String formTypePath,
      String label,
      boolean allowEquivalentValues,
      Errors errors,
      String errorPath,
      int errorCountBefore
  ) {

    if (Objects.isNull(behaviour.get1PTo3PError())) {
      return;
    }

    var probabilityValues = List.of(
        probabilityOfExceedanceForm.getOneP(),
        probabilityOfExceedanceForm.getTwoP(),
        probabilityOfExceedanceForm.getThreeP()
    );
    var filteredProbabilityValues = probabilityValues.stream()
        .filter(DecimalInput::hasContent)
        .collect(Collectors.toCollection(ArrayList::new));

    var pathList = new ArrayList<String>();
    if (probabilityOfExceedanceForm.getOneP().hasContent()) {
      var onePPath = cleanPath(formTypePath + ProbabilityOfExceedanceType.ONE_P.getPath() + DECIMAL_INPUT_VALUE_PATH);
      pathList.add(onePPath);
    }
    if (probabilityOfExceedanceForm.getTwoP().hasContent()) {
      var twoPPath = cleanPath(formTypePath + ProbabilityOfExceedanceType.TWO_P.getPath() + DECIMAL_INPUT_VALUE_PATH);
      pathList.add(twoPPath);
    }
    if (probabilityOfExceedanceForm.getThreeP().hasContent()) {
      var threePPath = cleanPath(formTypePath + ProbabilityOfExceedanceType.THREE_P.getPath() + DECIMAL_INPUT_VALUE_PATH);
      pathList.add(threePPath);
    }

    var errorCountAfter = errors.getErrorCount();
    // validate 3P to 1P values if any have been entered and no other errors have been found so far
    if (!filteredProbabilityValues.isEmpty() && errorCountAfter == errorCountBefore) {
      validateProbabilityFieldOrder(filteredProbabilityValues, pathList, allowEquivalentValues, errors);

      errorCountAfter = errors.getErrorCount();
      if (errorCountAfter > errorCountBefore) {
        errors.rejectValue(
            errorPath,
            errorPath + ERROR_CODE,
            behaviour.get1PTo3PError().getErrorMessage().formatted(label)
        );
      }
    }
  }
}
