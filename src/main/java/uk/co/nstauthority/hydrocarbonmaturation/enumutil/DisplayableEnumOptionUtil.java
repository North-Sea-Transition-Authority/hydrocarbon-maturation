package uk.co.nstauthority.hydrocarbonmaturation.enumutil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

public class DisplayableEnumOptionUtil {

  private DisplayableEnumOptionUtil() {
    throw new IllegalStateException("DisplayableEnumOptionUtil is a util class and should not be instantiated");
  }

  public static Map<String, String> getDisplayableOptions(
      Class<? extends Displayable> displayableOptionEnum
  ) {
    return Arrays.stream((Displayable[]) displayableOptionEnum.getEnumConstants())
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(
            Displayable::getEnumName,
            Displayable::getDisplayName
        ));
  }

  public static Map<String, String> getDisplayableOptionsWithDescription(
      Class<? extends Displayable> displayableOptionEnum
  ) {
    return Arrays.stream((Displayable[]) displayableOptionEnum.getEnumConstants())
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(
            Displayable::getEnumName,
            display -> !display.getDescription().isEmpty()
                ? "%s (%s)".formatted(display.getDescription(), display.getDisplayName())
                : display.getDisplayName()
        ));
  }
}
