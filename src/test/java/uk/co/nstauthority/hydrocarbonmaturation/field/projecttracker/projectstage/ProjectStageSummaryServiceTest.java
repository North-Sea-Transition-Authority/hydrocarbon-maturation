package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTimelineView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TimelineEvent;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class ProjectStageSummaryServiceTest {
  @Mock
  private UserService userService;
  @Mock
  private PermissionService permissionService;
  @Mock
  private ProjectStageService projectStageService;
  @InjectMocks
  private ProjectStageSummaryService projectStageSummaryService;

  private static final LocalDate FIXED_DATE = LocalDate.of(2024, Month.MARCH, 4);

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createPopulatedProject(
        "Project",
        ProjectType.FIELD_DEVELOPMENT_PLAN,
        ProjectStatus.ASSESSMENT_PHASE,
        true,
        Instant.parse("2024-02-01T00:00:00.00Z"));
  }

  @Nested
  class GivenAnyUser {
    @Test
    void whenGetSummarySection_andProjectHasNotStages_thenAssertCommonProperties() {
      var summarySectionOptional = projectStageSummaryService.getSummarySection(project);

      assertThat(summarySectionOptional)
          .isPresent()
          .get()
          .extracting(
              SummarySection::summaryText,
              SummarySection::displayOrder
          ).containsExactly(
              "",
              ProjectStageSummaryService.DISPLAY_ORDER
          );

      var summaryItems = summarySectionOptional.get().summaryItems();

      assertThat(summaryItems)
          .hasSize(1)
          .extracting(SummaryItem::displayName)
          .contains(ProjectStageSummaryService.PROJECT_STAGE);

      var summaryItem = summaryItems.get(0);
      assertThat(summaryItem.displayName()).isEqualTo(ProjectStageSummaryService.PROJECT_STAGE);
      assertThat(summaryItem.summaryCards()).hasSize(1);

      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectStageSummaryService.PROJECT_STAGE,
              SummaryCardType.EMPTY_SUMMARY_WITH_ACTIONS
          );
    }

    @Test
    void whenGetSummarySection_andProjectHasStages_thenAssertCommonProperties() {
      var firstCreatedStageHistory = new ProjectStageHistory();
      firstCreatedStageHistory.setId(UUID.randomUUID());
      firstCreatedStageHistory.setProject(project);
      firstCreatedStageHistory.setStage(ProjectStage.CONSENT_GRANTED);
      firstCreatedStageHistory.setStageDate(FIXED_DATE.plusDays(1));

      var secondCreatedStageHistory = new ProjectStageHistory();
      secondCreatedStageHistory.setId(UUID.randomUUID());
      secondCreatedStageHistory.setProject(project);
      secondCreatedStageHistory.setStage(ProjectStage.AWAITING_OPERATOR_INFO);
      secondCreatedStageHistory.setStageDate(FIXED_DATE.plusMonths(1));

      var lastCreatedStageHistory = new ProjectStageHistory();
      lastCreatedStageHistory.setId(UUID.randomUUID());
      lastCreatedStageHistory.setProject(project);
      lastCreatedStageHistory.setStage(ProjectStage.FDP_TECHNICAL_COMMENTS_WITH_REGULATOR);
      lastCreatedStageHistory.setStageDate(FIXED_DATE.plusYears(1));

      when(projectStageService.getOrderedProjectStages(project))
          .thenReturn(List.of(firstCreatedStageHistory, secondCreatedStageHistory, lastCreatedStageHistory));

      var summarySectionOptional = projectStageSummaryService.getSummarySection(project);

      assertThat(summarySectionOptional)
          .isPresent()
          .get()
          .extracting(
              SummarySection::summaryText,
              SummarySection::displayOrder
          ).containsExactly(
              "",
              ProjectStageSummaryService.DISPLAY_ORDER
          );

      var summaryItems = summarySectionOptional.get().summaryItems();

      assertThat(summaryItems)
          .hasSize(1)
          .extracting(SummaryItem::displayName)
          .contains(ProjectStageSummaryService.PROJECT_STAGE);

      var summaryItem = summaryItems.get(0);
      assertThat(summaryItem.displayName()).isEqualTo(ProjectStageSummaryService.PROJECT_STAGE);
      assertThat(summaryItem.summaryCards()).hasSize(1);

      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectStageSummaryService.PROJECT_STAGE,
              SummaryCardType.TIMELINE_SUMMARY
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryTimelineView.class);

      var summaryTimelineView = (SummaryTimelineView) summaryData;
      assertThat(summaryTimelineView.timelineEvents())
          .extracting(
              TimelineEvent::heading,
              TimelineEvent::hint
          )
          .containsExactly(
              Tuple.tuple(
                  firstCreatedStageHistory.getStage().getDisplayName(),
                  DateUtil.formatLongDate(firstCreatedStageHistory.getStageDate())
              ),
              Tuple.tuple(
                  secondCreatedStageHistory.getStage().getDisplayName(),
                  DateUtil.formatLongDate(secondCreatedStageHistory.getStageDate())
              ),
              Tuple.tuple(
                  lastCreatedStageHistory.getStage().getDisplayName(),
                  DateUtil.formatLongDate(lastCreatedStageHistory.getStageDate())
              )
          );
    }
  }

  @Nested
  class GivenUserIsProjectAnalyst {
    private static final boolean IS_PROJECT_ANALYST = true;

    @BeforeEach
    void setUp() {
      mockUserCallsWithPermissions(IS_PROJECT_ANALYST);
    }

    @Test
    void whenGetSummarySection_withStages_thenPopulatedCardActionMap() {
      var expectedActionsMap = getProjectAnalystActionsMap();

      var summaryTimelineView = getSectionSummaryDataWhenStages();
      assertThat(summaryTimelineView.cardActionsMap())
          .usingRecursiveComparison()
          .isEqualTo(expectedActionsMap);
    }

    @Test
    void whenGetSummarySection_withNoStages_thenPopulatedCardActionMap() {
      var expectedActionsMap = getProjectAnalystActionsMap();

      assertThat(getSectionSummaryDataWhenNoStages())
          .usingRecursiveComparison()
          .isEqualTo(expectedActionsMap);
    }

    @Test
    void whenGetSummarySection_withStages_thenPopulatedTimelineActionMap() {
      var firstCreatedStageHistory = new ProjectStageHistory();
      firstCreatedStageHistory.setId(UUID.randomUUID());
      firstCreatedStageHistory.setProject(project);
      firstCreatedStageHistory.setStage(ProjectStage.CONSENT_GRANTED);
      firstCreatedStageHistory.setStageDate(FIXED_DATE.plusDays(1));

      var secondCreatedStageHistory = new ProjectStageHistory();
      secondCreatedStageHistory.setId(UUID.randomUUID());
      secondCreatedStageHistory.setProject(project);
      secondCreatedStageHistory.setStage(ProjectStage.AWAITING_OPERATOR_INFO);
      secondCreatedStageHistory.setStageDate(FIXED_DATE.plusMonths(1));

      var lastCreatedStageHistory = new ProjectStageHistory();
      lastCreatedStageHistory.setId(UUID.randomUUID());
      lastCreatedStageHistory.setProject(project);
      lastCreatedStageHistory.setStage(ProjectStage.FDP_TECHNICAL_COMMENTS_WITH_REGULATOR);
      lastCreatedStageHistory.setStageDate(FIXED_DATE.plusYears(1));

      when(projectStageService.getOrderedProjectStages(project))
          .thenReturn(List.of(firstCreatedStageHistory, secondCreatedStageHistory, lastCreatedStageHistory));

      var summarySectionOptional = projectStageSummaryService.getSummarySection(project);
      var summaryItems = summarySectionOptional.get().summaryItems();
      var summaryItem = summaryItems.get(0);
      var summaryCard = summaryItem.summaryCards().get(0);
      var summaryData = summaryCard.summaryData();

      var summaryTimelineView = (SummaryTimelineView) summaryData;
      assertThat(summaryTimelineView.timelineEvents())
          .extracting(
              TimelineEvent::eventActionsMap
          )
          .containsExactly(
              Map.of(
                  "Remove",
                  ReverseRouter.route(on(ProjectStageController.class)
                      .getRemoveProjectStageConfirmation(
                          project.getField().getResource().getId(),
                          project.getId(),
                          firstCreatedStageHistory.getId()))),
              Map.of(
                  "Remove",
                  ReverseRouter.route(on(ProjectStageController.class)
                      .getRemoveProjectStageConfirmation(
                          project.getField().getResource().getId(),
                          project.getId(),
                          secondCreatedStageHistory.getId()))
              ),
              Map.of(
                  "Remove",
                  ReverseRouter.route(on(ProjectStageController.class)
                      .getRemoveProjectStageConfirmation(
                          project.getField().getResource().getId(),
                          project.getId(),
                          lastCreatedStageHistory.getId()))
              ));
    }

    private Map<String, String> getProjectAnalystActionsMap() {
      return Map.of(
          "Add",
          ReverseRouter.route(on(ProjectStageController.class)
              .getCreateProjectStageUpdate(project.getField().getResource().getId(), project.getId()))
      );
    }
  }

  @Nested
  class GivenUserIsNotProjectAnalyst {
    private static final boolean IS_PROJECT_ANALYST = false;

    @BeforeEach
    void setUp() {
      mockUserCallsWithPermissions(IS_PROJECT_ANALYST);
    }

    @Test
    void whenGetSummarySection_withStages_thenEmptyActionMap() {
      var expectedActionsMap = Map.of();
      assertThat(getSectionSummaryDataWhenStages().cardActionsMap())
          .usingRecursiveComparison()
          .isEqualTo(expectedActionsMap);
    }

    @Test
    void whenGetSummarySection_withNoStages_thenEmptyActionMap() {

      var expectedActionsMap = Map.of();
      assertThat(getSectionSummaryDataWhenNoStages())
          .usingRecursiveComparison()
          .isEqualTo(expectedActionsMap);
    }

    @Test
    void whenGetSummarySection_withStages_thenPopulatedTimelineActionMap() {
      var firstCreatedStageHistory = new ProjectStageHistory();
      firstCreatedStageHistory.setId(UUID.randomUUID());
      firstCreatedStageHistory.setProject(project);
      firstCreatedStageHistory.setStage(ProjectStage.CONSENT_GRANTED);
      firstCreatedStageHistory.setStageDate(FIXED_DATE.plusDays(1));

      var secondCreatedStageHistory = new ProjectStageHistory();
      secondCreatedStageHistory.setId(UUID.randomUUID());
      secondCreatedStageHistory.setProject(project);
      secondCreatedStageHistory.setStage(ProjectStage.AWAITING_OPERATOR_INFO);
      secondCreatedStageHistory.setStageDate(FIXED_DATE.plusMonths(1));

      var lastCreatedStageHistory = new ProjectStageHistory();
      lastCreatedStageHistory.setId(UUID.randomUUID());
      lastCreatedStageHistory.setProject(project);
      lastCreatedStageHistory.setStage(ProjectStage.FDP_TECHNICAL_COMMENTS_WITH_REGULATOR);
      lastCreatedStageHistory.setStageDate(FIXED_DATE.plusYears(1));

      when(projectStageService.getOrderedProjectStages(project))
          .thenReturn(List.of(firstCreatedStageHistory, secondCreatedStageHistory, lastCreatedStageHistory));

      var summarySectionOptional = projectStageSummaryService.getSummarySection(project);
      var summaryItems = summarySectionOptional.get().summaryItems();
      var summaryItem = summaryItems.get(0);
      var summaryCard = summaryItem.summaryCards().get(0);
      var summaryData = summaryCard.summaryData();

      var summaryTimelineView = (SummaryTimelineView) summaryData;
      assertThat(summaryTimelineView.timelineEvents())
          .extracting(
              TimelineEvent::eventActionsMap
          )
          .containsOnly(Map.of());
    }
  }

  private void mockUserCallsWithPermissions(boolean isProjectAnalyst) {
    var projectAnalystUser = UserTestUtil.Builder().build();
    when(userService.getUser())
        .thenReturn(projectAnalystUser);
    when(permissionService.hasPermission(projectAnalystUser, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(isProjectAnalyst);
  }

  private SummaryTimelineView getSectionSummaryDataWhenStages() {
    var stage = ProjectTestUtils.createProjectStageHistory(project, UUID.randomUUID());
    stage.setStage(ProjectStage.CONSENT_GRANTED);
    stage.setStageDate(LocalDate.now());
    when(projectStageService.getOrderedProjectStages(project))
        .thenReturn(List.of(stage));

    var summarySectionOptional = projectStageSummaryService.getSummarySection(project);

    var summaryItems = summarySectionOptional.get().summaryItems();

    var summaryItem = summaryItems.get(0);

    var summaryCard = summaryItem.summaryCards().get(0);

    var summaryData = summaryCard.summaryData();

    return (SummaryTimelineView) summaryData;
  }

  private Map<String, String> getSectionSummaryDataWhenNoStages() {
    var summarySectionOptional = projectStageSummaryService.getSummarySection(project);

    var summaryItems = summarySectionOptional.get().summaryItems();

    var summaryItem = summaryItems.get(0);

    var summaryCard = summaryItem.summaryCards().get(0);

    var summaryData = summaryCard.summaryData();

    return (Map<String, String>) summaryData;
  }
}