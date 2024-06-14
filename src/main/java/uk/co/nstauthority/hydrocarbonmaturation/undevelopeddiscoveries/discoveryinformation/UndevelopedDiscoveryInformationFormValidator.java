package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;

@Service
class UndevelopedDiscoveryInformationFormValidator {

  static final String RECOVERABLE_DEFAULT_ERROR_MESSAGE = "1C to 3C values must all be numbers " +
      "with up to 2 decimal places in the order of 0 ≤ 1C ≤ 2C ≤ 3C";

  private final UndevelopedDiscoveryInformationService undevelopedDiscoveryInformationService;

  @Autowired
  UndevelopedDiscoveryInformationFormValidator(UndevelopedDiscoveryInformationService informationService) {
    this.undevelopedDiscoveryInformationService = informationService;
  }

  boolean isValid(UndevelopedDiscoveryInformationForm form, Errors errors, UndevelopedDiscovery undevelopedDiscovery,
                  UUID existingInformationId) {
    validateDate(form, errors, undevelopedDiscovery, existingInformationId);
    validateVolumetrics(form.getVolumetricsForm(), errors);
    validateComplexity(form, errors);
    return !errors.hasErrors();
  }

  private void validateVolumetrics(UndevelopedDiscoveryVolumetricsForm form, Errors errors) {
    DecimalInputValidator.configureValidator(
            errors,
            "volumetricsForm.oilInPlace.decimalInputValue",
            "Oil initially in place",
            form.getOilInPlace())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "volumetricsForm.gasInPlace.decimalInputValue",
            "Gas initially in place",
            form.getGasInPlace())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    var oilRecoverableForm = form.getOilRecoverable();
    var validateOilRecoverable = ObjectUtils.anyNotNull(
        oilRecoverableForm.getOneC().getDecimalInputValue(),
        oilRecoverableForm.getTwoC().getDecimalInputValue(),
        oilRecoverableForm.getThreeC().getDecimalInputValue()
    );
    if (validateOilRecoverable) {
      validateRecoverableForm(oilRecoverableForm, errors, "oil");
    }

