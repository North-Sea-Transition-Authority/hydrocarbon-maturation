package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsServiceTest {

  private static GeneralDetailsFormValidator generalDetailsFormValidator;
  private static GeneralDetailsService generalDetailsService;

  @BeforeAll
  static void setup() {
    generalDetailsFormValidator = mock(GeneralDetailsFormValidator.class);
    generalDetailsService = new GeneralDetailsService(generalDetailsFormValidator);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalDetailsService.validate(form, bindingResult);
    verify(generalDetailsFormValidator, times(1)).validate(form, bindingResult);
  }
}