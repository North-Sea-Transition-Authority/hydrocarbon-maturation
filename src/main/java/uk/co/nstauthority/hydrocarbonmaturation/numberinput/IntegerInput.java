package uk.co.nstauthority.hydrocarbonmaturation.numberinput;

import java.math.BigInteger;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerInput {

  private String integerInputValue;

  private static final Logger LOGGER = LoggerFactory.getLogger(IntegerInput.class);

  public IntegerInput(String integerInputValue) {
    this.integerInputValue = integerInputValue;
  }

  public IntegerInput() {
  }

  public static IntegerInput createNumberInput(BigInteger integerInputValue) {
    return integerInputValue == null ? new IntegerInput() : new IntegerInput(integerInputValue.toString());
  }

  public String getIntegerInputValue() {
    return integerInputValue;
  }

  public void setIntegerInputValue(String integerInputValue) {
    this.integerInputValue = integerInputValue;
  }

  public BigInteger createBigIntegerOrNull() {
    return this.asBigInteger()
        .orElse(null);
  }

  /**
   * BigInteger(String) allows exponents. This method counts any input containing e/E as invalid
   */
  public Optional<BigInteger> asBigInteger() throws NumberFormatException {
    if (integerInputValue != null && integerInputValue.toLowerCase().contains("e")) {
      integerInputValue = integerInputValue.replaceAll("[\n\r\t]", "_");
      LOGGER.debug("Could not convert the input value to a valid IntegerInput for value: {}", integerInputValue);
      return Optional.empty();
    }

    try {
      return Optional.ofNullable(integerInputValue)
          .map(BigInteger::new);

    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public boolean hasContent() {
    return integerInputValue != null && !integerInputValue.isBlank();
  }

  @Override
  public String toString() {
    return """
        IntegerInput {
          integerInputValue: %s
        }
        """.formatted(integerInputValue);
  }
}
