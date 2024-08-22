package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ContextConfiguration(classes = ProjectListController.class)
class ProjectListControllerTest extends AbstractProjectTrackerControllerTest {
  @SecurityTest
  void getProjectList_whenNotRegulator_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectListController.class)
                .getProjectList(RESOURCE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getProjectList_whenRegulatorOrAreaTeamMember_thenOk() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectListController.class)
                .getProjectList(RESOURCE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getProjectList_whenFieldDoesNotExist_thenNotFound() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var resourceIdForFieldThatDoesNotExist = UUID.randomUUID();
    when(fieldPersistenceService.getFieldOrThrow(resourceIdForFieldThatDoesNotExist))
        .thenThrow(RemiEntityNotFoundException.class);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectListController.class)
                .getProjectList(resourceIdForFieldThatDoesNotExist, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void getProjectList_nonProjectAnalyst_assertProperties() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    var views = List.of(new ProjectListView("name", "caption", "url"));
    when(projectService.getCreatedProjectListViews(field))
        .thenReturn(views);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ProjectListController.class)
                    .getProjectList(RESOURCE_ID, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ProjectListController.PAGE_TITLE),
            entry("pageCaption", field.getCorrectedLegalFieldName()),
            entry("projectViews", views)
        ).doesNotContainKey("createProjectUrl");
  }

  @Test
  void getProjectList_whenProjectAnalyst_assertProjectAnalystOnlyProperties() throws Exception {
    setupUserIsProjectAnalyst(user);

    var views = List.of(new ProjectListView("name", "caption", "url"));
    when(projectService.getCreatedProjectListViews(field))
        .thenReturn(views);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ProjectListController.class)
                    .getProjectList(RESOURCE_ID, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ProjectListController.PAGE_TITLE),
            entry("pageCaption", field.getCorrectedLegalFieldName()),
            entry("projectViews", views),
            entry("createProjectUrl", ReverseRouter.route(on(ProjectDetailsController.class).getCreateProject(RESOURCE_ID)))
        );
  }

  @Test
  void getProjectList_assertBreadcrumbs() throws Exception {
    setupUserIsProjectAnalyst(user);

    var views = List.of(new ProjectListView("name", "caption", "url"));
    when(projectService.getCreatedProjectListViews(field))
        .thenReturn(views);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ProjectListController.class)
                    .getProjectList(RESOURCE_ID, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView())
        .getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(
                ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null, null)),
                SearchFieldRecordsController.PAGE_TITLE
            ),
            entry(
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID, null)),
                field.getCorrectedLegalFieldName()
            )
        );
  }
}