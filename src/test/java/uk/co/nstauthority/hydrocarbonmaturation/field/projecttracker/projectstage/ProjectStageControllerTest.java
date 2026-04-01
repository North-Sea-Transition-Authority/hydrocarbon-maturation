package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistoryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = ProjectStageController.class)
class ProjectStageControllerTest extends AbstractControllerTest {
  @MockitoBean
  private ProjectStageService projectStageService;
  @MockitoBean
  private ProjectStageHistoryService projectStageHistoryService;
  @MockitoBean
  private ProjectStageFormValidator projectStageFormValidator;

  private final User USER = UserTestUtil.Builder().build();
  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final Integer FIELD_IDENTIFIER = 8000;
  private final UUID PROJECT_ID = UUID.randomUUID();
  private final UUID PROJECT_STAGE_HISTORY_ID = UUID.randomUUID();
  private final UUID UNRELATED_UUID = UUID.randomUUID();

  private final Project project = ProjectTestUtils.createProject(FieldTestUtils.createField(RESOURCE_ID, FIELD_IDENTIFIER), PROJECT_ID);
  private final ProjectStageHistory projectStageHistory = ProjectTestUtils.createProjectStageHistory(project, PROJECT_STAGE_HISTORY_ID);

  @BeforeEach
  void setUp() {
    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));
  }

  @SecurityTest
  void getCreateProjectStageUpdate_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectStageController.class)
                .getCreateProjectStageUpdate(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void getCreateProjectStageUpdate_andProjectIsNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectStageController.class)
                .getCreateProjectStageUpdate(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void saveProjectStageUpdate_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectStageController.class)
                .saveProjectStageUpdate(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void saveProjectStageUpdate_andProjectIsNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectStageController.class)
                .saveProjectStageUpdate(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getRemoveProjectStageUpdate_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectStageController.class)
                .getRemoveProjectStageConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void getRemoveProjectStageUpdate_andProjectIsNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectStageController.class)
                .getRemoveProjectStageConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void removeProjectStage_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectStageController.class)
                .removeProjectStage(RESOURCE_ID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void removeProjectStage_andProjectIsNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectStageController.class)
                .removeProjectStage(RESOURCE_ID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Nested
  class GivenIsProjectAnalystAndProjectIsExtant {
    @BeforeEach
    void setUp() {
      when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
      project.setProjectStatus(ProjectStatus.EXTANT);
    }

    @Nested
    class AndProjectDoesNotExist {
      private ProjectStageHistory projectStageHistory;

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
                entry("pageTitle", ProjectStageController.PROJECT_STAGE_TITLE),
                entry("pageCaption", project.getProjectName()),
                entry("projectStageOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectStage.class)),
                entry("cancelUrl",
                    ReverseRouter.route(on(ViewProjectController.class)
                        .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
            );
      }

      @Test
      void whenSaveProjectStageUpdate_andProjectIsInvalid_thenAssertProperties() throws Exception {
        when(projectStageFormValidator.isValid(any(ProjectStageForm.class), any(BindingResult.class), eq(project)))
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
        when(projectStageFormValidator.isValid(any(ProjectStageForm.class), any(BindingResult.class), eq(project)))
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
                .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
            .andReturn()
            .getModelAndView();

        verify(projectStageService).saveProjectStage(project, validForm);
      }

      @Nested
      class AndProjectStageHistoryDoesNotExist {
        @BeforeEach
        void setUp() {
          when(projectStageHistoryService.getProjectStageHistory(PROJECT_STAGE_HISTORY_ID))
              .thenThrow(RemiEntityNotFoundException.class);
        }

        @SecurityTest
        void whenGetRemoveProjectStageConfirmation_thenIsNotFound() throws Exception {
          mockMvc.perform(
                  get(ReverseRouter.route(on(ProjectStageController.class)
                      .getRemoveProjectStageConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID)))
                      .with(user(USER)))
              .andExpect(status().isNotFound());
        }

        @SecurityTest
        void whenRemoveProjectStage_thenIsNotFound() throws Exception {
          mockMvc.perform(
                  post(ReverseRouter.route(on(ProjectStageController.class)
                      .removeProjectStage(RESOURCE_ID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID, null)))
                      .with(user(USER))
                      .with(csrf()))
              .andExpect(status().isNotFound());
        }
      }

      @Nested
      class AndProjectStageHistoryExists {
        @BeforeEach
        void setUp() {
          when(projectStageHistoryService.getProjectStageHistory(PROJECT_STAGE_HISTORY_ID))
              .thenReturn(Optional.of(projectStageHistory));
        }

        @Nested
        class AndProjectStatusHistoryDoesNotMatchProjectId {
          @SecurityTest
          void whenRemoveProjectStageConfirmation_thenIsNotFound() throws Exception {
            mockMvc.perform(
                    get(ReverseRouter.route(on(ProjectStageController.class)
                        .getRemoveProjectStageConfirmation(RESOURCE_ID, UNRELATED_UUID, PROJECT_ID)))
                        .with(user(USER)))
                .andExpect(status().isNotFound());
          }

          @SecurityTest
          void whenRemoveProjectStage_thenIsNotFound() throws Exception {
            mockMvc.perform(
                    post(ReverseRouter.route(on(ProjectStageController.class)
                        .removeProjectStage(RESOURCE_ID, UNRELATED_UUID, PROJECT_STAGE_HISTORY_ID, null)))
                        .with(user(USER))
                        .with(csrf()))
                .andExpect(status().isNotFound());
          }
        }

        @Nested
        class AndProjectDoesNotMatchResourceId {
          @SecurityTest
          void whenRemoveProjectStageConfirmation_thenIsNotFound() throws Exception {
            mockMvc.perform(
                    get(ReverseRouter.route(on(ProjectStageController.class)
                        .getRemoveProjectStageConfirmation(UNRELATED_UUID, PROJECT_ID, PROJECT_ID)))
                        .with(user(USER)))
                .andExpect(status().isNotFound());
          }

          @SecurityTest
          void whenRemoveProjectStage_thenIsNotFound() throws Exception {
            mockMvc.perform(
                    post(ReverseRouter.route(on(ProjectStageController.class)
                        .removeProjectStage(UNRELATED_UUID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID, null)))
                        .with(user(USER))
                        .with(csrf()))
                .andExpect(status().isNotFound());
          }
        }

        @Test
        void whenGetRemoveProjectStageConfirmation_thenAssertProperties() throws Exception {
          projectStageHistory.setStage(ProjectStage.CONSENT_GRANTED);
          projectStageHistory.setStageDate(LocalDate.of(2002, Month.NOVEMBER, 3));
          var expectedStageDto = new ProjectStageView("Consent granted", "3 November 2002");

          var modelMap = mockMvc.perform(
                  get(ReverseRouter.route(on(ProjectStageController.class)
                      .getRemoveProjectStageConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID)))
                      .with(user(USER)))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/removeProjectStageHistoryConfirmation"))
              .andReturn()
              .getModelAndView()
              .getModelMap();


          assertThat(modelMap)
              .contains(
                  entry("pageTitle", ProjectStageController.REMOVE_PROJECT_STAGE_TITLE),
                  entry("pageCaption", project.getProjectName()),
                  entry("projectStageHistory",expectedStageDto),
                  entry("cancelUrl",
                      ReverseRouter.route(on(ViewProjectController.class)
                          .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
              );
        }
        @Test
        void whenRemoveProjectStage_thenAssertRedirect() throws Exception {
          projectStageHistory.setStage(ProjectStage.CONSENT_GRANTED);
          projectStageHistory.setStageDate(LocalDate.of(2002, Month.NOVEMBER, 3));

          var expectedBanner = NotificationBanner.builder()
              .withBannerType(NotificationBannerType.SUCCESS)
              .withContent("Removed stage update 'Consent granted' on 3 November 2002")
              .withTitle("Success")
              .build();

          mockMvc.perform(
                  post(ReverseRouter.route(on(ProjectStageController.class)
                      .removeProjectStage(RESOURCE_ID, PROJECT_ID, PROJECT_STAGE_HISTORY_ID, null)))
                      .with(user(USER))
                      .with(csrf()))
              .andExpect(status().is3xxRedirection())
              .andExpect(notificationBanner(expectedBanner))
              .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                  .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
              .andReturn()
              .getModelAndView();

          verify(projectStageHistoryService).getProjectStageHistory(PROJECT_STAGE_HISTORY_ID);
          verify(projectStageHistoryService).removeProjectStage(projectStageHistory);
          verifyNoMoreInteractions(projectStageHistoryService);
        }
      }
    }
  }
}