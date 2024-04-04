package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StringUtilTest {
  @Test
  void toUuid_whenValidUuid_thenMappedToUuid() {
    var uuid = UUID.randomUUID();
    var result = StringUtil.toUuid(uuid.toString());
    assertThat(result)
        .isPresent()
        .get()
        .isEqualTo(uuid);
  }

  @Test
  void toUuid_whenInvalidUuid_thenEmpty() {
    var invalidUuidString = "invalid uuid";
    var result = StringUtil.toUuid(invalidUuidString);
    assertThat(result)
        .isEmpty();
  }

  @Test
  void withFirstLetterLowerCase_emptyString() {
    var result = StringUtil.withFirstLetterLowerCase("");
    assertThat(result).isEqualTo("");
  }

  @Test
  void withFirstLetterLowerCase_nullString() {
    var result = StringUtil.withFirstLetterLowerCase(null);
    assertThat(result).isNull();
  }

  @Test
  void withFirstLetterLowerCase_happyString() {
    var result = StringUtil.withFirstLetterLowerCase("Example");
    assertThat(result).isEqualTo("example");
  }

  @Test
  void withFirstLetterLowerCase_alreadyLowercase() {
    var result = StringUtil.withFirstLetterLowerCase("example");
    assertThat(result).isEqualTo("example");
  }

  @Test
  void withFirstLetterLowerCase_nonLetter() {
    var result = StringUtil.withFirstLetterLowerCase("1example");
    assertThat(result).isEqualTo("1example");
  }

  @Test
  void formatMoney_noSuffix() {
    assertThat(StringUtil.formatMoney(BigDecimal.valueOf(5.21), ""))
        .isEqualTo("£5.21");
  }

  @Test
  void formatMoney_withSuffix() {
    assertThat(StringUtil.formatMoney(BigDecimal.valueOf(5.21), "billion"))
        .isEqualTo("£5.21 billion");
  }

  @Test
  void formatMoneyInMillions_withSuffix() {
    assertThat(StringUtil.formatMoneyInMillions(BigDecimal.valueOf(5.21)))
        .isEqualTo("£5.21 million");
  }
}