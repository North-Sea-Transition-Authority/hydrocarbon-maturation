package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.Assertions.assertThat;

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
}