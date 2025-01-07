package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;

@ExtendWith(MockitoExtension.class)
class ProjectStageFormValidatorTest {
  @Mock
  private ProjectStageService projectStageService;
  @InjectMocks
  private ProjectStageFormValidator projectStageFormValidator;

  private ProjectStageForm form;
  private BindingResult bindingResult;
  private Project project;

  @BeforeEach
  void setUp() {
    form = getValidForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    project = new Project();
  }

  @Test
  void isValid_whenIsValid_thenTrue() {
    form = getValidForm();

    assertTrue(projectStageFormValidator.isValid(form, bindingResult, project));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void isValid_whenEmptyForm_thenFalse() {
    form.setStageDateDay(null);
    form.setStageDateMonth(null);
    form.setStageDateYear(null);

    assertFalse(projectStageFormValidator.isValid(form, bindingResult, project));

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .contains(
            Tuple.tuple("stageDateDay" , "stageDateDay.required", "Enter a date the project entered stage"),
            Tuple.tuple("stageDateMonth" , "stageDateMonth.required", ""),
            Tuple.tuple("stageDateYear" , "stageDateYear.required", "")
        );
  }

  @Test
  void isValid_whenDateInFuture_thenFalse() {
    var tomorrow = LocalDate.now().plusDays(1);

    form.setStageDateDay(String.valueOf(tomorrow.getDayOfMonth()));
    form.setStageDateMonth(String.valueOf(tomorrow.getMonthValue()));
    form.setStageDateYear(String.valueOf(tomorrow.getYear()));

    assertFalse(projectStageFormValidator.isValid(form, bindingResult, project));

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("stageDateDay" , "stageDateDay.notBeforeTargetDate", "Date the project entered stage must be today or in the past"),
            Tuple.tuple("stageDateMonth" , "stageDateMonth.notBeforeTargetDate", ""),
            Tuple.tuple("stageDateYear" , "stageDateYear.notBeforeTargetDate", "")
        );
  }

  @Test
  void isValid_whenDateMalformed_Strings_thenFalse() {
    form.setStageDateDay("not a day");
    form.setStageDateMonth("not a month");
    form.setStageDateYear("not a year");

    assertFalse(projectStageFormValidator.isValid(form, bindingResult, project));

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("stageDateDay" , "stageDateDay.invalid", "Date the project entered stage must be a real date"),
            Tuple.tuple("stageDateMonth" , "stageDateMonth.invalid", ""),
            Tuple.tuple("stageDateYear" , "stageDateYear.invalid", "")
        );
  }

  @Test
  void isValid_whenDateMalformed_badDate_thenFalse() {
    form.setStageDateDay("100");
    form.setStageDateMonth("1");
    form.setStageDateYear("2024");

    assertFalse(projectStageFormValidator.isValid(form, bindingResult, project));

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(Tuple.tuple("stageDateDay" , "stageDateDay.invalid", "Date the project entered stage must be a real date"));
  }

  @Test
  void isValid_whenStageAlreadyOnDate_thenFalse() {
    form.setStageDateDay("2");
    form.setStageDateMonth("2");
    form.setStageDateYear("2024");

    var stageWithSameDate = mock(ProjectStageHistory.class);
    when(stageWithSameDate.getStageDate())
        .thenReturn(LocalDate.of(2024, Month.FEBRUARY, 2));

    when(projectStageService.getOrderedProjectStages(project))
        .thenReturn(List.of(stageWithSameDate));

    assertFalse(projectStageFormValidator.isValid(form, bindingResult, project));

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("stageDateDay" , "stageDate.duplicate", "There is already a project stage for the date provided")
        );
  }

  @Test
  void isValid_whenStageAlreadyOnDate_andOtherInfoIsInvalid_thenFalse() {
    form.setStage(null);
    form.setStageDateDay("2");
    form.setStageDateMonth("2");
    form.setStageDateYear("2024");

    var stageWithSameDate = mock(ProjectStageHistory.class);
    when(stageWithSameDate.getStageDate())
        .thenReturn(LocalDate.of(2024, Month.FEBRUARY, 2));

    when(projectStageService.getOrderedProjectStages(project))
        .thenReturn(List.of(stageWithSameDate));

    assertFalse(projectStageFormValidator.isValid(form, bindingResult, project));

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("stage" , "stage.required", "Select a project stage"),
            Tuple.tuple("stageDateDay" , "stageDate.duplicate", "There is already a project stage for the date provided")
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