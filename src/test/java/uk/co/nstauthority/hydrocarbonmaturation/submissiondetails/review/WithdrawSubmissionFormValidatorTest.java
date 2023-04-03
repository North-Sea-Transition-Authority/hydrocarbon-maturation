package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class WithdrawSubmissionFormValidatorTest {

  @InjectMocks
  private WithdrawSubmissionFormValidator underTest;

  @Test
  void supports_whenWithdrawForm() {
    var withdrawSubmissionFormClass = WithdrawSubmissionForm.class;
    var result = underTest.supports(withdrawSubmissionFormClass);

    assertThat(result).isTrue();
  }

  @Test
  void supports_whenNotSupported() {
    var result = underTest.supports(NonSupportedClass.class);

    assertThat(result).isFalse();
  }

  @Test
  void validate_whenInvalidForm() {
    var form = new WithdrawSubmissionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    underTest.validate(form, bindingResult);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("withdrawReason", Set.of("withdrawReason.required"))
        );

    var errorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(errorMessages)
        .containsExactly(
            entry("withdrawReason", Set.of("Enter the reason for withdrawal"))
        );
  }

  @Test
  void validate_whenValidForm() {
    var form = new WithdrawSubmissionForm();
    form.setWithdrawReason("reason");
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    underTest.validate(form, bindingResult);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).isEmpty();
    assertThat(errors).isEmpty();
  }
}