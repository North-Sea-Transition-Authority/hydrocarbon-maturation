package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirListController;

@Service
public class ReservoirTaskListSectionService implements TaskListSectionService {

  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  private final PermeabilitySubmissionService permeabilitySubmissionService;

  @Autowired
  public ReservoirTaskListSectionService(ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService,
                                         PermeabilitySubmissionService permeabilitySubmissionService) {
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = new ArrayList<TaskListItem>();

    if (submissionDetail.isDiscoverySubmission()) {
      items.add(
          new TaskListItem("Discovery reservoirs",
              //TODO base task list label on completeness of reservoir sub-tasks
              TaskListLabel.NOT_COMPLETE,
              //TODO redirect to reservoir sub task list if any reservoirs are saved
              ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetail.getId(), null)))
      );
    }

    items.add(
        new TaskListItem("Reservoir properties",
            TaskListLabel.notStartedOrComplete(
                reservoirPropertiesSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(
                on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetail.getId())))
    );

    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      items.add(new TaskListItem("Permeability",
          TaskListLabel.notStartedOrComplete(permeabilitySubmissionService.isSectionSubmittable(submissionDetail)),
          ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(submissionDetail.getId(), null)))
      );
    }

    return Optional.of(new TaskListSection("Reservoir", 40, items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      return reservoirPropertiesSubmissionService.isSectionSubmittable(submissionDetail)
          && permeabilitySubmissionService.isSectionSubmittable(submissionDetail);
    }
    return reservoirPropertiesSubmissionService.isSectionSubmittable(submissionDetail);
  }
}