    var gasRecoverableForm = form.getGasRecoverable();
    var validateGasRecoverable = ObjectUtils.anyNotNull(
        gasRecoverableForm.getOneC().getDecimalInputValue(),
        gasRecoverableForm.getTwoC().getDecimalInputValue(),
        gasRecoverableForm.getThreeC().getDecimalInputValue()
    );
    if (validateGasRecoverable) {
      validateRecoverableForm(gasRecoverableForm, errors, "gas");
    }
  }

  private void validateRecoverableForm(UndevelopedDiscoveryRecoverableForm form, Errors errors, String hydrocarbon) {
    var builtPath = "volumetricsForm.%sRecoverable".formatted(hydrocarbon);
    var oneCPath = "%s.oneC.decimalInputValue".formatted(builtPath);
    var twoCPath = "%s.twoC.decimalInputValue".formatted(builtPath);
    var threeCPath = "%s.threeC.decimalInputValue".formatted(builtPath);
    var errorMessage = "%s recoverable resources - %s".formatted(
        StringUtils.capitalize(hydrocarbon),
        RECOVERABLE_DEFAULT_ERROR_MESSAGE
    );

    DecimalInputValidator.configureValidator(
            errors,
            oneCPath,
            "",
            form.getOneC())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .overrideDefaultErrorMessage(errorMessage)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            twoCPath,
            "",
            form.getTwoC())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .overrideDefaultErrorMessage(errorMessage)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            threeCPath,
            "",
            form.getThreeC())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .overrideDefaultErrorMessage(errorMessage)
        .validate();

    var hasErrors = ObjectUtils.anyNotNull(
        errors.getFieldError(oneCPath),
        errors.getFieldError(twoCPath),
        errors.getFieldError(threeCPath)
    );
    if (BooleanUtils.isFalse(hasErrors)) {
      validateOrder1CTo3C(form, errors, oneCPath, twoCPath, threeCPath);
    }
  }

  private void validateOrder1CTo3C(UndevelopedDiscoveryRecoverableForm form, Errors errors, String oneCPath,
                                   String twoCPath, String threeCPath) {
    var oneCBiggerThanTwoC = form.getOneC().createBigDecimalOrNull()
        .compareTo(form.getTwoC().createBigDecimalOrNull()) > 0;
    var twoCBiggerThanThreeC = form.getTwoC().createBigDecimalOrNull()
        .compareTo(form.getThreeC().createBigDecimalOrNull()) > 0;

    if (oneCBiggerThanTwoC || twoCBiggerThanThreeC) {
      errors.rejectValue(
          oneCPath,
          getInvalidErrorCode(oneCPath),
          "Gas recoverable resources - %s".formatted(RECOVERABLE_DEFAULT_ERROR_MESSAGE)
      );

      errors.rejectValue(twoCPath, getInvalidErrorCode(twoCPath), "");
      errors.rejectValue(threeCPath, getInvalidErrorCode(threeCPath), "");
    }
  }

  private void validateComplexity(UndevelopedDiscoveryInformationForm form, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "provideDiscoveryComplexity",
        "provideDiscoveryComplexity.required",
        "Select if you want to provide discovery complexity information"
    );

    if (BooleanUtils.isTrue(form.getProvideDiscoveryComplexity())) {
      validateNestedComplexity(errors);
    }
  }

  private void validateNestedComplexity(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "complexityForm.viscosityRange",
        "complexityForm.viscosityRange.required",
        "Provide the viscosity"
    );
    ValidationUtils.rejectIfEmpty(
        errors,
        "complexityForm.verticalHeterogeneityRange",
        "complexityForm.verticalHeterogeneityRange.required",
        "Provide the vertical heterogeneity"
    );
    ValidationUtils.rejectIfEmpty(
        errors,
        "complexityForm.arealDensityRange",
        "complexityForm.arealDensityRange.required",
        "Provide the areal density"
    );
    ValidationUtils.rejectIfEmpty(
        errors,
        "complexityForm.structuralComplexityRange",
        "complexityForm.structuralComplexityRange.required",
        "Provide the structural complexity"
    );
  }

  private void validateDate(UndevelopedDiscoveryInformationForm form, Errors errors,
                            UndevelopedDiscovery undevelopedDiscovery,
                            UUID existingInformationId) {
    DateValidationUtil.validateDateIsTodayOrInThePast(
        "discoveryInformationDate",
        "date discovery information is relevant to",
        form.getDiscoveryInformationDateDay(),
        form.getDiscoveryInformationDateMonth(),
        form.getDiscoveryInformationDateYear(),
        errors
    );

    if (ObjectUtils.allNull(
        errors.getFieldError("discoveryInformationDateDay"),
        errors.getFieldError("discoveryInformationDateMonth"),
        errors.getFieldError("discoveryInformationDateYear"))) {
      validateUniqueDate(form, errors, undevelopedDiscovery, existingInformationId);
    }
  }

  private void validateUniqueDate(UndevelopedDiscoveryInformationForm form, Errors errors,
                                  UndevelopedDiscovery undevelopedDiscovery,
                                  UUID existingInformationId) {
    var newDate = LocalDate.of(
        Integer.parseInt(form.getDiscoveryInformationDateYear()),
        Integer.parseInt(form.getDiscoveryInformationDateMonth()),
        Integer.parseInt(form.getDiscoveryInformationDateDay())
    );

    var isAlreadyInformationOnDate = undevelopedDiscoveryInformationService.getOrderedDiscoveryInformation(
            undevelopedDiscovery)
        .stream()
        .anyMatch(storedInformation -> storedInformation.getInformationDate().equals(newDate)
            && !storedInformation.getId().equals(existingInformationId));

    if (isAlreadyInformationOnDate) {
      errors.rejectValue(
          "discoveryInformationDateDay",
          "discoveryInformationDate.duplicate",
          "There is already discovery information for the date provided"
      );
    }
  }

  private String getInvalidErrorCode(String rootPath) {
    return "%s.invalid".formatted(rootPath);
  }
}