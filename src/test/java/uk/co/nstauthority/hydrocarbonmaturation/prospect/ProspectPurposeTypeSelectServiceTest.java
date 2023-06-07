package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class ProspectPurposeTypeSelectServiceTest {

  private static ProspectPurposeTypeSelectFormValidator prospectPurposeTypeSelectFormValidator;
  private static ProspectPurposeTypeSelectService prospectPurposeTypeSelectService;

  @BeforeAll
  static void setup() {
    prospectPurposeTypeSelectFormValidator = mock(ProspectPurposeTypeSelectFormValidator.class);
    prospectPurposeTypeSelectService = new ProspectPurposeTypeSelectService(prospectPurposeTypeSelectFormValidator);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new ProspectPurposeTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    prospectPurposeTypeSelectService.validate(form, bindingResult);
    verify(prospectPurposeTypeSelectFormValidator, times(1)).validate(form, bindingResult);
  }
}