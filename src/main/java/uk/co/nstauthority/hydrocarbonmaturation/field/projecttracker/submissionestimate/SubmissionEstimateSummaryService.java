package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Service
class SubmissionEstimateSummaryService implements SummarySectionService<Project> {
  static final int DISPLAY_ORDER = 30;
  static final String ESTIMATED_DATES = "Estimated dates";

  static final String ESTIMATED_DATES_ON = "Estimated dates on";
  static final String EARLIEST = "Earliest";
  static final String MOST_LIKELY = "Most likely";
  static final String LATEST = "Latest";
  static final String ACTIONS = "Actions";

  private final SubmissionEstimateService submissionEstimateService;
  private final UserService userService;
  private final PermissionService permissionService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Autowired
  SubmissionEstimateSummaryService(SubmissionEstimateService submissionEstimateService,
                                   UserService userService,
                                   PermissionService permissionService,
                                   RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService) {
    this.submissionEstimateService = submissionEstimateService;
    this.userService = userService;
    this.permissionService = permissionService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project) {
    List<SubmissionEstimateType> estimateTypesToCollect = getEstimateTypesForProjectType(project);

    var summaryCards = estimateTypesToCollect.stream()
        .map(estimateType -> getEstimateSummaryCard(project, estimateType))
        .toList();

    var summaryItems = List.of(SummaryItem.withCards(ESTIMATED_DATES, summaryCards));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  SummaryCard getEstimateSummaryCard(Project project, SubmissionEstimateType type) {
    var user = userService.getUser();
    var responsibleTeamPermissions = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(project.getField().getResource())
        .getResponsibleTeamType()
        .getRolePermissions();

    var canModifyDates = permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))
        || permissionService.hasPermission(user, responsibleTeamPermissions);

    var headings = getHeadingRow();
    if (canModifyDates) {
      headings.add(ACTIONS);
    }
    var summaryTableView = SummaryTableView.newWithHeading(headings.toArray());

    submissionEstimateService.getCreatedEstimates(project, type)
        .stream()
        .sorted(Comparator.comparing(SubmissionEstimate::getDateWhenEstimated))
        .forEach(
            submissionEstimate -> {
              var row = SummaryTableRow.builder()
                  .withValues(
                      submissionEstimate.getDateWhenEstimated(),
                      submissionEstimate.getEarliestDate(),
                      submissionEstimate.getMostLikelyDate(),
                      submissionEstimate.getLatestDate()
                  );

              if (canModifyDates) {
                row.withAction("Remove", ReverseRouter.route(on(SubmissionEstimateController.class)
                    .getRemoveConfirmation(
                        project.getField().getResource().getId(),
                        project.getId(),
                        submissionEstimate.getId(),
                        String.valueOf(submissionEstimate.getSubmissionEstimateType()))));
              }
              summaryTableView.addRow(row.build());
            }
        );

    var cardActions = new HashMap<String, String>();
    if (canModifyDates) {
      cardActions.put(
          "Add",
          ReverseRouter.route(on(SubmissionEstimateController.class)
              .getCreateSubmissionEstimate(project.getField().getResource().getId(), project.getId(), String.valueOf(type)))
      );
    }

    var cardTitle = type.getDisplayName();
    if (summaryTableView.tableRows().size() == 1) {
      return SummaryCard.emptySummaryCardWithActions(cardTitle, cardActions);
    }
    return new SummaryCard(cardTitle, SummaryCardType.TABLE_SUMMARY, summaryTableView, cardActions);
  }

  private ArrayList<String> getHeadingRow() {
    return new ArrayList<>(List.of(ESTIMATED_DATES_ON, EARLIEST, MOST_LIKELY, LATEST));
  }

  private List<SubmissionEstimateType> getEstimateTypesForProjectType(Project project) {
    return switch (project.getProjectType()) {
      case FIELD_DEVELOPMENT_PLAN ->
          List.of(SubmissionEstimateType.CSR, SubmissionEstimateType.FDP, SubmissionEstimateType.FIRST_PRODUCTION);
      case FIELD_DEVELOPMENT_PLAN_ADDENDUM ->
          List.of(SubmissionEstimateType.CSR, SubmissionEstimateType.FDPA, SubmissionEstimateType.FDPA_START_UP);
      case CESSATION_OF_PRODUCTION ->
          List.of(SubmissionEstimateType.COP);
    };
  }
}
