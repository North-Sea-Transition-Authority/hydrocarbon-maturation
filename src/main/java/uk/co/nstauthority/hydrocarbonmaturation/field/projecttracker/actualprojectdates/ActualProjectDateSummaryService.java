package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

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
class ActualProjectDateSummaryService implements SummarySectionService<Project> {
  static final int DISPLAY_ORDER = 31;

  static final String ACTUAL_DATES = "Actual dates";
  static final String DATE = "Date";
  static final String ACTIONS = "Actions";

  private final ActualProjectDateService actualProjectDateService;
  private final TeamQueryService teamQueryService;

  @Autowired
  ActualProjectDateSummaryService(
      ActualProjectDateService actualProjectDateService,
      TeamQueryService teamQueryService
  ) {
    this.actualProjectDateService = actualProjectDateService;
    this.teamQueryService = teamQueryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project, User user) {
    List<ActualProjectDateType> actualProjectDateTypesToCollect = getDateTypesForProjectType(project);

    var summaryCards = actualProjectDateTypesToCollect.stream()
        .map(dateType -> getProjectDateSummaryCard(project, dateType, user))
        .toList();

    var summaryItems = List.of(SummaryItem.withCards(ACTUAL_DATES, summaryCards));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  SummaryCard getProjectDateSummaryCard(Project project, ActualProjectDateType type, User user) {
    var canModifyDates = teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST);

    var headings = getHeadingRow();
    if (canModifyDates) {
      headings.add(ACTIONS);
    }
    var summaryTableView = SummaryTableView.newWithHeading(headings.toArray());

    actualProjectDateService.getDates(project, type)
        .stream()
        .sorted(Comparator.comparing(ActualProjectDate::getDate))
        .forEach(
            projectDate -> {
              var row = SummaryTableRow.builder()
                  .withValues(
                      projectDate.getDate()
                  );

              if (canModifyDates) {
                row.withAction("Remove", ReverseRouter.route(on(ActualProjectDateController.class)
                    .getRemoveConfirmation(
                        project.getField().getResource().getId(),
                        project.getId(),
                        projectDate.getId(),
                        String.valueOf(projectDate.getProjectDateType()))));
              }
              summaryTableView.addRow(row.build());
            }
        );

    var cardActions = new ArrayList<SummaryAction>();
    if (canModifyDates) {
      cardActions.add(
          new SummaryAction(
              "Add",
              ReverseRouter.route(on(ActualProjectDateController.class)
                  .getCreateProjectDate(project.getField().getResource().getId(), project.getId(), String.valueOf(type))),
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
    return new ArrayList<>(List.of(DATE));
  }

  private List<ActualProjectDateType> getDateTypesForProjectType(Project project) {
    return switch (project.getProjectType()) {
      case FIELD_DEVELOPMENT_PLAN, FIELD_DEVELOPMENT_PLAN_ADDENDUM ->
          List.of(ActualProjectDateType.CSR_FIRST_DRAFT, ActualProjectDateType.FDP_FIRST_DRAFT);
      case CESSATION_OF_PRODUCTION ->
          List.of(
              ActualProjectDateType.COP_FIRST_SUBMISSION,
              ActualProjectDateType.COP_NO_OBJECTION_LETTER_SENT,
              ActualProjectDateType.COP_CONFIRMATION_RECEIVED
          );
    };
  }
}
