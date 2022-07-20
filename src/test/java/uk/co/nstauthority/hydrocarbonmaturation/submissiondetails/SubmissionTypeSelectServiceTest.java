package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class SubmissionTypeSelectServiceTest {

  private static SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator;
  private static SubmissionTypeSelectService submissionTypeSelectService;

  @BeforeAll
  static void setup() {
    submissionTypeSelectFormValidator = mock(SubmissionTypeSelectFormValidator.class);
    submissionTypeSelectService = new SubmissionTypeSelectService(submissionTypeSelectFormValidator);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionTypeSelectService.validate(form, bindingResult);
    verify(submissionTypeSelectFormValidator, times(1)).validate(form, bindingResult);
  }
}