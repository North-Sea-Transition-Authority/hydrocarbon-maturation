package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class SubmissionEstimateSummaryServiceTest {
  @Mock
  private SubmissionEstimateService submissionEstimateService;
  @Mock
  private UserService userService;
  @Mock
  private PermissionService permissionService;
  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @InjectMocks
  SubmissionEstimateSummaryService submissionEstimateSummaryService;

  private final LocalDate FIXED_DATE = LocalDate.of(2023, 3, 19);

  private Project project;
  private User user;
  private Set<RolePermission> responsibleRegPermissions;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(8000), UUID.randomUUID());

    user = UserTestUtil.Builder().build();
    when(userService.getUser()).thenReturn(user);

    mockGetRegManagedTeamPermissions();
  }

  @Nested
  class GivenFdpProject {

    private List<SubmissionEstimate> csrEstimates;
    private List<SubmissionEstimate> fdpEstimates;

    @BeforeEach
    void setUp() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);
    }

    @Nested
    class WhenNotAllEstimatesProvided {
      @Test
      void thenEmptySummaryCards() {
        var summarySection = submissionEstimateSummaryService.getSummarySection(project).orElseThrow();

        assertEmptySummaryCardOnSection(SubmissionEstimateSummaryService.CSR_ESTIMATES, summarySection);
        assertEmptySummaryCardOnSection(SubmissionEstimateSummaryService.FDP_ESTIMATES, summarySection);
      }
    }

    @Nested
    class WhenAllEstimatesProvided {
      @BeforeEach
      void setUp() {
        csrEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.CSR);
        fdpEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.FDP);
      }

      @Test
      void whenAnyUser_andEstimatesAllProvided_assertPopulatedCards() {

        var summarySection = submissionEstimateSummaryService.getSummarySection(project).orElseThrow();

        assertSummaryCardOnSection(
            SubmissionEstimateSummaryService.CSR_ESTIMATES,
            csrEstimates,
            summarySection
        );
        assertSummaryCardOnSection(
            SubmissionEstimateSummaryService.FDP_ESTIMATES,
            fdpEstimates,
            summarySection
        );
      }

      @Test
      void whenProjectAnalyst_assertActionsAndHeader() {
        when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
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
      }

      @Test
      void whenResponsibleRegulator_assertActionsAndHeader() {
        when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
            .thenReturn(false);
        when(permissionService.hasPermission(user, responsibleRegPermissions))
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
      }

      @Test
      void whenCannotManageProject_assertActionsAndHeader() {
        when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
            .thenReturn(false);
        when(permissionService.hasPermission(user, responsibleRegPermissions))
            .thenReturn(false);

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
        assertSummaryTableRowsHaveActions(fdpTableRows, fdpEstimates);
      }
    }
  }

  @Nested
  class GivenFdpaProject {

    private List<SubmissionEstimate> csrEstimates;

    @BeforeEach
    void setUp() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);
    }

    @Nested
    class WhenNotAllEstimatesProvided {
      @Test
      void thenEmptySummaryCards() {
        var summarySection = submissionEstimateSummaryService.getSummarySection(project).orElseThrow();

        assertEmptySummaryCardOnSection(SubmissionEstimateSummaryService.CSR_ESTIMATES, summarySection);
      }
    }

    @Nested
    class WhenAllEstimatesProvided {
      @BeforeEach
      void setUp() {
        csrEstimates = createAndMockSubmissionEstimates(SubmissionEstimateType.CSR);
      }

      @Test
      void whenAnyUser_andEstimatesAllProvided_assertPopulatedCards() {

        var summarySection = submissionEstimateSummaryService.getSummarySection(project).orElseThrow();

        assertSummaryCardOnSection(
            SubmissionEstimateSummaryService.CSR_ESTIMATES,
            csrEstimates,
            summarySection
        );
      }

      @Test
      void whenProjectAnalyst_assertActionsAndHeader() {
        when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
            .thenReturn(true);

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.get(0);
        assertSummaryCardHasActions(csrCard, SubmissionEstimateType.CSR);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveActions(csrTableRows, csrEstimates);
      }

      @Test
      void whenResponsibleRegulator_assertActionsAndHeader() {
        when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
            .thenReturn(false);
        when(permissionService.hasPermission(user, responsibleRegPermissions))
            .thenReturn(true);

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.get(0);
        assertSummaryCardHasActions(csrCard, SubmissionEstimateType.CSR);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveActions(csrTableRows, csrEstimates);
      }

      @Test
      void whenCannotManageProject_assertActionsAndHeader() {
        when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
            .thenReturn(false);
        when(permissionService.hasPermission(user, responsibleRegPermissions))
            .thenReturn(false);

        var summaryCards = invokeAndGetSummaryCards();

        var csrCard = summaryCards.get(0);
        assertSummaryCardHasNoActions(csrCard);
        var csrTableView = (SummaryTableView) csrCard.summaryData();
        var csrTableRows = csrTableView.tableRows();
        assertSummaryTableRowsHaveNoActions(csrTableRows);
      }
    }
  }

  private List<SubmissionEstimate> createAndMockSubmissionEstimates(SubmissionEstimateType type) {
    var firstCreatedEstimates = new SubmissionEstimate(UUID.randomUUID());
    firstCreatedEstimates.setDateWhenEstimated(getRandomDate());
    firstCreatedEstimates.setEarliestDate(getRandomDate());
    firstCreatedEstimates.setMostLikelyDate(getRandomDate());
    firstCreatedEstimates.setLatestDate(getRandomDate());

    var secondCreatedEstimates = new SubmissionEstimate(UUID.randomUUID());
    secondCreatedEstimates.setDateWhenEstimated(getRandomDate());
    secondCreatedEstimates.setEarliestDate(getRandomDate());
    secondCreatedEstimates.setMostLikelyDate(getRandomDate());
    secondCreatedEstimates.setLatestDate(getRandomDate());

    var lastCreatedEstimates = new SubmissionEstimate(UUID.randomUUID());
    lastCreatedEstimates.setDateWhenEstimated(getRandomDate());
    lastCreatedEstimates.setEarliestDate(getRandomDate());
    lastCreatedEstimates.setMostLikelyDate(getRandomDate());
    lastCreatedEstimates.setLatestDate(getRandomDate());

    var createdEstimates = Stream.of(firstCreatedEstimates, secondCreatedEstimates, lastCreatedEstimates)
        .sorted(Comparator.comparing(SubmissionEstimate::getDateWhenEstimated))
        .toList();

    when(submissionEstimateService.getCreatedEstimates(project, type))
        .thenReturn(createdEstimates);

    return createdEstimates;
  }

  @SuppressWarnings("unchecked")
  private void mockGetRegManagedTeamPermissions() {
    var regulatorManagedInformation = mock(RegulatorManagedInformation.class);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(project.getField().getResource()))
        .thenReturn(regulatorManagedInformation);
    var responsibleTeamType = mock(ResponsibleTeamType.class);
    when(regulatorManagedInformation.getResponsibleTeamType())
        .thenReturn(responsibleTeamType);
    responsibleRegPermissions = mock(Set.class);
    when(responsibleTeamType.getRolePermissions())
        .thenReturn(responsibleRegPermissions);
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
            Tuple.tuple(cardTitle, SummaryCardType.EMPTY_SUMMARY_WITH_ACTIONS)
        );
  }

  private void assertSummaryCardHasActions(SummaryCard summaryCard, SubmissionEstimateType type) {
    assertThat(summaryCard.actionsMap())
        .isEqualTo(
            Map.of(
                "Add",
                ReverseRouter.route(on(SubmissionEstimateController.class)
                    .getCreateSubmissionEstimate(project.getField().getResource().getId(), project.getId(), String.valueOf(type)))
            )
        );
  }

  private void assertSummaryCardHasNoActions(SummaryCard summaryCard) {
    assertThat(summaryCard.actionsMap())
        .isEmpty();
  }

  private void assertSummaryTableRowsHaveActions(List<SummaryTableRow> tableRows, List<SubmissionEstimate> estimates) {
    List<SummaryTableRow.SummaryTableRowAction> expectedRowActions = estimates.stream()
        .map(submissionEstimate -> new SummaryTableRow.SummaryTableRowAction(
            "Remove",
            ReverseRouter.route(on(SubmissionEstimateController.class)
                .getRemoveConfirmation(
                    project.getField().getResource().getId(),
                    project.getId(),
                    submissionEstimate.getId(),
                    String.valueOf(submissionEstimate.getSubmissionEstimateType()))))).toList();
  }

  private void assertSummaryTableRowsHaveNoActions(List<SummaryTableRow> tableRows) {
    assertThat(tableRows)
        .extracting(SummaryTableRow::rowActions)
        .containsOnly(List.of());
  }

  private List<SummaryCard> invokeAndGetSummaryCards() {
    var summarySection = submissionEstimateSummaryService.getSummarySection(project).orElseThrow();
    var summaryItems = summarySection.summaryItems();
    return summaryItems.get(0).summaryCards();
  }
}