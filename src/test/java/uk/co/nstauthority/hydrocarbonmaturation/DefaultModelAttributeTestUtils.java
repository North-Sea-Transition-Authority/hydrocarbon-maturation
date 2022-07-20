package uk.co.nstauthority.hydrocarbonmaturation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultModelAttributeTestUtils {

  public static final List<String> DEFAULT_KEYS = List.of(
      "serviceBranding",
      "customerBranding",
      "serviceHomeUrl",
      "navigationItems",
      "currentEndPoint",
      "org.springframework.validation.BindingResult.serviceBranding",
      "org.springframework.validation.BindingResult.customerBranding");

  private DefaultModelAttributeTestUtils() {
    throw new IllegalStateException();
  }

  public static Set<String> withDefaultKeys(String... customKeys) {
    var combinedKeys = new HashSet<>(Arrays.asList(customKeys));
    combinedKeys.addAll(DEFAULT_KEYS);
    return combinedKeys;
  }
}
