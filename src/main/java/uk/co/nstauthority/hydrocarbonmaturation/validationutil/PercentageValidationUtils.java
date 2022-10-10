package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.math.BigDecimal;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.numberinput.DecimalInput;

public class PercentageValidationUtils {

  private PercentageValidationUtils() {
    throw new IllegalStateException("PercentageValidationUtils is an util class and should not be instantiated");
  }

  public static DecimalInputValidator configurePercentageValidator(DecimalInput percentage, String fieldId,
                                                                   String errorMessagePrefix, Errors errors) {
    return DecimalInputValidator.configureValidator(errors, fieldId, errorMessagePrefix + " percentage", percentage)
        .setMinValue(BigDecimal.ONE)
        .setMaxValue(BigDecimal.valueOf(100))
        .setMaxDecimalPlaces(0);
  }
}
