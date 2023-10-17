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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetricparameters.FieldGasVolumetricParametersController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetricparameters.FieldOilVolumetricParametersController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertyController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertyController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertyController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySubmissionService;
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
  private final OilCaseVolumetricParametersSubmissionService oilCaseVolumetricParametersSubmissionService;
  private final OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;
  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;
  private final GasCaseVolumetricParametersSubmissionService gasCaseVolumetricParametersSubmissionService;
  private final GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;
  private final DrillStemTestSubmissionService drillStemTestSubmissionService;
  private final OilFluidPropertySubmissionService oilFluidPropertySubmissionService;
  private final GasFluidPropertySubmissionService gasFluidPropertySubmissionService;
  private final DiscoveryMixedCaseOutputsSubmissionService discoveryMixedCaseOutputsSubmissionService;
  private final GeneralFluidPropertySubmissionService generalFluidPropertySubmissionService;

  @Autowired
  public ReservoirTaskListService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      PermeabilitySubmissionService permeabilitySubmissionService,
      GeneralReservoirInformationSubmissionService generalReservoirInformationSubmissionService,
      OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService,
      ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService,
      GeneralReservoirInformationService generalReservoirInformationService,
      OilCaseVolumetricParametersSubmissionService oilCaseVolumetricParametersSubmissionService,
      GasCaseVolumetricParametersSubmissionService gasCaseVolumetricParametersSubmissionService,
      GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService,
      DrillStemTestSubmissionService drillStemTestSubmissionService,
      DiscoveryMixedCaseOutputsSubmissionService discoveryMixedCaseOutputsSubmissionService,
      OilFluidPropertySubmissionService oilFluidPropertySubmissionService,
      GasFluidPropertySubmissionService gasFluidPropertySubmissionService,
      GeneralFluidPropertySubmissionService generalFluidPropertySubmissionService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
    this.generalReservoirInformationSubmissionService = generalReservoirInformationSubmissionService;
    this.oilCaseOutputsSubmissionService = oilCaseOutputsSubmissionService;
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.oilCaseVolumetricParametersSubmissionService = oilCaseVolumetricParametersSubmissionService;
    this.gasCaseVolumetricParametersSubmissionService = gasCaseVolumetricParametersSubmissionService;
    this.gasCaseOutputsSubmissionService = gasCaseOutputsSubmissionService;
    this.drillStemTestSubmissionService = drillStemTestSubmissionService;
    this.discoveryMixedCaseOutputsSubmissionService = discoveryMixedCaseOutputsSubmissionService;
    this.oilFluidPropertySubmissionService = oilFluidPropertySubmissionService;
    this.gasFluidPropertySubmissionService = gasFluidPropertySubmissionService;
    this.generalFluidPropertySubmissionService = generalFluidPropertySubmissionService;
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

      items.add(
          new TaskListItem("Reservoir properties",
              TaskListLabel.notStartedOrComplete(
                  reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                  submissionDetail.getId(),
                  reservoirSubmissionDetail.getId())))
      );

      var hydrocarbonsDiscovered = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
          .map(ReservoirHydrocarbons::getHydrocarbonsDiscovered)
          .orElse(null);

      if ((hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
          || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)
          && resourceType == ResourceType.DISCOVERY) {
        items.add(
            new TaskListItem("Oil case volumetric parameters",
                TaskListLabel.notStartedOrComplete(
                    oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(OilCaseVolumetricParametersController.class).getOilCaseVolumetrics(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
          && resourceType == ResourceType.DISCOVERY) {
        items.add(
            new TaskListItem("Oil case volumetrics",
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
                    oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(FieldOilVolumetricParametersController.class).getOilVolumetrics(
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
                    gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(GasCaseVolumetricParametersController.class).getGasCaseVolumetrics(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      if ((hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY)
          && resourceType == ResourceType.DISCOVERY) {
        items.add(
            new TaskListItem("Gas case volumetrics",
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
                    gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                ReverseRouter.route(on(FieldGasVolumetricParametersController.class).getGasVolumetrics(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
        );
      }

      if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
              && resourceType == ResourceType.DISCOVERY) {
        items.add(
                new TaskListItem("Oil and gas case volumetrics",
                        TaskListLabel.notStartedOrComplete(
                                discoveryMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
                        ReverseRouter.route(on(DiscoveryMixedCaseOutputsController.class).getMixedCaseOutputs(
                                submissionDetail.getId(),
                                reservoirSubmissionDetail.getId())))
        );
      }

      if (!submissionDetail.isCessationOfProductionSubmission()) {
        if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY
            || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
          items.add(new TaskListItem("Gas properties",
              TaskListLabel.notStartedOrComplete(
                  gasFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(GasFluidPropertyController.class)
                  .getGasFluidProperties(submissionDetail.getId(), reservoirSubmissionDetail.getId()))));
        }

        if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
            || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
          items.add(new TaskListItem("Oil properties",
              TaskListLabel.notStartedOrComplete(
                  oilFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(OilFluidPropertyController.class)
                  .getOilFluidProperties(submissionDetail.getId(), reservoirSubmissionDetail.getId()))));
        }
      }

      items.add(
          new TaskListItem("General properties",
              TaskListLabel.notStartedOrComplete(
                  generalFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(GeneralFluidPropertyController.class).getGeneralFluidProperties(
                  submissionDetail.getId(),
                  reservoirSubmissionDetail.getId())))
      );

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
