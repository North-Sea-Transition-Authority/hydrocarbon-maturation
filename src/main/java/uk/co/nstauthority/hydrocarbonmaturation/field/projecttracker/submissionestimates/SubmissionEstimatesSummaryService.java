package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

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
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Service
class SubmissionEstimatesSummaryService implements SummarySectionService<Project> {
  static final Set<ProjectType> PROJECT_TYPES_FOR_SECTION = Set.of(
      ProjectType.FIELD_DEVELOPMENT_PLAN,
      ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM
  );
  static final int DISPLAY_ORDER = 30;
  static final String CSR_ESTIMATES = "Estimated CSR first draft submission date";

  static final String ESTIMATED_DATES_ON = "Estimated dates on";
  static final String EARLIEST = "Earliest";
  static final String MOST_LIKELY = "Most likely";
  static final String LATEST = "Latest";
  static final String ACTIONS = "Actions";

  private final SubmissionEstimatesService submissionEstimatesService;
  private final UserService userService;
  private final PermissionService permissionService;

  @Autowired
  SubmissionEstimatesSummaryService(SubmissionEstimatesService submissionEstimatesService,
                                    UserService userService,
                                    PermissionService permissionService) {
    this.submissionEstimatesService = submissionEstimatesService;
    this.userService = userService;
    this.permissionService = permissionService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project) {
    if (!PROJECT_TYPES_FOR_SECTION.contains(project.getProjectType())) {
      return Optional.empty();
    }

    var summaryItems = List.of(SummaryItem.withCard(CSR_ESTIMATES, getCsrEstimatesSummaryCard(project)));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  SummaryCard getCsrEstimatesSummaryCard(Project project) {
    var user = userService.getUser();
    var isProjectAnalyst = permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST));

    var headings = getHeadingRow();
    if (isProjectAnalyst) {
      headings.add(ACTIONS);
    }
    var summaryTableView = SummaryTableView.newWithHeading(headings.toArray());

    submissionEstimatesService.getCreatedEstimates(project, SubmissionEstimatesType.CSR)
        .stream()
        .sorted(Comparator.comparing(SubmissionEstimates::getDateWhenEstimated))
        .forEach(
            submissionEstimates -> {
              var row = SummaryTableRow.builder()
                  .withValues(
                      submissionEstimates.getDateWhenEstimated(),
                      submissionEstimates.getEarliestDate(),
                      submissionEstimates.getMostLikelyDate(),
                      submissionEstimates.getLatestDate()
                  );

              if (isProjectAnalyst) {
                row.withAction("Remove", ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .removeEstimates(
                        project.getField().getResource().getId(),
                        project.getId(),
                        submissionEstimates.getId(),
                        null)));
              }
              summaryTableView.addRow(row.build());
            }
        );

    var cardActions = new HashMap<String, String>();
    if (isProjectAnalyst) {
      cardActions.put(
          "Add",
          ReverseRouter.route(on(SubmissionEstimatesController.class)
              .getCsrSubmissionEstimates(project.getField().getResource().getId(), project.getId()))
      );
    }

    if (summaryTableView.tableRows().size() == 1) {
      return SummaryCard.emptySummaryCardWithActions(CSR_ESTIMATES, cardActions);
    }
    return new SummaryCard(CSR_ESTIMATES, SummaryCardType.TABLE_SUMMARY, summaryTableView, cardActions);
  }

  private ArrayList<String> getHeadingRow() {
    return new ArrayList<>(List.of(ESTIMATED_DATES_ON, EARLIEST, MOST_LIKELY, LATEST));
  }
}
