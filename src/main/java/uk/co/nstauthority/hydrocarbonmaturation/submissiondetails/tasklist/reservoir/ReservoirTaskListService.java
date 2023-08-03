package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListLabel;

@Service
public class ReservoirTaskListService {

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final PermeabilitySubmissionService permeabilitySubmissionService;

  @Autowired
  public ReservoirTaskListService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      PermeabilitySubmissionService permeabilitySubmissionService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
  }

  public List<ReservoirTaskListItem> getAllReservoirTaskLists(SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetails =
        reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    var taskLists = new ArrayList<ReservoirTaskListItem>();
    reservoirSubmissionDetails.forEach(reservoirSubmissionDetail -> {
      var reservoirTaskListItem = new ReservoirTaskListItem(
          reservoirSubmissionDetail.getReservoir().getName(),
          getTaskList(reservoirSubmissionDetail)
      );
      taskLists.add(reservoirTaskListItem);
    });
    return taskLists;
  }

  private List<TaskListItem> getTaskList(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var items = new ArrayList<TaskListItem>();
    items.add(
        new TaskListItem("Permeability",
            TaskListLabel.notStartedOrComplete(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
            ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                reservoirSubmissionDetail.getSubmissionDetail().getId(),
                reservoirSubmissionDetail.getId())))
    );
    return items;
  }
}
