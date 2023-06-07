package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class DiscoveryPurposeTypeSelectServiceTest {

  private static DiscoveryPurposeTypeSelectFormValidator discoveryPurposeTypeSelectFormValidator;
  private static DiscoveryPurposeTypeSelectService discoveryPurposeTypeSelectService;

  @BeforeAll
  static void setup() {
    discoveryPurposeTypeSelectFormValidator = mock(DiscoveryPurposeTypeSelectFormValidator.class);
    discoveryPurposeTypeSelectService = new DiscoveryPurposeTypeSelectService(discoveryPurposeTypeSelectFormValidator);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new DiscoveryPurposeTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    discoveryPurposeTypeSelectService.validate(form, bindingResult);
    verify(discoveryPurposeTypeSelectFormValidator, times(1)).validate(form, bindingResult);
  }
}