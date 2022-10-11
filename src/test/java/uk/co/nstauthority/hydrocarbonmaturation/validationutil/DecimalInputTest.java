package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

@ExtendWith(MockitoExtension.class)
class DecimalInputTest {

  private DecimalInput decimalInput;

  @BeforeEach
  public void setup() {
    decimalInput = new DecimalInput();
  }

  @Test
  void asBigDecimal_whenNullValue() {
    assertThat(decimalInput.asBigDecimal()).isEmpty();
  }

  @Test
  void asBigDecimal_whenValueIsNumber() {
    decimalInput.setDecimalInputValue("56.9935");
    assertThat(decimalInput.asBigDecimal()).isPresent();
    assertThat(decimalInput.asBigDecimal()).isEqualTo(Optional.of(BigDecimal.valueOf(56.9935)));
  }

  @Test
  void asBigDecimal_whenValueIsNonNumerical() {
    decimalInput.setDecimalInputValue("no number");
    assertThat(decimalInput.asBigDecimal()).isEmpty();
  }

  @Test
  void asBigDecimal_whenValueIsExponent() {
    decimalInput.setDecimalInputValue("10e");
    assertThat(decimalInput.asBigDecimal()).isEmpty();
  }
}