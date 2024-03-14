package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class ProjectStageFormValidatorTest {
  private ProjectStageFormValidator projectStageFormValidator;

  private ProjectStageForm form;
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    projectStageFormValidator = new ProjectStageFormValidator();

    form = getValidForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void isValid_whenIsValid_thenTrue() {
    form = getValidForm();

    assertTrue(projectStageFormValidator.isValid(form, bindingResult));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void isValid_whenEmptyForm_thenFalse() {
    form.setStageDateDay(null);
    form.setStageDateMonth(null);
    form.setStageDateYear(null);

    assertFalse(projectStageFormValidator.isValid(form, bindingResult));

    assertThat(ValidatorTestingUtil.extractErrors(bindingResult))
        .contains(
            entry("stageDateDay" , Set.of("stageDateDay.required")),
            entry("stageDateMonth" , Set.of("stageDateMonth.required")),
            entry("stageDateYear" , Set.of("stageDateYear.required"))
        );
  }

  @Test
  void isValid_whenDateInFuture_thenFalse() {
    var tomorrow = LocalDate.now().plusDays(1);

    form.setStageDateDay(String.valueOf(tomorrow.getDayOfYear()));
    form.setStageDateMonth(String.valueOf(tomorrow.getMonthValue()));
    form.setStageDateYear(String.valueOf(tomorrow.getYear()));

    assertFalse(projectStageFormValidator.isValid(form, bindingResult));

    assertThat(ValidatorTestingUtil.extractErrors(bindingResult))
        .containsExactly(
            entry("stageDateDay" , Set.of("stageDateDay.invalid"))
        );
  }

  @Test
  void isValid_whenDateMalformed_thenFalse() {
    form.setStageDateDay("not a day");
    form.setStageDateMonth("not a month");
    form.setStageDateYear("not a year");

    assertFalse(projectStageFormValidator.isValid(form, bindingResult));

    assertThat(ValidatorTestingUtil.extractErrors(bindingResult))
        .containsExactly(
            entry("stageDateDay" , Set.of("stageDateDay.invalid")),
            entry("stageDateMonth" , Set.of("stageDateMonth.invalid")),
            entry("stageDateYear" , Set.of("stageDateYear.invalid"))
        );
  }

  private ProjectStageForm getValidForm() {
    var form = new ProjectStageForm();

    form.setStage(ProjectStage.CONSENT_GRANTED);
    form.setStageDateDay("1");
    form.setStageDateMonth("2");
    form.setStageDateYear("2024");

    return form;
  }
}