package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverymixedcaseoutputs.DiscoveryMixedCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverymixedcaseoutputs.DiscoveryMixedCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetrics.FieldGasVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetrics.FieldOilVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
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
  private final OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;
  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;
  private final GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService;
  private final GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;
  private final DrillStemTestSubmissionService drillStemTestSubmissionService;
  private final DiscoveryMixedCaseOutputsSubmissionService discoveryMixedCaseOutputsSubmissionService;

  @Autowired
  public ReservoirTaskListService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      PermeabilitySubmissionService permeabilitySubmissionService,
      GeneralReservoirInformationSubmissionService generalReservoirInformationSubmissionService,
      OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService,
      ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService,
      GeneralReservoirInformationService generalReservoirInformationService,
      OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService,
      GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService,
      GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService,
      DrillStemTestSubmissionService drillStemTestSubmissionService,
      DiscoveryMixedCaseOutputsSubmissionService discoveryMixedCaseOutputsSubmissionService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
    this.generalReservoirInformationSubmissionService = generalReservoirInformationSubmissionService;
    this.oilCaseOutputsSubmissionService = oilCaseOutputsSubmissionService;
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.oilCaseVolumetricsSubmissionService = oilCaseVolumetricsSubmissionService;
    this.gasCaseVolumetricsSubmissionService = gasCaseVolumetricsSubmissionService;
    this.gasCaseOutputsSubmissionService = gasCaseOutputsSubmissionService;
    this.drillStemTestSubmissionService = drillStemTestSubmissionService;
    this.discoveryMixedCaseOutputsSubmissionService = discoveryMixedCaseOutputsSubmissionService;
  }

  public List<ReservoirTaskListItem> getAllReservoirTaskLists(SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
            .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);

    var taskLists = new ArrayList<ReservoirTaskListItem>();

    reservoirSubmissionDetails.forEach(reservoirSubmissionDetail -> {
      var reservoirTaskListItem = new ReservoirTaskListItem(
          reservoirSubmissionDetail.getReservoir().getName(),
          getReservoirDeleteUrl(reservoirSubmissionDetail),
          getTaskList(reservoirSubmissionDetail)
      );
      taskLists.add(reservoirTaskListItem);
    });
    return taskLists;
  }

  private String getReservoirDeleteUrl(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return ReverseRouter.route(on(DeleteReservoirController.class).deleteReservoir(
        reservoirSubmissionDetail.getSubmissionDetail().getId(),
        reservoirSubmissionDetail.getId(),
        null));
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

      if (submissionDetail.isDiscoverySubmission() || submissionDetail.isFieldSubmission()) {
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

      if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
          && resourceType == ResourceType.DISCOVERY) {
        items.add(
            new TaskListItem("Oil case outputs (resources)",
                TaskListLabel.notStartedOrComplete(
                    oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(OilCaseOutputsController.class).getOilCaseOutputs(
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

      if ((hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY
          || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)
          && resourceType == ResourceType.DISCOVERY) {
        items.add(
            new TaskListItem("Gas case volumetric parameters",
                TaskListLabel.notStartedOrComplete(
                    gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(GasCaseVolumetricsController.class).getGasCaseVolumetrics(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      if ((hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY)
          && resourceType == ResourceType.DISCOVERY) {
        items.add(
            new TaskListItem("Gas case outputs (resources)",
                TaskListLabel.notStartedOrComplete(
                    gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(GasCaseOutputsController.class).getGasCaseOutputs(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      if ((hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY
          || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)
          && resourceType == ResourceType.FIELD) {
        items.add(
            new TaskListItem("Gas volumetric parameters",
                TaskListLabel.notStartedOrComplete(
                    gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(FieldGasVolumetricsController.class).getGasVolumetrics(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
          && resourceType == ResourceType.DISCOVERY) {
        items.add(
            new TaskListItem("Oil and gas case outputs (resources)",
                TaskListLabel.notStartedOrComplete(
                    discoveryMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(DiscoveryMixedCaseOutputsController.class).getMixedCaseOutputs(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      items.add(
          new TaskListItem("Well test results",
              TaskListLabel.notStartedOrComplete(
                  drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(DrillStemTestController.class).getDrillStemTestPage(
                  submissionDetail.getId(),
                  reservoirSubmissionDetail.getId(),
                  null)))
      );
    }
    return items;
  }
}
