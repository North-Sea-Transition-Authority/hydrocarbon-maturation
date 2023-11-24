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

  public static Map<String, String> getDisplayableOptionsWithNameAndDescription(
      Class<? extends Displayable> displayableOptionEnum
  ) {
    return Arrays.stream((Displayable[]) displayableOptionEnum.getEnumConstants())
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(
            Displayable::getEnumName,
            display -> !display.getDescription().isEmpty()
                ? "%s (%s)".formatted(display.getDisplayName(), display.getDescription())
                : display.getDisplayName()
        ));
  }

  /*
   * Gets the displayable options with a map of the display name and hint text in separate objects
   * rather than in a single string.
   * @param displayableOptionEnum
   * @return  Map<Enum name, Map<Enum display name, Enum description>>
   */
  public static Map<String, Map<String, String>> getDisplayableOptionsWithSeparateNameAndDescription(
      Class<? extends Displayable> displayableOptionEnum
  ) {
    return Arrays.stream((Displayable[]) displayableOptionEnum.getEnumConstants())
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(
            Displayable::getEnumName,
            displayable -> Map.of(displayable.getDisplayName(), displayable.getDescription())
        ));
  }

  public static String getDisplayableOptionOrDefault(Displayable displayable, String defaultText) {
    return displayable != null
        ? displayable.getDisplayName()
        : defaultText;
  }

  public static String getDisplayableOptionOrEmpty(Displayable displayable) {
    return displayable != null
        ? displayable.getDisplayName()
        : "";
  }
}
