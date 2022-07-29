package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

public class DecimalInput {

  private String decimalInputValue;

  public DecimalInput(String decimalInputValue) {
    this.decimalInputValue = decimalInputValue;
  }

  public DecimalInput() {

  }

  public String getDecimalInputValue() {
    return decimalInputValue;
  }

  public void setDecimalInputValue(String decimalInputValue) {
    this.decimalInputValue = decimalInputValue;
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
