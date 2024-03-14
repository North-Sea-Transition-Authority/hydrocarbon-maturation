package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.util.NotificationBannerTestUtil.notificationBanner;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = ProjectStageController.class)
class ProjectStageControllerTest extends AbstractControllerTest {
  @MockBean
  private PermissionService permissionService;
  @MockBean
  private ProjectService projectService;
  @MockBean
  private ProjectStageService projectStageService;
  @MockBean
  private ProjectStageFormValidator projectStageFormValidator;

  private final User USER = UserTestUtil.Builder().build();
  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final Integer FIELD_IDENTIFIER = 8000;
  private final UUID PROJECT_ID = UUID.randomUUID();
  private final UUID UNRELATED_UUID = UUID.randomUUID();

  private final Project project = ProjectTestUtils.createProject(FieldTestUtils.createField(RESOURCE_ID, FIELD_IDENTIFIER), PROJECT_ID);

  @SecurityTest
  void getCreateProjectStageUpdate_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectStageController.class)
                .getCreateProjectStageUpdate(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveProjectStageUpdate_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectStageController.class)
                .saveProjectStageUpdate(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Nested
  class GivenIsProjectAnalyst {
    @BeforeEach
    void setUp() {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);
    }

    @Nested
    class AndProjectDoesNotExist {
      @BeforeEach
      void setUp() {
        when(projectService.getProjectOrThrow(PROJECT_ID)).thenThrow(RemiEntityNotFoundException.class);
      }

      @SecurityTest
      void whenGetCreateProjectStageUpdate_thenIsNotFound() throws Exception {
        mockMvc.perform(
                get(ReverseRouter.route(on(ProjectStageController.class)
                    .getCreateProjectStageUpdate(RESOURCE_ID, PROJECT_ID)))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @SecurityTest
      void whenSaveProjectStageUpdate_thenIsNotFound() throws Exception {
        mockMvc.perform(
                post(ReverseRouter.route(on(ProjectStageController.class)
                    .saveProjectStageUpdate(RESOURCE_ID, PROJECT_ID, null, null, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }
    }

    @Nested
    class AndProjectExists {
      @BeforeEach
      void setUp() {
        when(projectService.getProjectOrThrow(PROJECT_ID)).thenReturn(project);
      }

      @Nested
      class AndProjectDoesNotMatchResourceId {
        @SecurityTest
        void whenGetCreateProjectStageUpdate_thenIsNotFound() throws Exception {
          mockMvc.perform(
                  get(ReverseRouter.route(on(ProjectStageController.class)
                      .getCreateProjectStageUpdate(UNRELATED_UUID, PROJECT_ID)))
                      .with(user(USER)))
              .andExpect(status().isNotFound());
        }

        @SecurityTest
        void whenSaveProjectStageUpdate_thenIsNotFound() throws Exception {
          mockMvc.perform(
                  post(ReverseRouter.route(on(ProjectStageController.class)
                      .saveProjectStageUpdate(UNRELATED_UUID, PROJECT_ID, null, null, null)))
                      .with(user(USER))
                      .with(csrf()))
              .andExpect(status().isNotFound());
        }
      }

      @Test
      void whenGetCreateProjectStageUpdate_thenAssertProperties() throws Exception {
        var modelMap = mockMvc.perform(
                get(ReverseRouter.route(on(ProjectStageController.class)
                    .getCreateProjectStageUpdate(RESOURCE_ID, PROJECT_ID)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/projecttracker/createProjectStageHistory"))
            .andReturn()
            .getModelAndView()
            .getModelMap();

        assertThat(modelMap)
            .contains(
                entry("pageTitle", ProjectStageController.PAGE_TITLE),
                entry("pageCaption", project.getProjectName()),
                entry("projectStageOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectStage.class)),
                entry("cancelUrl",
                    ReverseRouter.route(on(ViewProjectController.class)
                        .getViewProject(RESOURCE_ID, PROJECT_ID)))
            );
      }

      @Test
      void whenSaveProjectStageUpdate_andProjectIsInvalid_thenAssertProperties() throws Exception {
        when(projectStageFormValidator.isValid(any(ProjectStageForm.class), any(BindingResult.class)))
            .thenReturn(false);

        var invalidForm = new ProjectStageForm();
        invalidForm.setStage(null);

        mockMvc.perform(
                post(ReverseRouter.route(on(ProjectStageController.class)
                    .saveProjectStageUpdate(RESOURCE_ID, PROJECT_ID, null, null, null)))
                    .with(user(USER))
                    .with(csrf())
                    .flashAttr("form", invalidForm))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/projecttracker/createProjectStageHistory"))
            .andExpect(model().attribute("form", invalidForm))
            .andReturn()
            .getModelAndView();

        verify(projectStageService, never()).saveProjectStage(any(Project.class), any(ProjectStageForm.class));
      }

      @Test
      void whenSaveProjectStageUpdate_andProjectValid_thenAssertRedirect() throws Exception {
        when(projectStageFormValidator.isValid(any(ProjectStageForm.class), any(BindingResult.class)))
            .thenReturn(true);

        var validForm = new ProjectStageForm();
        validForm.setStage(ProjectStage.CONSENT_GRANTED);

        var expectedBanner = NotificationBanner.builder()
            .withBannerType(NotificationBannerType.SUCCESS)
            .withContent("Added stage update '%s'".formatted(validForm.getStage().getDisplayName()))
            .withTitle("Success")
            .build();

        mockMvc.perform(
                post(ReverseRouter.route(on(ProjectStageController.class)
                    .saveProjectStageUpdate(RESOURCE_ID, PROJECT_ID, null, null, null)))
                    .with(user(USER))
                    .with(csrf())
                    .flashAttr("form", validForm))
            .andExpect(status().is3xxRedirection())
            .andExpect(notificationBanner(expectedBanner))
            .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID))))
            .andReturn()
            .getModelAndView();

        verify(projectStageService).saveProjectStage(project, validForm);
      }
    }
  }
}