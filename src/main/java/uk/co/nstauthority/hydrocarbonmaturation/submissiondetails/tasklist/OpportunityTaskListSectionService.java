package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSubmissionService;

@Service
class OpportunityTaskListSectionService implements TaskListSectionService {

  private final OpportunityDetailsSubmissionService opportunityDetailsSubmissionService;
  private final TrapPropertiesSubmissionService trapPropertiesSubmissionService;
  private final SourceAndSealSubmissionService sourceAndSealSubmissionService;
  private final HydrocarbonCaseSubmissionService hydrocarbonCaseSubmissionService;
  private final ChanceOfSuccessSubmissionService chanceOfSuccessSubmissionService;

  @Autowired
  OpportunityTaskListSectionService(OpportunityDetailsSubmissionService opportunityDetailsSubmissionService,
                                    TrapPropertiesSubmissionService trapPropertiesSubmissionService,
                                    SourceAndSealSubmissionService sourceAndSealSubmissionService,
                                    HydrocarbonCaseSubmissionService hydrocarbonCaseSubmissionService,
                                    ChanceOfSuccessSubmissionService chanceOfSuccessSubmissionService) {
    this.opportunityDetailsSubmissionService = opportunityDetailsSubmissionService;
    this.trapPropertiesSubmissionService = trapPropertiesSubmissionService;
    this.sourceAndSealSubmissionService = sourceAndSealSubmissionService;
    this.hydrocarbonCaseSubmissionService = hydrocarbonCaseSubmissionService;
    this.chanceOfSuccessSubmissionService = chanceOfSuccessSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = getTaskListItemsBySubmissionType(submissionDetail);

    return Optional.of(new TaskListSection("Opportunity", 20, items));
  }

  private List<TaskListItem> getTaskListItemsBySubmissionType(SubmissionDetail submissionDetail) {
    var listOfItems = new ArrayList<TaskListItem>();

    listOfItems.add(
        new TaskListItem("Opportunity details",
            TaskListLabel.notStartedOrComplete(
                opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(
                on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetail.getId(), null)))
    );

    listOfItems.add(
        new TaskListItem("Trap properties",
            TaskListLabel.notStartedOrComplete(trapPropertiesSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetail.getId())))
    );

    listOfItems.add(
        new TaskListItem("Source and seal",
            TaskListLabel.notStartedOrComplete(sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetail.getId())))
    );

    if (submissionDetail.getSubmissionType() == SubmissionType.PROSPECT_UPDATE
        || submissionDetail.getSubmissionType() == SubmissionType.CREATE_PROSPECT) {
      listOfItems.add(new TaskListItem(
          "Hydrocarbon case",
          TaskListLabel.notStartedOrComplete(hydrocarbonCaseSubmissionService.isSectionSubmittable(submissionDetail)),
          ReverseRouter.route(on(HydrocarbonCaseController.class).getHydrocarbonCase(submissionDetail.getId())))
      );
      listOfItems.add(new TaskListItem(
          "Chance of success",
          TaskListLabel.notStartedOrComplete(chanceOfSuccessSubmissionService.isSectionSubmittable(submissionDetail)),
          ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetail.getId())))
      );
    }

    return listOfItems;
  }
}
