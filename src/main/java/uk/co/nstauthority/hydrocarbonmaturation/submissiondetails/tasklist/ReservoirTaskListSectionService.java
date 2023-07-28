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

@Service
public class ReservoirTaskListSectionService implements TaskListSectionService {

  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;
  private final PermeabilitySubmissionService permeabilitySubmissionService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Autowired
  public ReservoirTaskListSectionService(ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService,
                                         PermeabilitySubmissionService permeabilitySubmissionService,
                                         ReservoirSubmissionDetailPersistenceService
                                               reservoirSubmissionDetailPersistenceService) {
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
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

    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetail(submissionDetail);

    items.add(
        new TaskListItem("Reservoir properties",
            TaskListLabel.notStartedOrComplete(
                reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
            ReverseRouter.route(on(ReservoirPropertiesController.class)
                .getReservoirProperties(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
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
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetail(submissionDetail);

    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      return reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
          && permeabilitySubmissionService.isSectionSubmittable(submissionDetail);
    }
    return reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
  }
}
