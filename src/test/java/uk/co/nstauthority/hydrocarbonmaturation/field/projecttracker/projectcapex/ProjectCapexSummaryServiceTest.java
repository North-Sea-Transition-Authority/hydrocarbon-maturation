package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectPhase;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTimelineView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TimelineEvent;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class ProjectCapexSummaryServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private TeamQueryService teamQueryService;
  @Mock
  private ProjectCapexService projectStageService;
  @InjectMocks
  private ProjectCapexSummaryService projectStageSummaryService;

  private static final LocalDate FIXED_DATE = LocalDate.of(2024, Month.MARCH, 4);

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createPopulatedProject(
        "Project",
        ProjectType.FIELD_DEVELOPMENT_PLAN,
        ProjectPhase.ASSESSMENT_PHASE,
        true,
        Instant.parse("2024-02-01T00:00:00.00Z"));
    project.setField(FieldTestUtils.createField(8000));
  }

  @Nested
  class GivenAnyUser {
    @ParameterizedTest
    @EnumSource(
        value = ProjectType.class,
        names = {"FIELD_DEVELOPMENT_PLAN", "FIELD_DEVELOPMENT_PLAN_ADDENDUM"},
        mode = EnumSource.Mode.EXCLUDE
    )
    void whenGetSummarySection_andProjectIsNotApplicableType_thenAssertEmpty(ProjectType projectType) {
      project.setProjectType(projectType);

      var summarySectionOptional = projectStageSummaryService.getSummarySection(project, USER);

      assertThat(summarySectionOptional)
          .isEmpty();
    }

    @Test
    void whenGetSummarySection_andProjectHasNotStages_thenAssertCommonProperties() {
      var summarySectionOptional = projectStageSummaryService.getSummarySection(project, USER);

      assertThat(summarySectionOptional)
          .isPresent()
          .get()
          .extracting(
              SummarySection::summaryText,
              SummarySection::displayOrder
          ).containsExactly(
              "",
              ProjectCapexSummaryService.DISPLAY_ORDER
          );

      var summaryItems = summarySectionOptional.get().summaryItems();

      assertThat(summaryItems)
          .hasSize(1)
          .extracting(SummaryItem::displayName)
          .contains(ProjectCapexSummaryService.PROJECT_CAPEX);

      var summaryItem = summaryItems.get(0);
      assertThat(summaryItem.displayName()).isEqualTo(ProjectCapexSummaryService.PROJECT_CAPEX);
      assertThat(summaryItem.summaryCards()).hasSize(1);

      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectCapexSummaryService.PROJECT_CAPEX,
              SummaryCardType.EMPTY_SUMMARY
          );
    }

    @Test
    void whenGetSummarySection_andProjectHasStages_thenAssertCommonProperties() {
      var firstCreatedCapex = new ProjectCapex(UUID.randomUUID());
      firstCreatedCapex.setProject(project);
      firstCreatedCapex.setSourceOfInformation(ProjectCapexSource.UKSS);
      firstCreatedCapex.setDateProvided(FIXED_DATE.plusDays(1));
      firstCreatedCapex.setTotalCapex(BigDecimal.valueOf(24));

      var secondCreatedCapex = new ProjectCapex(UUID.randomUUID());
      secondCreatedCapex.setProject(project);
      secondCreatedCapex.setSourceOfInformation(ProjectCapexSource.UKSS);
      secondCreatedCapex.setDateProvided(FIXED_DATE.plusMonths(1));
      secondCreatedCapex.setTotalCapex(BigDecimal.valueOf(25));

      var lastCreatedCapex = new ProjectCapex(UUID.randomUUID());
      lastCreatedCapex.setProject(project);
      lastCreatedCapex.setSourceOfInformation(ProjectCapexSource.UKSS);
      lastCreatedCapex.setDateProvided(FIXED_DATE.plusYears(1));
      lastCreatedCapex.setTotalCapex(BigDecimal.valueOf(26));

      when(projectStageService.getOrderedCreatedProjectCapexs(project))
          .thenReturn(List.of(firstCreatedCapex, secondCreatedCapex, lastCreatedCapex));

      var summarySectionOptional = projectStageSummaryService.getSummarySection(project, USER);

      assertThat(summarySectionOptional)
          .isPresent()
          .get()
          .extracting(
              SummarySection::summaryText,
              SummarySection::displayOrder
          ).containsExactly(
              "",
              ProjectCapexSummaryService.DISPLAY_ORDER
          );

      var summaryItems = summarySectionOptional.get().summaryItems();

      assertThat(summaryItems)
          .hasSize(1)
          .extracting(SummaryItem::displayName)
          .contains(ProjectCapexSummaryService.PROJECT_CAPEX);

      var summaryItem = summaryItems.get(0);
      assertThat(summaryItem.displayName()).isEqualTo(ProjectCapexSummaryService.PROJECT_CAPEX);
      assertThat(summaryItem.summaryCards()).hasSize(1);

      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectCapexSummaryService.PROJECT_CAPEX,
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
                  "£%s million".formatted(firstCreatedCapex.getTotalCapex()),
                  "%s - provided %s".formatted(
                      firstCreatedCapex.getSourceOfInformation().getDisplayName(),
                      DateUtil.formatLongDate(firstCreatedCapex.getDateProvided()))
              ),
              Tuple.tuple(
                  "£%s million".formatted(secondCreatedCapex.getTotalCapex()),
                  "%s - provided %s".formatted(
                      secondCreatedCapex.getSourceOfInformation().getDisplayName(),
                      DateUtil.formatLongDate(secondCreatedCapex.getDateProvided()))
              ),
              Tuple.tuple(
                  "£%s million".formatted(lastCreatedCapex.getTotalCapex()),
                  "%s - provided %s".formatted(
                      lastCreatedCapex.getSourceOfInformation().getDisplayName(),
                      DateUtil.formatLongDate(lastCreatedCapex.getDateProvided()))
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
      var expectedActions = getProjectAnalystActions();

      var summaryTimelineView = getSectionSummaryCardWhenStages();
      assertThat(summaryTimelineView.summaryActions())
          .usingRecursiveComparison()
          .isEqualTo(expectedActions);
    }

    @Test
    void whenGetSummarySection_withNoStages_thenPopulatedCardActionMap() {
      var expectedActions = getProjectAnalystActions();

      assertThat(getSectionSummaryCardWhenNoStages().summaryActions())
          .usingRecursiveComparison()
          .isEqualTo(expectedActions);
    }

    @Test
    void whenGetSummarySection_withStages_thenPopulatedTimelineActionMap() {
      var firstCreatedCapex = new ProjectCapex(UUID.randomUUID());
      firstCreatedCapex.setProject(project);
      firstCreatedCapex.setSourceOfInformation(ProjectCapexSource.UKSS);
      firstCreatedCapex.setDateProvided(FIXED_DATE.plusDays(1));
      firstCreatedCapex.setTotalCapex(BigDecimal.valueOf(24));

      var secondCreatedCapex = new ProjectCapex(UUID.randomUUID());
      secondCreatedCapex.setProject(project);
      secondCreatedCapex.setSourceOfInformation(ProjectCapexSource.UKSS);
      secondCreatedCapex.setDateProvided(FIXED_DATE.plusMonths(1));
      secondCreatedCapex.setTotalCapex(BigDecimal.valueOf(24));

      var lastCreatedCapex = new ProjectCapex(UUID.randomUUID());
      lastCreatedCapex.setProject(project);
      lastCreatedCapex.setSourceOfInformation(ProjectCapexSource.UKSS);
      lastCreatedCapex.setDateProvided(FIXED_DATE.plusYears(1));
      lastCreatedCapex.setTotalCapex(BigDecimal.valueOf(24));

      when(projectStageService.getOrderedCreatedProjectCapexs(project))
          .thenReturn(List.of(firstCreatedCapex, secondCreatedCapex, lastCreatedCapex));

      var summarySectionOptional = projectStageSummaryService.getSummarySection(project, USER);
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
                  ReverseRouter.route(on(ProjectCapexController.class)
                      .getRemoveProjectCapex(
                          project.getField().getResource().getId(),
                          project.getId(),
                          firstCreatedCapex.getId()))),
              Map.of(
                  "Remove",
                  ReverseRouter.route(on(ProjectCapexController.class)
                      .getRemoveProjectCapex(
                          project.getField().getResource().getId(),
                          project.getId(),
                          secondCreatedCapex.getId()))
              ),
              Map.of(
                  "Remove",
                  ReverseRouter.route(on(ProjectCapexController.class)
                      .getRemoveProjectCapex(
                          project.getField().getResource().getId(),
                          project.getId(),
                          lastCreatedCapex.getId()))
              ));
    }

    private List<SummaryAction> getProjectAnalystActions() {
      return List.of(
          new SummaryAction(
              "Add",
              ReverseRouter.route(on(ProjectCapexController.class)
                  .getAddProjectCapex(project.getField().getResource().getId(), project.getId())),
              ""
          )
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
      var expectedActions = List.of();
      assertThat(getSectionSummaryCardWhenStages().summaryActions())
          .usingRecursiveComparison()
          .isEqualTo(expectedActions);
    }

    @Test
    void whenGetSummarySection_withNoStages_thenEmptyActionMap() {
      var expectedActions = List.of();
      assertThat(getSectionSummaryCardWhenNoStages().summaryActions())
          .usingRecursiveComparison()
          .isEqualTo(expectedActions);
    }

    @Test
    void whenGetSummarySection_withStages_thenPopulatedTimelineActionMap() {
      var firstCreatedCapex = new ProjectCapex(UUID.randomUUID());
      firstCreatedCapex.setProject(project);
      firstCreatedCapex.setSourceOfInformation(ProjectCapexSource.DRAFT_CSR);
      firstCreatedCapex.setDateProvided(FIXED_DATE.plusDays(1));
      firstCreatedCapex.setTotalCapex(BigDecimal.valueOf(24));

      var secondCreatedCapex = new ProjectCapex(UUID.randomUUID());
      secondCreatedCapex.setProject(project);
      secondCreatedCapex.setSourceOfInformation(ProjectCapexSource.APPROVED_CSR);
      secondCreatedCapex.setDateProvided(FIXED_DATE.plusMonths(1));
      secondCreatedCapex.setTotalCapex(BigDecimal.valueOf(24));

      var lastCreatedCapex = new ProjectCapex(UUID.randomUUID());
      lastCreatedCapex.setProject(project);
      lastCreatedCapex.setSourceOfInformation(ProjectCapexSource.APPROVED_CSR);
      lastCreatedCapex.setDateProvided(FIXED_DATE.plusYears(1));
      lastCreatedCapex.setTotalCapex(BigDecimal.valueOf(24));

      when(projectStageService.getOrderedCreatedProjectCapexs(project))
          .thenReturn(List.of(firstCreatedCapex, secondCreatedCapex, lastCreatedCapex));

      var summarySectionOptional = projectStageSummaryService.getSummarySection(project, USER);
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
    when(teamQueryService.userHasStaticRole(projectAnalystUser.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
        .thenReturn(isProjectAnalyst);
  }

  private SummaryCard getSectionSummaryCardWhenStages() {
    var projectCapex = new ProjectCapex(UUID.randomUUID());
    projectCapex.setSourceOfInformation(ProjectCapexSource.UKSS);
    projectCapex.setDateProvided(LocalDate.now());
    projectCapex.setTotalCapex(BigDecimal.valueOf(24));
    projectCapex.setProject(project);
    when(projectStageService.getOrderedCreatedProjectCapexs(project))
        .thenReturn(List.of(projectCapex));

    var summarySectionOptional = projectStageSummaryService.getSummarySection(project, USER);

    var summaryItems = summarySectionOptional.get().summaryItems();

    var summaryItem = summaryItems.get(0);

    return summaryItem.summaryCards().get(0);
  }

  private SummaryCard getSectionSummaryCardWhenNoStages() {
    var summarySectionOptional = projectStageSummaryService.getSummarySection(project, USER);

    var summaryItems = summarySectionOptional.get().summaryItems();

    var summaryItem = summaryItems.get(0);

    return summaryItem.summaryCards().get(0);
  }
}