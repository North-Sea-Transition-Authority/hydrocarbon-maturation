package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.util.Optional;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class StringUtil {

  private StringUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Optional<UUID> toUuid(String uuid) {
    try {
      return Optional.of(UUID.fromString(uuid));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
