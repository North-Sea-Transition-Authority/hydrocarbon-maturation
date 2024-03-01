package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

class ProjectFormValidatorTest {
  private ProjectFormValidator projectFormValidator;

  @BeforeEach
  void setUp() {
    projectFormValidator = new ProjectFormValidator();
  }

  @Test
  void validate_emptyForm_thenErrors() {
    var form = new ProjectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertFalse(projectFormValidator.isValid(bindingResult));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .containsExactly(
            entry("projectName", Set.of("projectName.required")),
            entry("projectType", Set.of("projectType.required")),
            entry("projectStatus", Set.of("projectStatus.required")),
            entry("isPartOfPipeline", Set.of("isPartOfPipeline.required"))
        );
  }

  @Test
  void validate_populatedForm_thenNoErrors() {
    var form = getPopulatedForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertTrue(projectFormValidator.isValid(bindingResult));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors).isEmpty();
  }

  private ProjectForm getPopulatedForm() {
    var form = new ProjectForm();
    form.setProjectName("Test name");
    form.setProjectType(ProjectType.PROJECT_CLOSE_OUT_RECEIVED_FROM_OPERATOR);
    form.setProjectStatus(ProjectStatus.ACCEPTED_CSR);
    form.setIsPartOfPipeline(true);
    form.setSelectedSubmissionId(UUID.randomUUID().toString());
    return form;
  }
}