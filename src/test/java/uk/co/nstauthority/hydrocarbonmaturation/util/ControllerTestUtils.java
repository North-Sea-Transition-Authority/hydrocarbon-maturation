package uk.co.nstauthority.hydrocarbonmaturation.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

public class ControllerTestUtils {

  private ControllerTestUtils() {
    throw new AssertionError();
  }

  public static MultiValueMap<String, String> validationPostParams(ValidationType validationType) {
    return new LinkedMultiValueMap<>(){{
      add(validationType.getButtonText(), validationType.getButtonText());
    }};
  }
}
