package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class FeedbackFormValidatorTest {

  @InjectMocks
  private FeedbackFormValidator feedbackFormValidator;

  @Test
  void supports_whenFeedbackFormClass_thenTrue() {
    var supportedClass = FeedbackForm.class;
    assertTrue(feedbackFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotFeedbackFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(feedbackFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    feedbackFormValidator.validate(form, bindingResult);

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new FeedbackForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    feedbackFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("serviceRating", Set.of("serviceRating.required"))
        );

    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("serviceRating", Set.of("Select how you felt about this service"))
        );
  }

  private FeedbackForm getValidForm() {
    var form = new FeedbackForm();
    form.setFeedback("feedback");
    form.setServiceRating(ServiceFeedbackRating.DISSATISFIED);
    return form;
  }
}