package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class ActualProjectDateFormValidatorTest {

  @InjectMocks
  private ActualProjectDateFormValidator actualProjectDateFormValidator;

  private ActualProjectDateForm form;

  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    form = new ActualProjectDateForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }
  @Test
  void isValid_validForm() {
    form.setDateDay("1");
    form.setDateMonth("1");
    form.setDateYear("2025");

    assertThat(actualProjectDateFormValidator.isValid(form, bindingResult)).isTrue();

    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void isValid_notValidForm() {
    assertThat(actualProjectDateFormValidator.isValid(form, bindingResult)).isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("dateDay", "dateDay.required", "Enter a date"),
            Tuple.tuple("dateMonth", "dateMonth.required", ""),
            Tuple.tuple("dateYear", "dateYear.required", "")
        );
  }
}