package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetrics.FieldOilVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
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
  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Autowired
  public ReservoirTaskListService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      PermeabilitySubmissionService permeabilitySubmissionService,
      GeneralReservoirInformationSubmissionService generalReservoirInformationSubmissionService,
      ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService,
      GeneralReservoirInformationService generalReservoirInformationService,
      OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
    this.generalReservoirInformationSubmissionService = generalReservoirInformationSubmissionService;
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
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
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var generalReservoirInformationCompleted =
        generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
    items.add(
        new TaskListItem("%s reservoir".formatted(
            reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission() ? "Discovery" : "Field"),
            TaskListLabel.notStartedOrComplete(generalReservoirInformationCompleted),
            ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                submissionDetail.getId(),
                reservoirSubmissionDetail.getId())))
    );

    if (generalReservoirInformationCompleted) {
      items.add(
          new TaskListItem("Permeability",
              TaskListLabel.notStartedOrComplete(
                  permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                  submissionDetail.getId(),
                  reservoirSubmissionDetail.getId())))
      );

      if (submissionDetail.isDiscoverySubmission()) {
        items.add(
            new TaskListItem("Reservoir properties",
                TaskListLabel.notStartedOrComplete(
                    reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      var hydrocarbonsDiscovered = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
          .map(ReservoirHydrocarbons::getHydrocarbonsDiscovered)
          .orElse(null);

      if ((hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
          || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)
          && resourceType == ResourceType.DISCOVERY) {
        items.add(
            new TaskListItem("Oil case volumetric parameters",
                TaskListLabel.notStartedOrComplete(
                    oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(OilCaseVolumetricsController.class).getOilCaseVolumetrics(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      if ((hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
          || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)
          && resourceType == ResourceType.FIELD) {
        items.add(
            new TaskListItem("Oil volumetric parameters",
                TaskListLabel.notStartedOrComplete(
                    oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(FieldOilVolumetricsController.class).getOilVolumetrics(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }
    }
    return items;
  }
}
