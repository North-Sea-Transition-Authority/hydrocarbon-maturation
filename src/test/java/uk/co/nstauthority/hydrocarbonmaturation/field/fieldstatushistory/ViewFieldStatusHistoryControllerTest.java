package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ViewFieldStatusHistoryController.class)
class ViewFieldStatusHistoryControllerTest extends AbstractSubmissionControllerTest {
  @MockitoBean
  private ResourceService resourceService;
  @MockitoBean
  private ViewFieldStatusHistoryService viewFieldStatusHistoryService;

  private Resource resource;
  private Field field;

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.FIELD);

    field = new Field();
    field.setResource(resource);
    field.setFieldIdentifier(8000);
    field.setCorrectedLegalFieldName("Field name");
  }

  @SecurityTest
  void getViewFieldStatusHistory_whenRegulator_assertOk() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);

    mockMvc.perform(
            get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class).getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getViewFieldStatusHistoryWithSubmissionDetail_whenRegulator_assertOk() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);

    mockMvc.perform(
            get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                .getViewFieldStatusHistoryWithSubmissionDetail(field.getFieldIdentifier(), submissionDetail.getId(), null)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getViewFieldStatusHistory_whenNotRegulator_assertForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class).getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getViewFieldStatusHistoryWithSubmissionDetail_whenNotRegulator_assertForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                .getViewFieldStatusHistoryWithSubmissionDetail(field.getFieldIdentifier(), submissionDetail.getId(), null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getViewFieldStatusHistory_whenFieldDoesNotExist_assertThrowsRemiEntityNotFound() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenCallRealMethod();

    assertThatThrownBy(() ->
        mockMvc.perform(
            get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                .getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
                .with(user(USER)))
    ).hasCauseExactlyInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void getViewFieldStatusHistoryWithSubmissionDetail_whenFieldDoesNotExist_assertThrowsRemiEntityNotFound() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenCallRealMethod();

    assertThatThrownBy(() ->
        mockMvc.perform(
            get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                .getViewFieldStatusHistoryWithSubmissionDetail(field.getFieldIdentifier(), submissionDetail.getId(), null)))
                .with(user(USER)))
    ).hasCauseExactlyInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void getViewFieldStatusHistoryWithSubmissionDetail_whenSubmissionDetailDoesNotExist_assertThrowsEntityNotFound() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenThrow(EntityNotFoundException.class);

    assertThatThrownBy(() ->
        mockMvc.perform(
            get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                .getViewFieldStatusHistoryWithSubmissionDetail(field.getFieldIdentifier(), submissionDetail.getId(), null)))
                .with(user(USER)))
    ).hasCauseExactlyInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void getViewFieldStatusHistory_userIsProjectAnalyst_andMoreThanOneFieldStatusHistoryExists_assertProperties() throws Exception {
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);


    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var fieldStatusHistoryViews = List.of(
        new FieldStatusHistoryDto("/remove", FieldStatus.STATUS_200.getDisplayName(), "29th January 2024"),
        new FieldStatusHistoryDto("/remove", FieldStatus.STATUS_300.getDisplayName(), "30th January 2024")
    );
    when(viewFieldStatusHistoryService.getFieldStatusHistoryViews(field))
        .thenReturn(fieldStatusHistoryViews);

    var canRemoveFieldStatus = true; // because user is project analyst and there is more than one field status history

    var isProjectAnalyst = true;
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(isProjectAnalyst);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class).getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/fieldstatushistory/viewFieldStatusHistory"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ViewFieldStatusHistoryController.PAGE_TITLE),
            entry("pageCaption", field.getCorrectedLegalFieldName()),
            entry("fieldStatusHistoryViews", fieldStatusHistoryViews),
            entry("canAddFieldStatus", isProjectAnalyst),
            entry("canRemoveFieldStatus", canRemoveFieldStatus),
            entry("addFieldStatusUrl",
                ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                    .getAddFieldStatus(field.getFieldIdentifier())))
        );
  }

  @Test
  void getViewFieldStatusHistory_userIsProjectAnalyst_andOnlyOneFieldStatusHistoryExists_assertProperties() throws Exception {
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var fieldStatusHistoryViews = List.of(
        new FieldStatusHistoryDto("/remove", FieldStatus.STATUS_200.getDisplayName(), "29th January 2024")
    );
    var canRemoveFieldStatus = false; // because only one field status history

    when(viewFieldStatusHistoryService.getFieldStatusHistoryViews(field))
        .thenReturn(fieldStatusHistoryViews);

    var isProjectAnalyst = true;
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(isProjectAnalyst);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class).getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/fieldstatushistory/viewFieldStatusHistory"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ViewFieldStatusHistoryController.PAGE_TITLE),
            entry("pageCaption", field.getCorrectedLegalFieldName()),
            entry("fieldStatusHistoryViews", fieldStatusHistoryViews),
            entry("canAddFieldStatus", isProjectAnalyst),
            entry("canRemoveFieldStatus", canRemoveFieldStatus),
            entry("addFieldStatusUrl",
                ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                    .getAddFieldStatus(field.getFieldIdentifier())))
        );
  }

  @Test
  void getViewFieldStatusHistory_userIsNotProjectAnalyst_assertProperties() throws Exception {
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var fieldStatusHistoryViews = List.of(new FieldStatusHistoryDto("/remove", FieldStatus.STATUS_200.getDisplayName(), "29th January 2024"));
    when(viewFieldStatusHistoryService.getFieldStatusHistoryViews(field))
        .thenReturn(fieldStatusHistoryViews);

    var isProjectAnalyst = false;
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(isProjectAnalyst);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class).getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/fieldstatushistory/viewFieldStatusHistory"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ViewFieldStatusHistoryController.PAGE_TITLE),
            entry("pageCaption", field.getCorrectedLegalFieldName()),
            entry("fieldStatusHistoryViews", fieldStatusHistoryViews),
            entry("canAddFieldStatus", isProjectAnalyst)
        ).doesNotContainKey("addFieldStatusUrl");
  }

  @Test
  void getViewFieldStatusHistoryWithSubmissionDetail_assertProperties() throws Exception {
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var fieldStatusHistoryViews = List.of(new FieldStatusHistoryDto("/remove", FieldStatus.STATUS_200.getDisplayName(), "29th January 2024"));
    when(viewFieldStatusHistoryService.getFieldStatusHistoryViews(field))
        .thenReturn(fieldStatusHistoryViews);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                    .getViewFieldStatusHistoryWithSubmissionDetail(field.getFieldIdentifier(), submissionDetail.getId(), null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/fieldstatushistory/viewFieldStatusHistory"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ViewFieldStatusHistoryController.PAGE_TITLE),
            entry("pageCaption", field.getCorrectedLegalFieldName()),
            entry("fieldStatusHistoryViews", fieldStatusHistoryViews)
        );
  }

  @Test
  void getViewFieldStatusHistory_assertBreadcrumbs() throws Exception {
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var fieldStatusHistoryViews = List.of(new FieldStatusHistoryDto("/remove", FieldStatus.STATUS_200.getDisplayName(), "29th January 2024"));
    when(viewFieldStatusHistoryService.getFieldStatusHistoryViews(field))
        .thenReturn(fieldStatusHistoryViews);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class).getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/fieldstatushistory/viewFieldStatusHistory"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(
                ReverseRouter.route(on(SearchFieldRecordsController.class).searchResourceRecords(null, null)),
                SearchFieldRecordsController.PAGE_TITLE
            ),
            entry(
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(field.getResource().getId(), null)),
                field.getCorrectedLegalFieldName()
            )
        );
  }

  @Test
  void getViewFieldStatusHistoryWithSubmissionDetail_assertBreadcrumbs() throws Exception {
    when(resourceService.getResourceById(resource.getId()))
        .thenReturn(Optional.of(resource));
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var fieldStatusHistoryViews = List.of(new FieldStatusHistoryDto("/remove", FieldStatus.STATUS_200.getDisplayName(), "29th January 2024"));
    when(viewFieldStatusHistoryService.getFieldStatusHistoryViews(field))
        .thenReturn(fieldStatusHistoryViews);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                    .getViewFieldStatusHistoryWithSubmissionDetail(field.getFieldIdentifier(), submissionDetail.getId(), null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/fieldstatushistory/viewFieldStatusHistory"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(
                ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)),
                WorkAreaController.WORK_AREA_TITLE
            ),
            entry(
                ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(field.getResource().getId(), null)),
                field.getCorrectedLegalFieldName()
            )
        );
  }
}