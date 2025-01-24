package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

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
class ActualProjectDateSummaryServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private ActualProjectDateService actualProjectDateService;
  @Mock
  private TeamQueryService teamQueryService;
  @InjectMocks
  ActualProjectDateSummaryService actualProjectDateSummaryService;

  private static final LocalDate FIXED_DATE = LocalDate.of(2023, 3, 19);

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(8000), UUID.randomUUID());
  }

  @Nested
  class GivenFdpOrFdpaProject {

    private List<ActualProjectDate> csrDates;
    private List<ActualProjectDate> fdpDates;

    @BeforeEach
    void setUp() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);
    }

    @Nested
    class WhenNotAllDatesProvided {
      @Test
      void thenEmptySummaryCards() {
        var summarySection = actualProjectDateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertEmptySummaryCardOnSection(ActualProjectDateType.CSR_FIRST_DRAFT.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(ActualProjectDateType.FDP_FIRST_DRAFT.getDisplayName(), summarySection);
      }
    }

    @Nested
    class WhenAllDatesProvided {
      @BeforeEach
      void setUp() {
        csrDates = createAndMockProjectDates(ActualProjectDateType.CSR_FIRST_DRAFT);
        fdpDates = createAndMockProjectDates(ActualProjectDateType.FDP_FIRST_DRAFT);
      }

      @Test
      void whenAnyUser_andDatesAllProvided_assertPopulatedCards() {

        var summarySection = actualProjectDateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertSummaryCardOnSection(
            ActualProjectDateType.CSR_FIRST_DRAFT.getDisplayName(),
            csrDates,
            summarySection
        );
        assertSummaryCardOnSection(
            ActualProjectDateType.FDP_FIRST_DRAFT.getDisplayName(),
            fdpDates,
            summarySection
        );
      }

      @Test
      void whenProjectAnalyst_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.getFirst();
        assertSummaryCardHasActions(csrCard, ActualProjectDateType.CSR_FIRST_DRAFT);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveActions(csrTableRows, csrDates);

        var fdpCard = summaryCards.get(1);
        assertSummaryCardHasActions(fdpCard, ActualProjectDateType.FDP_FIRST_DRAFT);
        var fdpTableView = (SummaryTableView) fdpCard.summaryData();
        var fdpTableRows = fdpTableView.tableRows();
        assertSummaryTableRowsHaveActions(fdpTableRows, fdpDates);
      }

      @Test
      void whenCannotManageProject_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(
                USER.wuaId(),
                TeamType.REGULATOR,
                Role.PROJECT_ANALYST)
        ).thenReturn(false);

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.getFirst();
        assertSummaryCardHasNoActions(csrCard);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(csrTableRows);

        var fdpCard = summaryCards.get(1);
        assertSummaryCardHasNoActions(fdpCard);
        var fdpTableView = (SummaryTableView) fdpCard.summaryData();
        var fdpTableRows = fdpTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(fdpTableRows);
      }
    }
  }

  @Nested
  class GivenCopProject {

    private List<ActualProjectDate> copDates;
    private List<ActualProjectDate> noObjectionDates;
    private List<ActualProjectDate> confirmationReceivedDates;

    @BeforeEach
    void setUp() {
      project.setProjectType(ProjectType.CESSATION_OF_PRODUCTION);
    }

    @Nested
    class WhenNotAllDatesProvided {
      @Test
      void thenEmptySummaryCards() {
        var summarySection = actualProjectDateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertEmptySummaryCardOnSection(ActualProjectDateType.COP_FIRST_SUBMISSION.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(ActualProjectDateType.COP_NO_OBJECTION_LETTER_SENT.getDisplayName(), summarySection);
        assertEmptySummaryCardOnSection(ActualProjectDateType.COP_CONFIRMATION_RECEIVED.getDisplayName(), summarySection);
      }
    }

    @Nested
    class WhenAllDatesProvided {
      @BeforeEach
      void setUp() {
        copDates = createAndMockProjectDates(ActualProjectDateType.COP_FIRST_SUBMISSION);
        noObjectionDates = createAndMockProjectDates(ActualProjectDateType.COP_NO_OBJECTION_LETTER_SENT);
        confirmationReceivedDates = createAndMockProjectDates(ActualProjectDateType.COP_CONFIRMATION_RECEIVED);
      }

      @Test
      void whenAnyUser_andDatesAllProvided_assertPopulatedCards() {

        var summarySection = actualProjectDateSummaryService.getSummarySection(project, USER).orElseThrow();

        assertSummaryCardOnSection(
            ActualProjectDateType.COP_FIRST_SUBMISSION.getDisplayName(),
            copDates,
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

        var copCard = summaryCards.getFirst();
        assertSummaryCardHasActions(copCard, ActualProjectDateType.COP_FIRST_SUBMISSION);
        var copTableView = (SummaryTableView) copCard.summaryData();
        var copTableRows = copTableView.tableRows();
        assertSummaryTableRowsHaveActions(copTableRows, copDates);

        var noObjectionCard = summaryCards.get(1);
        assertSummaryCardHasActions(noObjectionCard, ActualProjectDateType.COP_NO_OBJECTION_LETTER_SENT);
        var noObjectionTableView = (SummaryTableView) noObjectionCard.summaryData();
        var noObjectionTableRows = noObjectionTableView.tableRows();
        assertSummaryTableRowsHaveActions(noObjectionTableRows, noObjectionDates);

        var confirmationCard = summaryCards.get(2);
        assertSummaryCardHasActions(confirmationCard, ActualProjectDateType.COP_CONFIRMATION_RECEIVED);
        var confirmationTableView = (SummaryTableView) confirmationCard.summaryData();
        var confirmationTableRows = confirmationTableView.tableRows();
        assertSummaryTableRowsHaveActions(confirmationTableRows, confirmationReceivedDates);
      }

      @Test
      void whenCannotManageProject_assertActionsAndHeader() {
        when(teamQueryService.userHasStaticRole(
                USER.wuaId(),
                TeamType.REGULATOR,
                Role.PROJECT_ANALYST)
        ).thenReturn(false);

        var summaryCards = invokeAndGetSummaryCards();

        var copCard = summaryCards.getFirst();
        assertSummaryCardHasNoActions(copCard);
        var copTableView = (SummaryTableView) copCard.summaryData();
        var copTableRows = copTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(copTableRows);

        var noObjectionCard = summaryCards.get(1);
        assertSummaryCardHasNoActions(noObjectionCard);
        var noObjectionTableView = (SummaryTableView) noObjectionCard.summaryData();
        var noObjectionTableRows = noObjectionTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(noObjectionTableRows);

        var confirmationCard = summaryCards.get(2);
        assertSummaryCardHasNoActions(confirmationCard);
        var confirmationTableView = (SummaryTableView) confirmationCard.summaryData();
        var confirmationTableRows = confirmationTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(confirmationTableRows);
      }
    }
  }

  private List<ActualProjectDate> createAndMockProjectDates(ActualProjectDateType type) {
    var firstDate = new ActualProjectDate(UUID.randomUUID());
    firstDate.setProjectDateType(type);
    firstDate.setDate(getRandomDate());

    var secondDate = new ActualProjectDate(UUID.randomUUID());
    secondDate.setProjectDateType(type);
    secondDate.setDate(getRandomDate());

    var lastDate = new ActualProjectDate(UUID.randomUUID());
    lastDate.setProjectDateType(type);
    lastDate.setDate(getRandomDate());

    var dates = Stream.of(firstDate, secondDate, lastDate)
        .sorted(Comparator.comparing(ActualProjectDate::getDate))
        .toList();

    when(actualProjectDateService.getDates(project, type))
        .thenReturn(dates);

    return dates;
  }

  private LocalDate getRandomDate() {
    return LocalDate.ofEpochDay(ThreadLocalRandom
        .current()
        .nextLong(FIXED_DATE.toEpochDay(), FIXED_DATE.plusYears(5).toEpochDay()));
  }

  private void assertSummaryCardOnSection(String cardTitle, List<ActualProjectDate> actualProjectDates, SummarySection summarySection) {
    var summaryItems = summarySection.summaryItems();
    var summaryCards = summaryItems.getFirst().summaryCards();

    var expectedRows = new ArrayList<>(Collections.singletonList(
        SummaryTableRow.builder().withValues(ActualProjectDateSummaryService.DATE).build())
    );

    expectedRows.addAll(actualProjectDates.stream()
        .map(estimate -> SummaryTableRow.builder()
            .withValues(DateUtil.formatLongDate(estimate.getDate())).build())
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
    var summaryCards = summaryItems.getFirst().summaryCards();

    assertThat(summaryCards)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        )
        .contains(
            Tuple.tuple(cardTitle, SummaryCardType.EMPTY_SUMMARY)
        );
  }

  private void assertSummaryCardHasActions(SummaryCard summaryCard, ActualProjectDateType type) {
    assertThat(summaryCard.summaryActions())
        .isEqualTo(
            List.of(
                new SummaryAction(
                    "Add",
                    ReverseRouter.route(on(ActualProjectDateController.class)
                        .getCreateProjectDate(project.getField().getResource().getId(), project.getId(), String.valueOf(type))),
                    ""
                )
            )
        );
  }

  private void assertSummaryCardHasNoActions(SummaryCard summaryCard) {
    assertThat(summaryCard.summaryActions())
        .isEmpty();
  }

  private void assertSummaryTableRowsHaveActions(List<SummaryTableRow> tableRows, List<ActualProjectDate> actualProjectDates) {
    // check heading has actions heading and no action links
    assertThat(tableRows.getFirst().rowValues())
        .usingRecursiveComparison()
        .isEqualTo(List.of(
            new SummaryTableValue(ActualProjectDateSummaryService.DATE),
            new SummaryTableValue(ActualProjectDateSummaryService.ACTIONS)
        ));

    assertThat(tableRows.getFirst().rowActions())
        .isEmpty();

    // check each table data row has an action to remove
    var expectedRowActions = actualProjectDates.stream()
        .map(projectDate -> List.of(new SummaryAction(
            "Remove",
            ReverseRouter.route(on(ActualProjectDateController.class)
                .getRemoveConfirmation(
                    project.getField().getResource().getId(),
                    project.getId(),
                    projectDate.getId(),
                    String.valueOf(projectDate.getProjectDateType()))),
            "")))
        .toList();

    tableRows.removeFirst();
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
    var summarySection = actualProjectDateSummaryService.getSummarySection(project, USER).orElseThrow();
    var summaryItems = summarySection.summaryItems();
    return summaryItems.getFirst().summaryCards();
  }
}