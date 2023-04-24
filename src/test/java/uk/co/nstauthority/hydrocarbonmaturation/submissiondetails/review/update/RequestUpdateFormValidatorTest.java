package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class RequestUpdateFormValidatorTest {

  @InjectMocks
  private RequestUpdateFormValidator underTest;

  @Nested
  class WhenSupporting {

    @Test
    void supportedClass() {
      assertThat(underTest.supports(RequestUpdateForm.class)).isTrue();
    }

    @Test
    void nonSupportedClass() {
      var nonSupportedClass = NonSupportedClass.class;

      assertThat(underTest.supports(nonSupportedClass)).isFalse();
    }
  }

  @Nested
  class WhenValidating {
    private static final String REASON_FOR_REQUEST = "Update reason here";
    private RequestUpdateForm form;

    private BindingResult bindingResult;

    @BeforeEach
    void setup() {
      form = new RequestUpdateForm();
      bindingResult = new BeanPropertyBindingResult(form, "form");
    }

    @Test
    void AndValidForm() {
      form.setUpdateReason(REASON_FOR_REQUEST);
      underTest.validate(
          form,
          bindingResult);
      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
      assertThat(extractedErrors)
          .isEmpty();
      assertThat(extractedErrorMessages)
          .isEmpty();
    }

    @Test
    void AndInvalidForm() {
      form.setUpdateReason(null);
      underTest.validate(
          form,
          bindingResult);
      var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
      var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
      assertThat(extractedErrors)
          .containsExactly(
              entry("updateReason", Set.of("updateReason.required"))
          );
      assertThat(extractedErrorMessages)
          .containsExactly(
              entry("updateReason", Set.of("Enter reason for requesting the update"))
          );
    }
  }
}