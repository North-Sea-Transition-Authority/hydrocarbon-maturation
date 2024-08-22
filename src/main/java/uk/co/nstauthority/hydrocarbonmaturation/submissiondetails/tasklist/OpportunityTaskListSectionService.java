package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseUploadController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSubmissionService;

@Service
public class OpportunityTaskListSectionService implements TaskListSectionService {

  public static final String OUTLINE_FOR_MAXIMUM_CASE = "Outline for maximum case";
  static final String PROSPECT_CHANCE_OF_SUCCESS = "Chance of success";
  static final String DISCOVERY_CHANCE_OF_SUCCESS = "Commercial chance of success";
  private final OpportunityDetailsSubmissionService opportunityDetailsSubmissionService;
  private final TrapPropertiesSubmissionService trapPropertiesSubmissionService;
  private final SourceAndSealSubmissionService sourceAndSealSubmissionService;
  private final HydrocarbonCaseSubmissionService hydrocarbonCaseSubmissionService;
  private final ChanceOfSuccessSubmissionService chanceOfSuccessSubmissionService;
  private final OutlineForMaximumCaseSubmissionService outlineForMaximumCaseSubmissionService;
  private final FieldTrapPropertiesSubmissionService fieldTrapPropertiesSubmissionService;

  @Autowired
  OpportunityTaskListSectionService(OpportunityDetailsSubmissionService opportunityDetailsSubmissionService,
                                    TrapPropertiesSubmissionService trapPropertiesSubmissionService,
                                    SourceAndSealSubmissionService sourceAndSealSubmissionService,
                                    HydrocarbonCaseSubmissionService hydrocarbonCaseSubmissionService,
                                    ChanceOfSuccessSubmissionService chanceOfSuccessSubmissionService,
                                    OutlineForMaximumCaseSubmissionService outlineForMaximumCaseSubmissionService,
                                    FieldTrapPropertiesSubmissionService fieldTrapPropertiesSubmissionService) {
    this.opportunityDetailsSubmissionService = opportunityDetailsSubmissionService;
    this.trapPropertiesSubmissionService = trapPropertiesSubmissionService;
    this.sourceAndSealSubmissionService = sourceAndSealSubmissionService;
    this.hydrocarbonCaseSubmissionService = hydrocarbonCaseSubmissionService;
    this.chanceOfSuccessSubmissionService = chanceOfSuccessSubmissionService;
    this.outlineForMaximumCaseSubmissionService = outlineForMaximumCaseSubmissionService;
    this.fieldTrapPropertiesSubmissionService = fieldTrapPropertiesSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail, User user) {

    var items = getTaskListItemsBySubmissionType(submissionDetail, user);

    return Optional.of(new TaskListSection(
        submissionDetail.isFieldSubmission() ? "Field details" : "Opportunity details",
        20,
        items
    ));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    return switch (submissionDetail.getSubmissionType()) {
      case PROSPECT_UPDATE, CREATE_PROSPECT, RELINQUISHMENT_PROSPECT ->
          opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail, user)
              && outlineForMaximumCaseSubmissionService.isSectionSubmittable(submissionDetail, user)
              && chanceOfSuccessSubmissionService.isSectionSubmittable(submissionDetail, user)
              && trapPropertiesSubmissionService.isSectionSubmittable(submissionDetail, user)
              && sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail, user)
              && hydrocarbonCaseSubmissionService.isSectionSubmittable(submissionDetail, user);
      case CREATE_DISCOVERY, CREATE_DISCOVERY_FROM_PROSPECT, UPDATE_DISCOVERY, RELINQUISHMENT_DISCOVERY ->
          discoveryAndDrillSharedChecks(submissionDetail, user)
              && chanceOfSuccessSubmissionService.isSectionSubmittable(submissionDetail, user);
      case POST_DRILL_DRY_HOLE -> discoveryAndDrillSharedChecks(submissionDetail, user);
      case CREATE_CSR, CREATE_FDP, CREATE_FDPA, CREATE_COP ->
          sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail, user)
              && fieldTrapPropertiesSubmissionService.isSectionSubmittable(submissionDetail, user);
      case CREATE_FIELD -> throw new ActionNotAllowedException("New fields do not have task lists");
    };
  }


  private List<TaskListItem> getTaskListItemsBySubmissionType(SubmissionDetail submissionDetail, User user) {
    var listOfItems = new ArrayList<TaskListItem>();

    if (!submissionDetail.isFieldSubmission()) {
      listOfItems.add(
          new TaskListItem("Opportunity details",
              TaskListLabel.notStartedOrComplete(
                  opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail, user)),
              ReverseRouter.route(
                  on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetail.getId(), null)))
      );

      listOfItems.add(
          new TaskListItem(OUTLINE_FOR_MAXIMUM_CASE,
              TaskListLabel.notStartedOrComplete(
                  outlineForMaximumCaseSubmissionService.isSectionSubmittable(submissionDetail, user)),
              ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class)
                  .getOutlineForMaximumCaseUpload(submissionDetail.getId(), null)))
      );
    }

    if (submissionDetail.isFieldSubmission()) {
      listOfItems.add(
          new TaskListItem("Trap properties",
              TaskListLabel.notStartedOrComplete(
                  fieldTrapPropertiesSubmissionService.isSectionSubmittable(submissionDetail, user)),
              ReverseRouter.route(
                  on(FieldTrapPropertiesController.class).getFieldTrapProperties(submissionDetail.getId())))
      );
    } else {
      listOfItems.add(
          new TaskListItem("Trap properties",
              TaskListLabel.notStartedOrComplete(
                  trapPropertiesSubmissionService.isSectionSubmittable(submissionDetail, user)),
              ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetail.getId())))
      );
    }

    listOfItems.add(
        new TaskListItem("Source and seal",
            TaskListLabel.notStartedOrComplete(sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail, user)),
            ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetail.getId())))
    );

    if (submissionDetail.isProspectSubmission()) {
      listOfItems.add(new TaskListItem(
          "Hydrocarbon case",
          TaskListLabel.notStartedOrComplete(hydrocarbonCaseSubmissionService.isSectionSubmittable(submissionDetail, user)),
          ReverseRouter.route(on(HydrocarbonCaseController.class).getHydrocarbonCase(submissionDetail.getId())))
      );
      listOfItems.add(getChanceOfSuccessTaskListItemWithDisplay(PROSPECT_CHANCE_OF_SUCCESS, submissionDetail, user));
    }

    if (submissionDetail.isDiscoverySubmission()) {
      listOfItems.add(getChanceOfSuccessTaskListItemWithDisplay(DISCOVERY_CHANCE_OF_SUCCESS, submissionDetail, user));
    }

    return listOfItems;
  }

  private Boolean discoveryAndDrillSharedChecks(SubmissionDetail submissionDetail, User user) {
    return opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail, user)
        && outlineForMaximumCaseSubmissionService.isSectionSubmittable(submissionDetail, user)
        && sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail, user)
        && trapPropertiesSubmissionService.isSectionSubmittable(submissionDetail, user);
  }

  private TaskListItem getChanceOfSuccessTaskListItemWithDisplay(String displayName,
                                                                 SubmissionDetail submissionDetail,
                                                                 User user) {
    return new TaskListItem(
        displayName,
        TaskListLabel.notStartedOrComplete(chanceOfSuccessSubmissionService.isSectionSubmittable(submissionDetail, user)),
        ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetail.getId()))
    );
  }
}
