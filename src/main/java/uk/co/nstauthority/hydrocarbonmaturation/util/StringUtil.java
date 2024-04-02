package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.util.Optional;
import java.util.UUID;
import org.jooq.tools.StringUtils;
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

  public static String withFirstLetterLowerCase(String string) {
    if (StringUtils.isBlank(string)) {
      return string;
    }

    var stringCharArray = string.toCharArray();
    stringCharArray[0] = Character.toLowerCase(stringCharArray[0]);
    return new String(stringCharArray);
  }
}
