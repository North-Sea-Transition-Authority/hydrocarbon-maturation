package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
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

  @Autowired
  OpportunityTaskListSectionService(OpportunityDetailsSubmissionService opportunityDetailsSubmissionService,
                                    TrapPropertiesSubmissionService trapPropertiesSubmissionService,
                                    SourceAndSealSubmissionService sourceAndSealSubmissionService) {
    this.opportunityDetailsSubmissionService = opportunityDetailsSubmissionService;
    this.trapPropertiesSubmissionService = trapPropertiesSubmissionService;
    this.sourceAndSealSubmissionService = sourceAndSealSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = List.of(
        new TaskListItem("Opportunity details",
            TaskListLabel.notStartedOrComplete(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetail.getId(), null))),
        new TaskListItem("Trap properties",
            TaskListLabel.notStartedOrComplete(trapPropertiesSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetail.getId()))),
        new TaskListItem("Source and seal",
            TaskListLabel.notStartedOrComplete(sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetail.getId())))
    );

    return Optional.of(new TaskListSection("Opportunity", 20, items));
  }
}
