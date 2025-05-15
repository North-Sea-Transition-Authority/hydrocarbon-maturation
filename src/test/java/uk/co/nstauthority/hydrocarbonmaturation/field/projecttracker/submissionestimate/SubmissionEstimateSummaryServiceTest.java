package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class SubmissionEstimateSummaryServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionEstimateService submissionEstimateService;
  @Mock
  private TeamQueryService teamQueryService;
  @InjectMocks
  SubmissionEstimateSummaryService submissionEstimateSummaryService;

  private final LocalDate FIXED_DATE = LocalDate.of(2023, 3, 19);

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(8000), UUID.randomUUID());
  }

  @Nested
  class GivenFdpProject {

    private List<SubmissionEstimate> csrEstimates;
    private List<SubmissionEstimate> fdpEstimates;
    private List<SubmissionEstimate> decisionDateEstimates;
    private List<SubmissionEstimate> firstProductionEstimates;

    @BeforeEach
    void setUp() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);
    }

    @Nested
    class WhenNotAllEstimatesProvided {
      @Test
      void thenEmptySummaryCards() {
        var summarySection = submissionEstimateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertEmptySummaryCardOnSection(SubmissionEstimateType.CSR.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(SubmissionEstimateType.FDP.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(SubmissionEstimateType.DECISION_DATE.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(SubmissionEstimateType.FIRST_PRODUCTION.getDisplayName(), summarySection);
      }
    }

    @Nested
    class WhenAllEstimatesProvided {
      @BeforeEach
      void setUp() {
        csrEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.CSR);
        fdpEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.FDP);
        decisionDateEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.DECISION_DATE);
        firstProductionEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.FIRST_PRODUCTION);
      }

      @Test
      void whenAnyUser_andEstimatesAllProvided_assertPopulatedCards() {

        var summarySection = submissionEstimateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertSummaryCardOnSection(
            SubmissionEstimateType.CSR.getDisplayName(),
            csrEstimates,
            summarySection
        );
        assertSummaryCardOnSection(
            SubmissionEstimateType.FDP.getDisplayName(),
            fdpEstimates,
            summarySection
        );
        assertSummaryCardOnSection(
            SubmissionEstimateType.DECISION_DATE.getDisplayName(),
            decisionDateEstimates,
            summarySection
        );
        assertSummaryCardOnSection(
            SubmissionEstimateType.FIRST_PRODUCTION.getDisplayName(),
            firstProductionEstimates,
            summarySection
        );
      }

      @Test
      void whenProjectAnalyst_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.get(0);
        assertSummaryCardHasActions(csrCard, SubmissionEstimateType.CSR);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveActions(csrTableRows, csrEstimates);

        var fdpCard = summaryCards.get(1);
        assertSummaryCardHasActions(fdpCard, SubmissionEstimateType.FDP);
        var fdpTableView = (SummaryTableView) fdpCard.summaryData();
        var fdpTableRows = fdpTableView.tableRows();
        assertSummaryTableRowsHaveActions(fdpTableRows, fdpEstimates);

        var decisionDateCard = summaryCards.get(2);
        assertSummaryCardHasActions(decisionDateCard, SubmissionEstimateType.DECISION_DATE);
        var decisionDateTableView = (SummaryTableView) decisionDateCard.summaryData();
        var decisionDateTableRows = decisionDateTableView.tableRows();
        assertSummaryTableRowsHaveActions(decisionDateTableRows, decisionDateEstimates);

        var firstProductionCard = summaryCards.get(3);
        assertSummaryCardHasActions(firstProductionCard, SubmissionEstimateType.FIRST_PRODUCTION);
        var firstProductionTableView = (SummaryTableView) firstProductionCard.summaryData();
        var firstProductionTableRows = firstProductionTableView.tableRows();
        assertSummaryTableRowsHaveActions(firstProductionTableRows, firstProductionEstimates);
      }

      @Test
      void whenCannotManageProject_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(
                USER.wuaId(),
                TeamType.REGULATOR,
                Role.PROJECT_ANALYST)
        ).thenReturn(false);
        

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.get(0);
        assertSummaryCardHasNoActions(csrCard);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(csrTableRows);

        var fdpCard = summaryCards.get(1);
        assertSummaryCardHasNoActions(fdpCard);
        var fdpTableView = (SummaryTableView) fdpCard.summaryData();
        var fdpTableRows = fdpTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(fdpTableRows);

        var decisionDateCard = summaryCards.get(2);
        assertSummaryCardHasNoActions(decisionDateCard);
        var decisionDateTableView = (SummaryTableView) decisionDateCard.summaryData();
        var decisionDateTableRows = decisionDateTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(decisionDateTableRows);
        
        var firstProductionCard = summaryCards.get(3);
        assertSummaryCardHasNoActions(firstProductionCard);
        var firstProductionTableView = (SummaryTableView) firstProductionCard.summaryData();
        var firstProductionTableRows = firstProductionTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(firstProductionTableRows);
      }
    }
  }

  @Nested
  class GivenFdpaProject {

    private List<SubmissionEstimate> csrEstimates;
    private List<SubmissionEstimate> fdpaEstimates;
    private List<SubmissionEstimate> fdpaStartUpEstimates;
    private List<SubmissionEstimate> decisionDateEstimates;

    @BeforeEach
    void setUp() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);
    }

    @Nested
    class WhenNotAllEstimatesProvided {
      @Test
      void thenEmptySummaryCards() {
        var summarySection = submissionEstimateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertEmptySummaryCardOnSection(SubmissionEstimateType.CSR.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(SubmissionEstimateType.FDPA.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(SubmissionEstimateType.FDPA_START_UP.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(SubmissionEstimateType.DECISION_DATE.getDisplayName(), summarySection);
      }
    }

    @Nested
    class WhenAllEstimatesProvided {
      @BeforeEach
      void setUp() {
        csrEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.CSR);
        fdpaEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.FDPA);
        fdpaStartUpEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.FDPA_START_UP);
        decisionDateEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.DECISION_DATE);
      }

      @Test
      void whenAnyUser_andEstimatesAllProvided_assertPopulatedCards() {

        var summarySection = submissionEstimateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertSummaryCardOnSection(
            SubmissionEstimateType.CSR.getDisplayName(),
            csrEstimates,
            summarySection
        );
        assertSummaryCardOnSection(
            SubmissionEstimateType.FDPA.getDisplayName(),
            fdpaEstimates,
            summarySection
        );
        assertSummaryCardOnSection(
            SubmissionEstimateType.FDPA_START_UP.getDisplayName(),
            fdpaStartUpEstimates,
            summarySection
        );
        assertSummaryCardOnSection(
            SubmissionEstimateType.DECISION_DATE.getDisplayName(),
            decisionDateEstimates,
            summarySection
        );
      }

      @Test
      void whenProjectAnalyst_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(
                USER.wuaId(),
                TeamType.REGULATOR,
                Role.PROJECT_ANALYST)
        ).thenReturn(true);

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.get(0);
        assertSummaryCardHasActions(csrCard, SubmissionEstimateType.CSR);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveActions(csrTableRows, csrEstimates);

        var fdpaCard = summaryCards.get(1);
        assertSummaryCardHasActions(fdpaCard, SubmissionEstimateType.FDPA);
        var fdpaTableView = (SummaryTableView) fdpaCard.summaryData();
        var fdpaTableRows = fdpaTableView.tableRows();
        assertSummaryTableRowsHaveActions(fdpaTableRows, fdpaEstimates);

        var fdpaStartUpCard = summaryCards.get(2);
        assertSummaryCardHasActions(fdpaStartUpCard, SubmissionEstimateType.FDPA_START_UP);
        var fdpaStartUpTableView = (SummaryTableView) fdpaStartUpCard.summaryData();
        var fdpaStartUpTableRows = fdpaStartUpTableView.tableRows();
        assertSummaryTableRowsHaveActions(fdpaStartUpTableRows, fdpaStartUpEstimates);

        var decisionDateCard = summaryCards.get(3);
        assertSummaryCardHasActions(decisionDateCard, SubmissionEstimateType.DECISION_DATE);
        var decisionDateTableView = (SummaryTableView) decisionDateCard.summaryData();
        var decisionDateTableRows = decisionDateTableView.tableRows();
        assertSummaryTableRowsHaveActions(decisionDateTableRows, decisionDateEstimates);
      }

      @Test
      void whenCannotManageProject_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(
                USER.wuaId(),
                TeamType.REGULATOR,
                Role.PROJECT_ANALYST)
        ).thenReturn(false);
        

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.get(0);
        assertSummaryCardHasNoActions(csrCard);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(csrTableRows);

        var fdpaCard = summaryCards.get(0);
        assertSummaryCardHasNoActions(fdpaCard);
        var fdpaTableView = (SummaryTableView) fdpaCard.summaryData();
        var fdpaTableRows = fdpaTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(fdpaTableRows);

        var fdpaStartUpCard = summaryCards.get(0);
        assertSummaryCardHasNoActions(fdpaStartUpCard);
        var fdpaStartUpTableView = (SummaryTableView) fdpaStartUpCard.summaryData();
        var fdpaStartUpTableRows = fdpaStartUpTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(fdpaStartUpTableRows);

        var decisionDateCard = summaryCards.get(0);
        assertSummaryCardHasNoActions(decisionDateCard);
        var decisionDateTableView = (SummaryTableView) decisionDateCard.summaryData();
        var decisionDateTableRows = decisionDateTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(decisionDateTableRows);
      }
    }
  }

  @Nested
  class GivenCopProject {

    private List<SubmissionEstimate> copEstimates;

    @BeforeEach
    void setUp() {
      project.setProjectType(ProjectType.CESSATION_OF_PRODUCTION);
    }

    @Nested
    class WhenNotAllEstimatesProvided {
      @Test
      void thenEmptySummaryCards() {
        var summarySection = submissionEstimateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertEmptySummaryCardOnSection(SubmissionEstimateType.COP.getDisplayName(), summarySection);
      }
    }

    @Nested
    class WhenAllEstimatesProvided {
      @BeforeEach
      void setUp() {
        copEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.COP);
      }

      @Test
      void whenAnyUser_andEstimatesAllProvided_assertPopulatedCards() {

        var summarySection = submissionEstimateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertSummaryCardOnSection(
            SubmissionEstimateType.COP.getDisplayName(),
            copEstimates,
            summarySection
        );
      }

      @Test
      void whenProjectAnalyst_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(
                USER.wuaId(),
                TeamType.REGULATOR,
                Role.PROJECT_ANALYST)
        ).thenReturn(true);

        var summaryCards = invokeAndGetSummaryCards();

        var copCard = summaryCards.get(0);
        assertSummaryCardHasActions(copCard, SubmissionEstimateType.COP);
        var copTableView = (SummaryTableView) copCard.summaryData();
        var copTableRows = copTableView.tableRows();
        assertSummaryTableRowsHaveActions(copTableRows, copEstimates);
      }

      @Test
      void whenCannotManageProject_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(
                USER.wuaId(),
                TeamType.REGULATOR,
                Role.PROJECT_ANALYST)
        ).thenReturn(false);

        var summaryCards = invokeAndGetSummaryCards();

        var copCard = summaryCards.get(0);
        assertSummaryCardHasNoActions(copCard);
        var copTableView = (SummaryTableView) copCard.summaryData();
        var copTableRows = copTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(copTableRows);
      }
    }
  }

  private List<SubmissionEstimate> createAndMockSubmissionEstimates(SubmissionEstimateType type) {
    var firstCreatedEstimates = new SubmissionEstimate(UUID.randomUUID());
    firstCreatedEstimates.setSubmissionEstimateType(type);
    firstCreatedEstimates.setDateWhenEstimated(getRandomDate());
    firstCreatedEstimates.setEarliestDate(getRandomDate());
    firstCreatedEstimates.setMostLikelyDate(getRandomDate());
    firstCreatedEstimates.setLatestDate(getRandomDate());

    var secondCreatedEstimates = new SubmissionEstimate(UUID.randomUUID());
    secondCreatedEstimates.setSubmissionEstimateType(type);
    secondCreatedEstimates.setDateWhenEstimated(getRandomDate());
    secondCreatedEstimates.setEarliestDate(getRandomDate());
    secondCreatedEstimates.setMostLikelyDate(getRandomDate());
    secondCreatedEstimates.setLatestDate(getRandomDate());

    var lastCreatedEstimates = new SubmissionEstimate(UUID.randomUUID());
    lastCreatedEstimates.setSubmissionEstimateType(type);
    lastCreatedEstimates.setDateWhenEstimated(getRandomDate());
    lastCreatedEstimates.setEarliestDate(getRandomDate());
    lastCreatedEstimates.setMostLikelyDate(getRandomDate());
    lastCreatedEstimates.setLatestDate(getRandomDate());

    var createdEstimates = Stream.of(firstCreatedEstimates, secondCreatedEstimates, lastCreatedEstimates)
        .sorted(Comparator.comparing(SubmissionEstimate::getMostLikelyDate))
        .toList();

    when(submissionEstimateService.getCreatedEstimates(project, type))
        .thenReturn(createdEstimates);

    return createdEstimates;
  }

  private LocalDate getRandomDate() {
    return LocalDate.ofEpochDay(ThreadLocalRandom
        .current()
        .nextLong(FIXED_DATE.toEpochDay(), FIXED_DATE.plusYears(5).toEpochDay()));
  }

  private void assertSummaryCardOnSection(String cardTitle, List<SubmissionEstimate> estimates, SummarySection summarySection) {
    var summaryItems = summarySection.summaryItems();
    var summaryCards = summaryItems.get(0).summaryCards();

    var expectedRows = new ArrayList<>(Collections.singletonList(
        SummaryTableRow.builder().withValues(
                SubmissionEstimateSummaryService.ESTIMATED_DATES_ON,
                SubmissionEstimateSummaryService.EARLIEST,
                SubmissionEstimateSummaryService.MOST_LIKELY,
                SubmissionEstimateSummaryService.LATEST)
            .build())
    );

    expectedRows.addAll(estimates.stream()
        .map(estimate -> SummaryTableRow.builder()
            .withValues(
                DateUtil.formatLongDate(estimate.getDateWhenEstimated()),
                DateUtil.formatLongDate(estimate.getEarliestDate()),
                DateUtil.formatLongDate(estimate.getMostLikelyDate()),
                DateUtil.formatLongDate(estimate.getLatestDate()))
            .build())
        .toList());

    var expectedSummaryData = new SummaryTableView(expectedRows);

    assertThat(summaryCards)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .contains(
            Tuple.tuple(cardTitle, SummaryCardType.TABLE_SUMMARY, expectedSummaryData)
        );
  }

  private void assertEmptySummaryCardOnSection(String cardTitle, SummarySection summarySection) {
    var summaryItems = summarySection.summaryItems();
    var summaryCards = summaryItems.get(0).summaryCards();

    assertThat(summaryCards)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        )
        .contains(
            Tuple.tuple(cardTitle, SummaryCardType.EMPTY_SUMMARY)
        );
  }

  private void assertSummaryCardHasActions(SummaryCard summaryCard, SubmissionEstimateType type) {
    assertThat(summaryCard.summaryActions())
        .isEqualTo(
            List.of(
                new SummaryAction(
                    "Add",
                    ReverseRouter.route(on(SubmissionEstimateController.class)
                        .getCreateSubmissionEstimate(project.getField().getResource().getId(), project.getId(), String.valueOf(type))),
                    ""
                )
            )
        );
  }

  private void assertSummaryCardHasNoActions(SummaryCard summaryCard) {
    assertThat(summaryCard.summaryActions())
        .isEmpty();
  }

  private void assertSummaryTableRowsHaveActions(List<SummaryTableRow> tableRows, List<SubmissionEstimate> estimates) {
    // check heading has actions heading and no action links
    assertThat(tableRows.get(0).rowValues())
        .usingRecursiveComparison()
        .isEqualTo(List.of(
            new SummaryTableValue(SubmissionEstimateSummaryService.ESTIMATED_DATES_ON),
            new SummaryTableValue(SubmissionEstimateSummaryService.EARLIEST),
            new SummaryTableValue(SubmissionEstimateSummaryService.MOST_LIKELY),
            new SummaryTableValue(SubmissionEstimateSummaryService.LATEST),
            new SummaryTableValue(SubmissionEstimateSummaryService.ACTIONS)
        ));

    assertThat(tableRows.get(0).rowActions())
        .isEmpty();

    // check each table data row has an action to remove
    var expectedRowActions = estimates.stream()
        .map(submissionEstimate -> List.of(new SummaryAction(
            "Remove",
            ReverseRouter.route(on(SubmissionEstimateController.class)
                .getRemoveConfirmation(
                    project.getField().getResource().getId(),
                    project.getId(),
                    submissionEstimate.getId(),
                    String.valueOf(submissionEstimate.getSubmissionEstimateType()))),
            "")))
        .toList();

    tableRows.remove(0);
    assertThat(tableRows)
        .extracting(SummaryTableRow::rowActions)
        .usingRecursiveComparison()
        .isEqualTo(expectedRowActions);
  }

  private void assertSummaryTableRowsHaveNoActions(List<SummaryTableRow> tableRows) {
    assertThat(tableRows)
        .extracting(SummaryTableRow::rowActions)
        .containsOnly(List.of());
  }

  private List<SummaryCard> invokeAndGetSummaryCards() {
    var summarySection = submissionEstimateSummaryService.getSummarySection(project, USER).orElseThrow();
    var summaryItems = summarySection.summaryItems();
    return summaryItems.get(0).summaryCards();
  }
}