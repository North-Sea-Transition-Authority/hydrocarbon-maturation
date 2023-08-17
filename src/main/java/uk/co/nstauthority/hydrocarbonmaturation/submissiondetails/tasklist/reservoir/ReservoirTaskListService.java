package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;
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
  private final GeneralReservoirInformationSubmissionService generalReservoirInformationSubmissionService;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService;

  @Autowired
  public ReservoirTaskListService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      PermeabilitySubmissionService permeabilitySubmissionService,
      GeneralReservoirInformationSubmissionService generalReservoirInformationSubmissionService,
      GeneralReservoirInformationService generalReservoirInformationService,
      OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
    this.generalReservoirInformationSubmissionService = generalReservoirInformationSubmissionService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.oilCaseVolumetricsSubmissionService = oilCaseVolumetricsSubmissionService;
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

    var generalReservoirInformationCompleted =
        generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
    items.add(
        new TaskListItem("%s reservoir".formatted(
            reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission() ? "Discovery" : "Field"),
            TaskListLabel.notStartedOrComplete(generalReservoirInformationCompleted),
            ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                reservoirSubmissionDetail.getSubmissionDetail().getId(),
                reservoirSubmissionDetail.getId())))
    );

    if (generalReservoirInformationCompleted) {
      items.add(
          new TaskListItem("Permeability",
              TaskListLabel.notStartedOrComplete(
                  permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                  reservoirSubmissionDetail.getSubmissionDetail().getId(),
                  reservoirSubmissionDetail.getId())))
      );

      var hydrocarbonsDiscovered = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
          .map(ReservoirHydrocarbons::getHydrocarbonsDiscovered)
          .orElse(null);

      if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
        items.add(
            new TaskListItem("Oil case volumetric parameters",
                TaskListLabel.notStartedOrComplete(
                    oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(OilCaseVolumetricsController.class).getOilCaseVolumetrics(
                    reservoirSubmissionDetail.getSubmissionDetail().getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }
    }
    return items;
  }
}
