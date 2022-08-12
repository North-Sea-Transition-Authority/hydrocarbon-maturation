package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesFormServiceTest {

  private static ReservoirPropertiesFormService reservoirPropertiesFormService;

  @Mock
  private static ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  @BeforeEach
  void setUp() {
    reservoirPropertiesFormService = new ReservoirPropertiesFormService(reservoirPropertiesFormValidator);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new ReservoirPropertiesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormService.validate(form, bindingResult);
    verify(reservoirPropertiesFormValidator, times(1)).validate(form, bindingResult);
  }
}