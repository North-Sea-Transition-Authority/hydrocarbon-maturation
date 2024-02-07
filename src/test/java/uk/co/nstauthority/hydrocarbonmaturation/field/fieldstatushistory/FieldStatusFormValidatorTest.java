package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class FieldStatusFormValidatorTest {
  @InjectMocks
  private FieldStatusFormValidator fieldStatusFormValidator;

  private FieldStatusForm form;
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    form = new FieldStatusForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void validateForm_validForm_thenNoErrors() {
    form = getValidForm();
    assertTrue(fieldStatusFormValidator.isValid(form, bindingResult));
  }

  @Test
  void validateEmptyForm_thenErrors() {
    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldStatus", Set.of("fieldStatus.required")),
            entry("fieldStatusDateDay", Set.of("fieldStatusDateDay.required")),
            entry("fieldStatusDateMonth", Set.of("fieldStatusDateMonth.required")),
            entry("fieldStatusDateYear", Set.of("fieldStatusDateYear.required"))
        );
  }

  @Test
  void validateForm_incompleteDate_thenError() {
    form = getValidForm();

    form.setFieldStatusDateDay("03");
    form.setFieldStatusDateMonth("11");
    form.setFieldStatusDateYear("");

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldStatusDateYear", Set.of("fieldStatusDateYear.invalid"))
        );
  }

  @Test
  void validateForm_invalidDate_thenError() {
    form = getValidForm();

    form.setFieldStatusDateDay("a");
    form.setFieldStatusDateMonth("b");
    form.setFieldStatusDateYear("c");

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldStatusDateDay", Set.of("fieldStatusDateDay.invalid")),
            entry("fieldStatusDateMonth", Set.of("fieldStatusDateMonth.invalid")),
            entry("fieldStatusDateYear", Set.of("fieldStatusDateYear.invalid"))
        );
  }

  private FieldStatusForm getValidForm() {
    var form = new FieldStatusForm();

    form.setFieldStatus(FieldStatus.STATUS_300);
    form.setFieldStatusDateDay("03");
    form.setFieldStatusDateMonth("11");
    form.setFieldStatusDateYear("2002");

    return form;
  }
}