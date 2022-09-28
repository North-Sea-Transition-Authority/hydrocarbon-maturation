package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

public class ProbabilityDistributionValidationUtil {

  public static final String ERROR_MESSAGE =
      "%s - all mandatory probability of exceedence inputs must be provided. " +
          "All inputs must be numbers with up to 2 decimal places.";

  private ProbabilityDistributionValidationUtil() {
    throw new IllegalStateException("ProbabilityDistributionValidationUtil is a util class and should not be instantiated");
  }

  public static void validatePartial(ProbabilityDistributionForm probabilityDistributionForm,
                                     Errors errors,
                                     String formField,
                                     String label) {
    validate(probabilityDistributionForm, errors, formField, label, ValidationType.PARTIAL);
  }

  public static void validateFull(ProbabilityDistributionForm probabilityDistributionForm,
                                  Errors errors,
                                  String formField,
                                  String label) {
    validate(probabilityDistributionForm, errors, formField, label, ValidationType.FULL);
  }

  private static void validate(ProbabilityDistributionForm probabilityDistributionForm,
                               Errors errors,
                               String formField,
                               String label,
                               ValidationType validationType
  ) {
    int errorCountBefore = errors.getErrorCount();
    if (!Objects.isNull(probabilityDistributionForm.getProbabilityDistributionType())) {
      var instance = probabilityDistributionForm.getProbabilityDistributionFieldInstance();
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
          validationType,
          errors
      );

      int errorCountAfter = errors.getErrorCount();
      if (errorCountAfter > errorCountBefore) {
        String errorPath = formField + "." + typePath + ".error";
        errorPath = errorPath.replaceAll("[\n\r\t]", "_");
        errors.rejectValue(
            errorPath,
            errorPath + ".error",
            ERROR_MESSAGE.formatted(label)
        );
      }
    }
  }

  private static void validateInput(
      boolean isAvailable,
      boolean isRequired,
      String formField,
      String typePath,
      String inputPath,
      DecimalInput input,
      ValidationType validationType,
      Errors errors
  ) {
    if (isAvailable) {
      var pathToExceedance = formField + "." + typePath + "." + inputPath + ".decimalInputValue";
      pathToExceedance = pathToExceedance.replaceAll("[\n\r\t]", "_");
      var validator = ProbabilityDistributionValidationUtil.configureProbabilityOfExceedanceInputValidator(
          input,
          pathToExceedance,
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
  }

  private static DecimalInputValidator configureProbabilityOfExceedanceInputValidator(
      DecimalInput input,
      String fieldPath,
      Errors errors
  ) {
    return DecimalInputValidator.configureValidator(errors, fieldPath, "", input)
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.valueOf(0))
        .overrideDefaultErrorMessage("");
  }
}
