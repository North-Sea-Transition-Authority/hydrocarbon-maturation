package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;

@Service
class ReservoirTaskListSectionService implements TaskListSectionService {

  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Autowired
  ReservoirTaskListSectionService(ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService) {
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = List.of(
        new TaskListItem("Reservoir properties",
            TaskListLabel.notStartedOrComplete(reservoirPropertiesSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetail.getId())))
    );

    return Optional.of(new TaskListSection("Reservoir", 40, items));
  }
}
