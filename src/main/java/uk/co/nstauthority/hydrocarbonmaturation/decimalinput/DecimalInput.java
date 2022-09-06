package uk.co.nstauthority.hydrocarbonmaturation.decimalinput;

import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecimalInput {

  private String decimalInputValue;

  private static final Logger LOGGER = LoggerFactory.getLogger(DecimalInput.class);

  public DecimalInput(String decimalInputValue) {
    this.decimalInputValue = decimalInputValue;
  }

  public DecimalInput() {

  }

  public static DecimalInput createDecimalInput(BigDecimal decimalInputValue) {
    return decimalInputValue == null ? new DecimalInput() : new DecimalInput(decimalInputValue.toString());
  }

  public String getDecimalInputValue() {
    return decimalInputValue;
  }

  public void setDecimalInputValue(String decimalInputValue) {
    this.decimalInputValue = decimalInputValue;
  }

  public BigDecimal createBigDecimalOrNull() {
    return this.asBigDecimal()
        .orElse(null);
  }

  /**
   * BigDecimal(String) allows exponents. This method counts any input containing e/E as invalid
   */
  public Optional<BigDecimal> asBigDecimal() throws NumberFormatException {
    if (decimalInputValue != null && decimalInputValue.toLowerCase().contains("e")) {
      decimalInputValue = decimalInputValue.replaceAll("[\n\r\t]", "_");
      LOGGER.debug("Could not convert the input value to a valid DecimalInput for value: {}", decimalInputValue);
      return Optional.empty();
    }

    try {
      return Optional.ofNullable(decimalInputValue)
          .map(BigDecimal::new);

    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public boolean hasContent() {
    return decimalInputValue != null && !decimalInputValue.isBlank();
  }

  @Override
  public String toString() {
    return """
        DecimalInput {
          decimalInputValue: %s
        }
        """.formatted(decimalInputValue);
  }
}
