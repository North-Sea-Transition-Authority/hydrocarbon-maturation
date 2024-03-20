package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.util.NotificationBannerTestUtil.notificationBanner;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
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
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = SubmissionEstimatesController.class)
class SubmissionEstimatesControllerTest extends AbstractControllerTest {
  @MockBean
  private PermissionService permissionService;
  @MockBean
  private ProjectTrackerControllerService projectTrackerControllerService;
  @MockBean
  private SubmissionEstimatesFormValidator submissionEstimatesFormValidator;
  @MockBean
  private SubmissionEstimatesService submissionEstimatesService;

  private final User USER = UserTestUtil.Builder().build();

  private final UUID PROJECT_ID = UUID.randomUUID();
  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final UUID SUBMISSION_ESTIMATES_ID = UUID.randomUUID();
  private final UUID UNRELATED_ID = UUID.randomUUID();

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(RESOURCE_ID, 8000), PROJECT_ID);
  }

  @SecurityTest
  void getCsrSubmissionEstimates_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getCsrSubmissionEstimates_whenProjectDoesNotMatchResource_thenNotFound() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenThrow(RemiEntityNotFoundException.class);

    mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
  }

  @Test
  void getCsrSubmissionEstimates_whenProjectAnalyst_andProjectMatchesResource_assertProperties() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/projecttracker/addSubmissionEstimates"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle",SubmissionEstimatesController.ESTIMATED_CSR),
            entry("pageCaption", project.getProjectName()),
            entry("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(project.getField().getResource().getId(), project.getId())))
            )
        .containsKey("form");
  }

  @SecurityTest
  void saveCsrSubmissionEstimates_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                .saveCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getRemoveConfirmation_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATES_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void removeEstimates_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                .removeEstimates(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATES_ID,null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }


  @Nested
  class GivenIsProjectAnalyst {
    @BeforeEach
    void setUp() {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);
    }

    @Nested
    class WhenGetCsrSubmissionEstimates {
      @Test
      void andProjectForResourceDoesNotExist_thenNotFound() throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getCsrSubmissionEstimates(RESOURCE_ID, UNRELATED_ID)))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @Test
      void andProjectMatchesResourceFound_assertProperties() throws Exception {
        var modelMap = Objects.requireNonNull(mockMvc.perform(
                    get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                        .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/field/projecttracker/addSubmissionEstimates"))
                .andReturn()
                .getModelAndView())
            .getModelMap();

        assertThat(modelMap)
            .contains(
                entry("pageTitle", SubmissionEstimatesController.ESTIMATED_CSR),
                entry("pageCaption", project.getProjectName()),
                entry("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
                    .getViewProject(project.getField().getResource().getId(), project.getId())))
            )
            .containsKey("form");
      }
    }

    @Nested
    class WhenSaveCsrSubmissionEstimates {
      @Test
      void andProjectForResourceDoesNotExist_thenNotFound() throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .saveCsrSubmissionEstimates(RESOURCE_ID, UNRELATED_ID, null, null, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Test
      void andProjectMatchesResource_andValidForm_thenAssertPropertiesAndVerifyCall() throws Exception {
        when(submissionEstimatesFormValidator.isValid(any(SubmissionEstimatesForm.class), any(BindingResult.class)))
            .thenReturn(true);

        var expectedBanner = NotificationBanner.builder()
            .withBannerType(NotificationBannerType.SUCCESS)
            .withTitle("Success")
            .withContent("Saved estimated CSR first draft information")
            .build();

        mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID))))
            .andExpect(notificationBanner(expectedBanner));

        verify(submissionEstimatesService).saveSubmissionEstimateFromForm(any(SubmissionEstimatesForm.class), eq(project), eq(SubmissionEstimatesType.CSR));
        verifyNoMoreInteractions(submissionEstimatesService);
      }

      @Test
      void andProjectMatchesResource_andInvalidForm_thenAssertPropertiesAndVerifyCall() throws Exception {
        when(submissionEstimatesFormValidator.isValid(any(SubmissionEstimatesForm.class), any(BindingResult.class)))
            .thenReturn(false);

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/projecttracker/addSubmissionEstimates"));

        verify(submissionEstimatesService, never()).saveSubmissionEstimateFromForm(any(), any(), any());
      }
    }

    @Nested
    class WhenGetRemoveConfirmation {
      @Test
      void andProjectForResourceDoesNotExist_thenNotFound() throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getRemoveConfirmation(RESOURCE_ID, UNRELATED_ID, SUBMISSION_ESTIMATES_ID)))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @Test
      void andSubmissionEstimateDoesNotExist_thenNotFound() throws Exception {
        when(submissionEstimatesService.getSubmissionEstimates(UNRELATED_ID))
            .thenReturn(Optional.empty());

        mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, UNRELATED_ID)))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @Test
      void andSubmissionEstimateDoesNotMatchProject_thenNotFound() throws Exception {
        var unrelatedEstimate = new SubmissionEstimates();

        var unrelatedProject = new Project();
        unrelatedProject.setId(UUID.randomUUID());
        unrelatedEstimate.setProject(unrelatedProject);

        when(submissionEstimatesService.getSubmissionEstimates(UNRELATED_ID))
            .thenReturn(Optional.of(unrelatedEstimate));

        mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, UNRELATED_ID)))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @Test
      void andProjectMatchesResourceFound_assertProperties() throws Exception {
        final var FIXED_DATE = LocalDate.of(2024, 11, 3);

        var estimates = new SubmissionEstimates();
        estimates.setProject(project);
        estimates.setDateWhenEstimated(FIXED_DATE);
        estimates.setMostLikelyDate(FIXED_DATE.minusDays(2));

        when(submissionEstimatesService.getSubmissionEstimates(SUBMISSION_ESTIMATES_ID))
            .thenReturn(Optional.of(estimates));

        var modelMap = Objects.requireNonNull(mockMvc.perform(
                    get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                        .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATES_ID)))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/field/projecttracker/removeEstimateConfirmation"))
                .andReturn()
                .getModelAndView())
            .getModelMap();

        assertThat(modelMap)
            .contains(
                entry("pageTitle", "Are you sure you want to remove this estimated CSR first draft submission date?"),
                entry("pageCaption", project.getProjectName()),
                entry("estimates", new SubmissionEstimatesView(
                    "3 November 2024",
                    "",
                    "1 November 2024",
                     ""
                )),
                entry("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
                    .getViewProject(project.getField().getResource().getId(), project.getId())))
            );
      }
    }

    @Nested
    class WhenRemoveEstimates {
      @Test
      void andProjectForResourceDoesNotExist_thenNotFound() throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getRemoveConfirmation(RESOURCE_ID, UNRELATED_ID, SUBMISSION_ESTIMATES_ID)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Test
      void andSubmissionEstimateDoesNotExist_thenNotFound() throws Exception {
        when(submissionEstimatesService.getSubmissionEstimates(UNRELATED_ID))
            .thenReturn(Optional.empty());

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .removeEstimates(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Test
      void andSubmissionEstimateDoesNotMatchProject_thenNotFound() throws Exception {
        var unrelatedEstimate = new SubmissionEstimates();

        var unrelatedProject = new Project();
        unrelatedProject.setId(UUID.randomUUID());
        unrelatedEstimate.setProject(unrelatedProject);

        when(submissionEstimatesService.getSubmissionEstimates(UNRELATED_ID))
            .thenReturn(Optional.of(unrelatedEstimate));

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .removeEstimates(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Test
      void andSubmissionEstimateDoesExist_assertProperties() throws Exception {
        var estimates = new SubmissionEstimates();
        estimates.setProject(project);
        when(submissionEstimatesService.getSubmissionEstimates(SUBMISSION_ESTIMATES_ID))
            .thenReturn(Optional.of(estimates));

        var expectedBanner = NotificationBanner.builder()
            .withBannerType(NotificationBannerType.SUCCESS)
            .withTitle("Success")
            .withContent("Removed CSR first draft submission estimates")
            .build();

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .removeEstimates(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATES_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID))))
            .andExpect(notificationBanner(expectedBanner));

        verify(submissionEstimatesService).removeEstimates(estimates);
      }
    }
  }
}