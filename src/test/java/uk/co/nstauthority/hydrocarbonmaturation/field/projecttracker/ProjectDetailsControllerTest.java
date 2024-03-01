package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = ProjectDetailsController.class)
class ProjectDetailsControllerTest extends AbstractProjectTrackerControllerTest {
  @MockBean
  private ProjectFormValidator projectFormValidator;

  @SecurityTest
  void getCreateProject_whenNoProjectAnalystPermission_thenAssertForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectDetailsController.class)
                .getCreateProject(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Nested
  class GivenGettingCreateProject {
    @Test
    void whenFieldDoesNotExists_thenAssertNotFound() throws Exception {
      setupUserIsProjectAnalyst(USER);

      var fieldResourceIdThatDoesNotExist = UUID.randomUUID();
      when(fieldPersistenceService.getFieldOrThrow(fieldResourceIdThatDoesNotExist))
          .thenThrow(RemiEntityNotFoundException.class);

      mockMvc.perform(
              get(ReverseRouter.route(on(ProjectDetailsController.class)
                  .getCreateProject(fieldResourceIdThatDoesNotExist)))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @Test
    void whenFieldDoesExist_thenAssertProperties() throws Exception {
      setupUserIsProjectAnalyst(USER);

      var acceptedSubmissionOptions = Map.of("id", "submission title");
      when(projectService.getAcceptedSubmissionsMapForField(field))
          .thenReturn(acceptedSubmissionOptions);

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  get(ReverseRouter.route(on(ProjectDetailsController.class)
                      .getCreateProject(RESOURCE_ID)))
                      .with(user(USER)))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/projectDetails"))
              .andReturn()
              .getModelAndView())
          .getModelMap();

      assertThat(modelMap)
          .contains(
              entry("pageTitle", ProjectDetailsController.PAGE_TITLE),
              entry("pageCaption", field.getCorrectedLegalFieldName()),
              entry("projectTypeOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectType.class)),
              entry("projectStatusOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectStatus.class)),
              entry("acceptedSubmissionOptions", acceptedSubmissionOptions),
              entry("cancelUrl",
                  ReverseRouter.route(on(ProjectListController.class)
                      .getProjectList(RESOURCE_ID, null)))
          );

      assertThat(modelMap.getAttribute("form"))
          .usingRecursiveComparison()
          .isEqualTo(new ProjectForm());
    }
  }

  @Nested
  class GivenSavingCreatedProject {
    @SecurityTest
    void whenNotProjectAnalyst_thenAssertForbidden() throws Exception {
      mockMvc.perform(
              get(ReverseRouter.route(on(ProjectDetailsController.class)
                  .getCreateProject(RESOURCE_ID)))
                  .with(user(USER))
                  .with(csrf()))
          .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldDoesNotExists_thenAssertNotFound() throws Exception {
      setupUserIsProjectAnalyst(USER);

      var fieldResourceIdThatDoesNotExists = UUID.randomUUID();
      when(fieldPersistenceService.getFieldOrThrow(fieldResourceIdThatDoesNotExists))
          .thenThrow(RemiEntityNotFoundException.class);

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectDetailsController.class)
                  .saveCreatedProject(fieldResourceIdThatDoesNotExists, null, null)))
                  .with(user(USER))
                  .with(csrf()))
          .andExpect(status().isNotFound());
    }

    @Test
    void whenFormInvalid_thenAssertProperties() throws Exception {
      setupUserIsProjectAnalyst(USER);

      when(projectFormValidator.isValid(any(BindingResult.class)))
          .thenReturn(false);

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectDetailsController.class)
                  .saveCreatedProject(RESOURCE_ID, null, null)))
                  .with(user(USER))
                  .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(view().name("remi/field/projecttracker/projectDetails"));

      verify(projectService).getAcceptedSubmissionsMapForField(field);
      verifyNoMoreInteractions(projectService);
    }

    @Test
    void whenFormValid_thenAssertRedirect() throws Exception {
      setupUserIsProjectAnalyst(USER);

      when(projectFormValidator.isValid(any(BindingResult.class)))
          .thenReturn(true);

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectDetailsController.class)
                  .saveCreatedProject(RESOURCE_ID, null, null)))
                  .with(user(USER))
                  .with(csrf()))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl(ReverseRouter.route(on(ProjectListController.class)
              .getProjectList(RESOURCE_ID, null))))
          .andReturn()
          .getModelAndView();

      verify(projectService).saveNewProject(eq(field), any(ProjectForm.class));
    }
  }
}