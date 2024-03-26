package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
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
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class SubmissionEstimatesSummaryServiceTest {
  @Mock
  private SubmissionEstimatesService submissionEstimatesService;
  @Mock
  private UserService userService;
  @Mock
  private PermissionService permissionService;
  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @InjectMocks
  SubmissionEstimatesSummaryService submissionEstimatesSummaryService;

  private final LocalDate FIXED_DATE = LocalDate.of(2023, 3, 19);

  private Project project;
  private SubmissionEstimates firstEstimates;
  private SubmissionEstimates secondEstimates;
  private SubmissionEstimates lastEstimates;
  private Set<RolePermission> responsibleRegPermissions;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(8000), UUID.randomUUID());
    project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);
  }

  @ParameterizedTest
  @EnumSource(
    value = ProjectType.class,
    names = {"FIELD_DEVELOPMENT_PLAN", "FIELD_DEVELOPMENT_PLAN_ADDENDUM"},
    mode = EnumSource.Mode.EXCLUDE
  )
  void getSummarySection_projectNotTypeForSection_thenEmpty(ProjectType projectType) {
    project.setProjectType(projectType);
    assertThat(submissionEstimatesSummaryService.getSummarySection(project))
        .isEmpty();
  }

  @Test
  void getSummarySection_cannotModifyDates_noEstimatesProvided_thenEmptyCard() {
    when(submissionEstimatesService.getCreatedEstimates(project, SubmissionEstimatesType.CSR))
        .thenReturn(List.of());

    mockGetRegulatorMangedTeamPermissions();
    var summarySection = submissionEstimatesSummaryService.getSummarySection(project).orElseThrow();

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems)
        .hasSize(1)
        .extracting(SummaryItem::displayName)
        .containsExactly(SubmissionEstimatesSummaryService.CSR_ESTIMATES);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        )
        .containsExactly(
            Tuple.tuple(
                SummaryCardType.EMPTY_SUMMARY_WITH_ACTIONS,
                SubmissionEstimatesSummaryService.CSR_ESTIMATES
            )
        );

    // check actions on card is empty
    assertThat(summaryCards.get(0).summaryData())
        .isEqualTo(Map.of());
  }

  @Test
  void getSummarySection_isProjectAnalyst_noEstimatesProvided_thenEmptyCard() {
    var user = mock(User.class);
    when(userService.getUser()).thenReturn(user);

    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))).thenReturn(true);

    when(submissionEstimatesService.getCreatedEstimates(project, SubmissionEstimatesType.CSR))
        .thenReturn(List.of());

    var summarySection = submissionEstimatesSummaryService.getSummarySection(project).orElseThrow();

    assertSummarySectionWithActions(summarySection);
  }

  @Test
  @SuppressWarnings("unchecked")
  void getSummarySection_isResponsibleRegulatorForField_noEstimatesProvided_thenEmptyCard() {
    var user = mock(User.class);
    when(userService.getUser()).thenReturn(user);
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))).thenReturn(false);

    mockGetRegulatorMangedTeamPermissions();
    when(permissionService.hasPermission(user, responsibleRegPermissions)).thenReturn(true);

    when(submissionEstimatesService.getCreatedEstimates(project, SubmissionEstimatesType.CSR))
        .thenReturn(List.of());

    var summarySection = submissionEstimatesSummaryService.getSummarySection(project).orElseThrow();

    assertSummarySectionWithActions(summarySection);
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectType.class,
      names = {"FIELD_DEVELOPMENT_PLAN", "FIELD_DEVELOPMENT_PLAN_ADDENDUM"},
      mode = EnumSource.Mode.INCLUDE
  )
  void getSummarySection_projectTypeForSection_assertCommon(ProjectType projectType) {
    project.setProjectType(projectType);
    createAndMockSubmissionEstimates();
    mockGetRegulatorMangedTeamPermissions();

    var summarySection = submissionEstimatesSummaryService.getSummarySection(project).orElseThrow();
    assertThat(summarySection)
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        )
        .containsExactly(
            SubmissionEstimatesSummaryService.DISPLAY_ORDER,
            ""
        );

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems)
        .hasSize(1)
        .extracting(SummaryItem::displayName)
        .containsExactly(SubmissionEstimatesSummaryService.CSR_ESTIMATES);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        )
        .containsExactly(
            Tuple.tuple(
                SummaryCardType.TABLE_SUMMARY,
                SubmissionEstimatesSummaryService.CSR_ESTIMATES
            )
        );

    SummaryTableView summaryTable = (SummaryTableView) summaryCards.get(0).summaryData();
    assertThat(summaryTable.tableRows())
        .extracting(SummaryTableRow::rowValues)
        .containsExactly(
            List.of(
                SubmissionEstimatesSummaryService.ESTIMATED_DATES_ON,
                SubmissionEstimatesSummaryService.EARLIEST,
                SubmissionEstimatesSummaryService.MOST_LIKELY,
                SubmissionEstimatesSummaryService.LATEST
            ),
            List.of(
                DateUtil.formatLongDate(firstEstimates.getDateWhenEstimated()),
                DateUtil.formatLongDate(firstEstimates.getEarliestDate()),
                DateUtil.formatLongDate(firstEstimates.getMostLikelyDate()),
                DateUtil.formatLongDate(firstEstimates.getLatestDate())
            ),
            List.of(
                DateUtil.formatLongDate(secondEstimates.getDateWhenEstimated()),
                DateUtil.formatLongDate(secondEstimates.getEarliestDate()),
                DateUtil.formatLongDate(secondEstimates.getMostLikelyDate()),
                DateUtil.formatLongDate(secondEstimates.getLatestDate())
            ),
            List.of(
                DateUtil.formatLongDate(lastEstimates.getDateWhenEstimated()),
                DateUtil.formatLongDate(lastEstimates.getEarliestDate()),
                DateUtil.formatLongDate(lastEstimates.getMostLikelyDate()),
                DateUtil.formatLongDate(lastEstimates.getLatestDate())
            )
        );
  }

  @Test
  void givenIsProjectAnalyst_assertHeaderAndActions() {
    var user = mock(User.class);
    when(userService.getUser()).thenReturn(user);

    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);

    var summaryCard = invokeAndReturnSummaryCard();
    assertSummaryTableWithActions(summaryCard);
  }

  @Test
  void givenIsResponsibleRegulator_assertHeaderAndActions() {
    var user = mock(User.class);
    when(userService.getUser()).thenReturn(user);
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))).thenReturn(false);

    mockGetRegulatorMangedTeamPermissions();
    when(permissionService.hasPermission(user, responsibleRegPermissions))
        .thenReturn(true);

    var summaryCard = invokeAndReturnSummaryCard();
    assertSummaryTableWithActions(summaryCard);
  }

  @Test
  void givenIsNotProjectAnalyst_assertHeaderAndActions() {
    var user = mock(User.class);
    when(userService.getUser()).thenReturn(user);

    mockGetRegulatorMangedTeamPermissions();
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(false);

    var emptyActionsMap = Map.of();

    var summaryCard = invokeAndReturnSummaryCard();
    assertThat(summaryCard)
        .extracting(SummaryCard::actionsMap)
        .isEqualTo(emptyActionsMap);

    var summaryTable = (SummaryTableView) summaryCard.summaryData();
    assertThat(summaryTable.tableRows())
        .extracting(SummaryTableRow::rowValues)
        .contains(
            List.of(
                SubmissionEstimatesSummaryService.ESTIMATED_DATES_ON,
                SubmissionEstimatesSummaryService.EARLIEST,
                SubmissionEstimatesSummaryService.MOST_LIKELY,
                SubmissionEstimatesSummaryService.LATEST
            )
        );

    assertThat(summaryTable.tableRows())
        .extracting(SummaryTableRow::rowActions)
        .containsOnly(List.of());
  }

  private void createAndMockSubmissionEstimates() {
    firstEstimates = new SubmissionEstimates(UUID.randomUUID());
    firstEstimates.setDateWhenEstimated(FIXED_DATE);
    firstEstimates.setEarliestDate(FIXED_DATE);
    firstEstimates.setMostLikelyDate(FIXED_DATE);
    firstEstimates.setLatestDate(FIXED_DATE);

    secondEstimates = new SubmissionEstimates(UUID.randomUUID());
    secondEstimates.setDateWhenEstimated(FIXED_DATE);
    secondEstimates.setEarliestDate(FIXED_DATE);
    secondEstimates.setMostLikelyDate(FIXED_DATE);
    secondEstimates.setLatestDate(FIXED_DATE);

    lastEstimates = new SubmissionEstimates(UUID.randomUUID());
    lastEstimates.setDateWhenEstimated(FIXED_DATE);
    lastEstimates.setEarliestDate(FIXED_DATE);
    lastEstimates.setMostLikelyDate(FIXED_DATE);
    lastEstimates.setLatestDate(FIXED_DATE);

    when(submissionEstimatesService.getCreatedEstimates(project, SubmissionEstimatesType.CSR))
        .thenReturn(List.of(firstEstimates, secondEstimates, lastEstimates));
  }

  private SummaryCard invokeAndReturnSummaryCard() {
    createAndMockSubmissionEstimates();

    var summarySection = submissionEstimatesSummaryService.getSummarySection(project).orElseThrow();
    var summaryItems = summarySection.summaryItems();
    var summaryCards = summaryItems.get(0).summaryCards();

    return summaryCards.get(0);
  }

  @SuppressWarnings("unchecked")
  private void mockGetRegulatorMangedTeamPermissions() {
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
  private void assertSummarySectionWithActions(SummarySection summarySection) {
    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems)
        .hasSize(1)
        .extracting(SummaryItem::displayName)
        .containsExactly(SubmissionEstimatesSummaryService.CSR_ESTIMATES);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        )
        .containsExactly(
            Tuple.tuple(
                SummaryCardType.EMPTY_SUMMARY_WITH_ACTIONS,
                SubmissionEstimatesSummaryService.CSR_ESTIMATES
            )
        );

    // check actions on card has add
    assertThat(summaryCards.get(0).summaryData())
        .isEqualTo(Map.of(
            "Add",
            ReverseRouter.route(on(SubmissionEstimatesController.class)
                .getCsrSubmissionEstimates(project.getField().getResource().getId(), project.getId()))
        ));
  }

  private void assertSummaryTableWithActions(SummaryCard summaryCard) {
    var actionsMap = Map.of(
        "Add",
        ReverseRouter.route(on(SubmissionEstimatesController.class)
            .getCsrSubmissionEstimates(project.getField().getResource().getId(), project.getId()))
    );

    assertThat(summaryCard)
        .extracting(SummaryCard::actionsMap)
        .usingRecursiveComparison()
        .isEqualTo(actionsMap);

    var summaryTable = (SummaryTableView) summaryCard.summaryData();
    assertThat(summaryTable.tableRows())
        .extracting(SummaryTableRow::rowValues)
        .contains(
            List.of(
                SubmissionEstimatesSummaryService.ESTIMATED_DATES_ON,
                SubmissionEstimatesSummaryService.EARLIEST,
                SubmissionEstimatesSummaryService.MOST_LIKELY,
                SubmissionEstimatesSummaryService.LATEST,
                SubmissionEstimatesSummaryService.ACTIONS
            )
        );

    assertThat(summaryTable.tableRows())
        .extracting(SummaryTableRow::rowActions)
        .containsExactly(
            // first row is table header with no actions
            List.of(),

            List.of(
                new SummaryTableRow.SummaryTableRowAction(
                    "Remove",
                    ReverseRouter.route(on(SubmissionEstimatesController.class)
                        .getRemoveConfirmation(project.getField().getResource().getId(), project.getId(), firstEstimates.getId())))
            ),
            List.of(
                new SummaryTableRow.SummaryTableRowAction(
                    "Remove",
                    ReverseRouter.route(on(SubmissionEstimatesController.class)
                        .getRemoveConfirmation(project.getField().getResource().getId(), project.getId(), secondEstimates.getId())))
            ),
            List.of(
                new SummaryTableRow.SummaryTableRowAction(
                    "Remove",
                    ReverseRouter.route(on(SubmissionEstimatesController.class)
                        .getRemoveConfirmation(project.getField().getResource().getId(), project.getId(), lastEstimates.getId())))
            )
        );
  }
}