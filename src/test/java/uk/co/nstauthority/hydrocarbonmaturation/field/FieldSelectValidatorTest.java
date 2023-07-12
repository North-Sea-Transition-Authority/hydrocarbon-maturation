package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class FieldSelectValidatorTest {

  @InjectMocks
  private FieldSelectValidator underTest;

  @Test
  void emptyForm() {
    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(underTest.isValid(bindingResult)).isFalse();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("fieldSelectedOption", Set.of("fieldSelectedOption.required"))
        );
  }

  @Test
  void validForm() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(underTest.isValid(bindingResult)).isTrue();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).isEmpty();
  }

  private FieldSelectForm getEmptyForm() {
    return new FieldSelectForm();
  }

  private FieldSelectForm getValidForm() {
    var form = new FieldSelectForm();
    form.setFieldSelectedOption("field");
    return form;
  }
}