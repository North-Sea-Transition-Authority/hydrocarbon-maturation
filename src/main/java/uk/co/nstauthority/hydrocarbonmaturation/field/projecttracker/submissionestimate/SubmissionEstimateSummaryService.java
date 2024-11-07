package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

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
  private final TeamQueryService teamQueryService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Autowired
  SubmissionEstimateSummaryService(SubmissionEstimateService submissionEstimateService,
                                   TeamQueryService teamQueryService,
                                   RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService) {
    this.submissionEstimateService = submissionEstimateService;
    this.teamQueryService = teamQueryService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project, User user) {
    List<SubmissionEstimateType> estimateTypesToCollect = getEstimateTypesForProjectType(project);

    var summaryCards = estimateTypesToCollect.stream()
        .map(estimateType -> getEstimateSummaryCard(project, estimateType, user))
        .toList();

    var summaryItems = List.of(SummaryItem.withCards(ESTIMATED_DATES, summaryCards));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  SummaryCard getEstimateSummaryCard(Project project, SubmissionEstimateType type, User user) {
    var responsibleTeamType = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(project.getField().getResource())
        .getResponsibleTeamType();
    var teamType = responsibleTeamType.getTeamType();
    var role = responsibleTeamType.getRole();

    var canModifyDates = teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)
        || teamQueryService.userHasStaticRole(user.wuaId(), teamType, role);

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

    var cardActions = new ArrayList<SummaryAction>();
    if (canModifyDates) {
      cardActions.add(
          new SummaryAction(
              "Add",
              ReverseRouter.route(on(SubmissionEstimateController.class)
                  .getCreateSubmissionEstimate(project.getField().getResource().getId(), project.getId(), String.valueOf(type))),
              ""
          ));
    }

    var cardTitle = type.getDisplayName();
    if (summaryTableView.tableRows().size() == 1) {
      return SummaryCard.emptySummaryCardWithActions(cardTitle, cardActions);
    }
    return new SummaryCard(cardTitle, SummaryCardType.TABLE_SUMMARY, summaryTableView, cardActions, List.of());
  }

  private ArrayList<String> getHeadingRow() {
    return new ArrayList<>(List.of(ESTIMATED_DATES_ON, EARLIEST, MOST_LIKELY, LATEST));
  }

  private List<SubmissionEstimateType> getEstimateTypesForProjectType(Project project) {
    return switch (project.getProjectType()) {
      case FIELD_DEVELOPMENT_PLAN ->
          List.of(SubmissionEstimateType.CSR, SubmissionEstimateType.FDP, SubmissionEstimateType.DECISION_DATE,
              SubmissionEstimateType.FIRST_PRODUCTION);
      case FIELD_DEVELOPMENT_PLAN_ADDENDUM ->
          List.of(SubmissionEstimateType.CSR, SubmissionEstimateType.FDPA, SubmissionEstimateType.FDPA_START_UP,
              SubmissionEstimateType.DECISION_DATE);
      case CESSATION_OF_PRODUCTION ->
          List.of(SubmissionEstimateType.COP);
    };
  }
}
