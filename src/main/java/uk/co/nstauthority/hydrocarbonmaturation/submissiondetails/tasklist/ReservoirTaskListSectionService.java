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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;

@Service
public class ReservoirTaskListSectionService implements TaskListSectionService {

  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;
  private final PermeabilitySubmissionService permeabilitySubmissionService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Autowired
  public ReservoirTaskListSectionService(
      ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService,
      PermeabilitySubmissionService permeabilitySubmissionService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = new ArrayList<TaskListItem>();

    if (submissionDetail.isDiscoverySubmission() || submissionDetail.isFieldSubmission()) {
      items.add(
          new TaskListItem("%s reservoirs".formatted(
              submissionDetail.getSubmission().getResource().getType().getDisplayName()),
              //TODO base task list label on completeness of reservoir sub-tasks
              TaskListLabel.NOT_COMPLETE,
              reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)
                  ? ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(
                      submissionDetail.getId(), null))
                  : ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetail.getId(), null)))
      );
    }

    if (submissionDetail.isProspectSubmission() || submissionDetail.isDryWellSubmission()) {
      var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
          .getExtantReservoirSubmissionDetail(submissionDetail);

      if (submissionDetail.isDryWellSubmission()) {
        items.add(new TaskListItem("Permeability",
            TaskListLabel.notStartedOrComplete(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
            ReverseRouter.route(on(PermeabilityController.class)
                .getPermeabilityPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
        );
      }

      items.add(
          new TaskListItem("Reservoir properties",
              TaskListLabel.notStartedOrComplete(
                  reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(ReservoirPropertiesController.class)
                  .getReservoirProperties(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
      );
    }

    return Optional.of(new TaskListSection(
        "Reservoirs",
        40,
        items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isProspectSubmission() || submissionDetail.isDryWellSubmission()) {
      var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
          .getExtantReservoirSubmissionDetail(submissionDetail);

      if (submissionDetail.isDryWellSubmission()) {
        return reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
            && permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      } else if (submissionDetail.isProspectSubmission()) {
        return reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      }
    }

    return true;
  }
}
