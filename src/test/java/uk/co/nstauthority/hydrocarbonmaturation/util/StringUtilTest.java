package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
}