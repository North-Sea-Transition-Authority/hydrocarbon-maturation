package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

class ProjectFormValidatorTest {
  private ProjectFormValidator projectFormValidator;

  @BeforeEach
  void setUp() {
    projectFormValidator = new ProjectFormValidator();
  }

  @Nested
  class GivenIsValidNewProject {
    @Test
    void whenIsValidNewProject_onEmptyForm_thenErrors() {
      var form = new ProjectForm();
      var bindingResult = new BeanPropertyBindingResult(form, "form");

      assertFalse(projectFormValidator.isValidNewProject(bindingResult));

      var errors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(errors)
          .containsExactly(
              entry("projectName", Set.of("projectName.required")),
              entry("projectType", Set.of("projectType.required")),
              entry("projectPhase", Set.of("projectPhase.required")),
              entry("isPartOfPipeline", Set.of("isPartOfPipeline.required"))
          );
    }

    @Test
    void isValidNewProject_onValidForm_thenNoErrors() {
      var form = getPopulatedForm();
      var bindingResult = new BeanPropertyBindingResult(form, "form");

      assertTrue(projectFormValidator.isValidNewProject(bindingResult));

      var errors = ValidatorTestingUtil.extractErrors(bindingResult);
      assertThat(errors).isEmpty();
    }

    private ProjectForm getPopulatedForm() {
      var form = new ProjectForm();
      form.setProjectName("Test name");
      form.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);
      form.setProjectPhase(ProjectPhase.ASSESSMENT_PHASE);
      form.setIsPartOfPipeline(true);
      form.setSelectedSubmissionId(UUID.randomUUID().toString());
      return form;
    }
  }

  @Nested
  class GivenIsValidProjectUpdate {
    private Project project;
    private ProjectForm form;

    @BeforeEach
    void setUp() {
      project = new Project();
    }

    @Nested
    class WhenEmptyForm {

      @BeforeEach
      void setUp() {
        form = new ProjectForm();
      }

      @Test
      void andProjectTypeFdpa_thenErrors() {
        project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);

        var bindingResult = new BeanPropertyBindingResult(form, "form");

        assertFalse(projectFormValidator.isValidProjectUpdate(project, form, bindingResult));

        var errors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(errors)
            .containsExactly(
                entry("projectName", Set.of("projectName.required")),
                entry("projectPhase", Set.of("projectPhase.required")),
                entry("isPartOfPipeline", Set.of("isPartOfPipeline.required")),
                entry("fdpaStartUpDateDay", Set.of("fdpaStartUpDateDay.required")),
                entry("fdpaStartUpDateMonth", Set.of("fdpaStartUpDateMonth.required")),
                entry("fdpaStartUpDateYear", Set.of("fdpaStartUpDateYear.required"))
            );
      }

      @ParameterizedTest
      @EnumSource(
          value = ProjectType.class,
          names = "FIELD_DEVELOPMENT_PLAN_ADDENDUM",
          mode = EnumSource.Mode.EXCLUDE
      )
      void andProjectTypeNotFdpa_thenErrors(ProjectType projectType) {
        project.setProjectType(projectType);

        var bindingResult = new BeanPropertyBindingResult(form, "form");

        assertFalse(projectFormValidator.isValidProjectUpdate(project, form, bindingResult));

        var errors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(errors)
            .containsExactly(
                entry("projectName", Set.of("projectName.required")),
                entry("projectPhase", Set.of("projectPhase.required")),
                entry("isPartOfPipeline", Set.of("isPartOfPipeline.required"))
            ).doesNotContainKeys(
                "fdpaStartUpDateDay",
                "fdpaStartUpDateMonth",
                "fdpaStartUpDateYear"
            );
      }
    }

    @Nested
    class WhenInvalidForm {

      @Nested
      class DueToFdpaFields {
        @BeforeEach
        void setUp() {
          project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);
        }

        @Test
        void becauseFdpaStartUpDateIsInFuture_thenErrors() {
          var form = getValidFdpaForm();
          form.setFdpaStartUpDateDay("3");
          form.setFdpaStartUpDateMonth("11");
          form.setFdpaStartUpDateYear(String.valueOf(LocalDate.now().plusYears(1).getYear()));

          var bindingResult = new BeanPropertyBindingResult(form, "form");

          assertFalse(projectFormValidator.isValidProjectUpdate(project, form, bindingResult));

          var errors = ValidatorTestingUtil.extractErrors(bindingResult);
          assertThat(errors)
              .containsExactly(
                  entry("fdpaStartUpDateDay", Set.of("fdpaStartUpDateDay.notBeforeTargetDate")),
                  entry("fdpaStartUpDateMonth", Set.of("fdpaStartUpDateMonth.notBeforeTargetDate")),
                  entry("fdpaStartUpDateYear", Set.of("fdpaStartUpDateYear.notBeforeTargetDate"))
              );
        }

        @Test
        void becauseFdpaStartUpDateIsNotDate_thenErrors() {
          var form = getValidNonFdpaForm();
          form.setFdpaStartUpDateDay("Not a day");
          form.setFdpaStartUpDateMonth("Not a month");
          form.setFdpaStartUpDateYear("Not a year");

          var bindingResult = new BeanPropertyBindingResult(form, "form");

          assertFalse(projectFormValidator.isValidProjectUpdate(project, form, bindingResult));

          var errors = ValidatorTestingUtil.extractErrors(bindingResult);
          assertThat(errors)
              .containsExactly(
                  entry("fdpaStartUpDateDay", Set.of("fdpaStartUpDateDay.invalid")),
                  entry("fdpaStartUpDateMonth", Set.of("fdpaStartUpDateMonth.invalid")),
                  entry("fdpaStartUpDateYear", Set.of("fdpaStartUpDateYear.invalid"))
              );
        }
      }
    }

    @Nested
    class WhenValidForm {

      @Test
      void andFdpaProjectType_thenNoErrors() {
        project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);
        var form = getValidFdpaForm();
        var bindingResult = new BeanPropertyBindingResult(form, "form");

        assertTrue(projectFormValidator.isValidProjectUpdate(project, form, bindingResult));

        var errors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(errors).isEmpty();
      }

      @ParameterizedTest
      @EnumSource(
          value = ProjectType.class,
          names = "FIELD_DEVELOPMENT_PLAN_ADDENDUM",
          mode = EnumSource.Mode.EXCLUDE
      )
      void andNonFdpaProjectType_thenNoErrors(ProjectType projectType) {
        project.setProjectType(projectType);
        var form = getValidNonFdpaForm();
        var bindingResult = new BeanPropertyBindingResult(form, "form");

        assertTrue(projectFormValidator.isValidProjectUpdate(project, form, bindingResult));

        var errors = ValidatorTestingUtil.extractErrors(bindingResult);
        assertThat(errors).isEmpty();
      }
    }

    private ProjectForm getValidFdpaForm() {
      var form = new ProjectForm();
      form.setProjectName("Test name");
      form.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);
      form.setProjectPhase(ProjectPhase.ASSESSMENT_PHASE);
      form.setIsPartOfPipeline(true);
      form.setSelectedSubmissionId(UUID.randomUUID().toString());
      form.setFdpaStartUpDateDay("3");
      form.setFdpaStartUpDateMonth("11");
      form.setFdpaStartUpDateYear("2002");
      return form;
    }

    private ProjectForm getValidNonFdpaForm() {
      var form = new ProjectForm();
      form.setProjectName("Test name");
      form.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);
      form.setProjectPhase(ProjectPhase.ASSESSMENT_PHASE);
      form.setIsPartOfPipeline(true);
      form.setSelectedSubmissionId(UUID.randomUUID().toString());
      form.setFdpaStartUpDateDay(null);
      form.setFdpaStartUpDateMonth(null);
      form.setFdpaStartUpDateYear(null);
      return form;
    }
  }
}