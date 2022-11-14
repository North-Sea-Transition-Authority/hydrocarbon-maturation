package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;


@Service
class VolumetricsTaskListSectionService implements TaskListSectionService {

  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService;
  private final OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;
  private final GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService;
  private final GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;
  private final MixedCaseOutputsSubmissionService mixedCaseOutputsSubmissionService;
  private final MixedCaseVolumetricsSubmissionService mixedCaseVolumetricsSubmissionService;

  public VolumetricsTaskListSectionService(HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                           OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService,
                                           OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService,
                                           GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService,
                                           GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService,
                                           MixedCaseOutputsSubmissionService mixedCaseOutputsSubmissionService,
                                           MixedCaseVolumetricsSubmissionService mixedCaseVolumetricsSubmissionService) {
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.oilCaseVolumetricsSubmissionService = oilCaseVolumetricsSubmissionService;
    this.oilCaseOutputsSubmissionService = oilCaseOutputsSubmissionService;
    this.gasCaseVolumetricsSubmissionService = gasCaseVolumetricsSubmissionService;
    this.gasCaseOutputsSubmissionService = gasCaseOutputsSubmissionService;
    this.mixedCaseOutputsSubmissionService = mixedCaseOutputsSubmissionService;
    this.mixedCaseVolumetricsSubmissionService = mixedCaseVolumetricsSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = getTaskListItemsByHydrocarbonCase(submissionDetail);

    if (items.isEmpty() || submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      return Optional.empty();
    }

    return Optional.of(
        new TaskListSection(
            "Volumetrics",
            40,
            items));
  }

  private List<TaskListItem> getTaskListItemsByHydrocarbonCase(SubmissionDetail submissionDetail) {
    var gasOnly = hydrocarbonCasePersistenceService.isGasOnlyPresent(submissionDetail);
    var oilOnly = hydrocarbonCasePersistenceService.isOilOnlyPresent(submissionDetail);
    var oilAndGas = hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(submissionDetail);
    var listOfItems = new ArrayList<TaskListItem>();
    if (oilOnly) {
      listOfItems.add(
          new TaskListItem("Oil case volumetrics parameters",
              TaskListLabel.notStartedOrComplete(
                  oilCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(OilCaseVolumetricsController.class)
                  .getOilCaseVolumetrics(submissionDetail.getId(), null)))
      );
      listOfItems.add(
          new TaskListItem(
              "Oil case outputs (resources)",
              TaskListLabel.notStartedOrComplete(
                  oilCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(OilCaseOutputsController.class)
                  .getOilCaseOutputs(submissionDetail.getId(), null)))
      );
    }

    if (gasOnly) {
      listOfItems.add(
          new TaskListItem("Gas case volumetrics parameters",
              TaskListLabel.notStartedOrComplete(
                  gasCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(GasCaseVolumetricsController.class)
                  .getGasCaseVolumetrics(submissionDetail.getId(), null)))
      );
      listOfItems.add(
          new TaskListItem(
              "Gas case outputs (resources)",
              TaskListLabel.notStartedOrComplete(gasCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(GasCaseOutputsController.class)
                  .getGasCaseOutputs(submissionDetail.getId(), null)))
      );
    }

    if (oilAndGas) {
      listOfItems.add(
          new TaskListItem("Mixed case volumetrics parameters",
              TaskListLabel.notStartedOrComplete(
                  mixedCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(MixedCaseVolumetricsController.class)
                  .getMixedCaseVolumetrics(submissionDetail.getId(), null)))
      );
      listOfItems.add(
          new TaskListItem(
              "Mixed case outputs (resources)",
              TaskListLabel.notStartedOrComplete(
                  mixedCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(MixedCaseOutputsController.class)
                  .getMixedCaseOutputs(submissionDetail.getId(), null)))
      );
    }
    return listOfItems;
  }
}
