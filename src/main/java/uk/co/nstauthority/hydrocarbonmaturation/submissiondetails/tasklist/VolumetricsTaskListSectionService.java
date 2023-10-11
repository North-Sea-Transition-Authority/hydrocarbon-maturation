package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters.MixedCaseVolumetricParametersController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters.MixedCaseVolumetricParametersSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs.ProspectMixedCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs.ProspectMixedCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;


@Service
class VolumetricsTaskListSectionService implements TaskListSectionService {

  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final OilCaseVolumetricParametersSubmissionService oilCaseVolumetricParametersSubmissionService;
  private final OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;
  private final GasCaseVolumetricParametersSubmissionService gasCaseVolumetricParametersSubmissionService;
  private final GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;
  private final ProspectMixedCaseOutputsSubmissionService prospectMixedCaseOutputsSubmissionService;
  private final MixedCaseVolumetricParametersSubmissionService mixedCaseVolumetricParametersSubmissionService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public VolumetricsTaskListSectionService(
      HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
      OilCaseVolumetricParametersSubmissionService oilCaseVolumetricParametersSubmissionService,
      OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService,
      GasCaseVolumetricParametersSubmissionService gasCaseVolumetricParametersSubmissionService,
      GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService,
      ProspectMixedCaseOutputsSubmissionService prospectMixedCaseOutputsSubmissionService,
      MixedCaseVolumetricParametersSubmissionService mixedCaseVolumetricParametersSubmissionService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.oilCaseVolumetricParametersSubmissionService = oilCaseVolumetricParametersSubmissionService;
    this.oilCaseOutputsSubmissionService = oilCaseOutputsSubmissionService;
    this.gasCaseVolumetricParametersSubmissionService = gasCaseVolumetricParametersSubmissionService;
    this.gasCaseOutputsSubmissionService = gasCaseOutputsSubmissionService;
    this.prospectMixedCaseOutputsSubmissionService = prospectMixedCaseOutputsSubmissionService;
    this.mixedCaseVolumetricParametersSubmissionService = mixedCaseVolumetricParametersSubmissionService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = new ArrayList<TaskListItem>();
    if (submissionDetail.isProspectSubmission()) {
      items.addAll(getTaskListItemsByHydrocarbonCase(submissionDetail));
    }

    if (items.isEmpty() || submissionDetail.isDryWellSubmission()) {
      return Optional.empty();
    }

    return Optional.of(
        new TaskListSection(
            "Volumetrics",
            40,
            items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isProspectSubmission()) {
      var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
          .getExtantReservoirSubmissionDetail(submissionDetail);
      var hydrocarbonCaseDetail = hydrocarbonCasePersistenceService
          .getHydrocarbonCaseDetail(submissionDetail)
          .orElseGet(HydrocarbonCaseDetail::new);

      var sectionSubmittable = true;

      if (hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCaseDetail)) {
        sectionSubmittable = gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
            && gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      }

      if (hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCaseDetail)) {
        sectionSubmittable = sectionSubmittable
            && oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
            && oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      }

      if (hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)) {
        sectionSubmittable = sectionSubmittable
            && mixedCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
            && prospectMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      }

      return sectionSubmittable;
    }
    return true;
  }

  private List<TaskListItem> getTaskListItemsByHydrocarbonCase(SubmissionDetail submissionDetail) {
    var listOfItems = new ArrayList<TaskListItem>();
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetail(submissionDetail);
    var hydrocarbonCaseDetail = hydrocarbonCasePersistenceService
        .getHydrocarbonCaseDetail(submissionDetail)
        .orElseGet(HydrocarbonCaseDetail::new);

    if (hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCaseDetail)) {
      listOfItems.add(
          new TaskListItem("Oil case volumetric parameters",
              TaskListLabel.notStartedOrComplete(
                  oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(OilCaseVolumetricParametersController.class)
                  .getOilCaseVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
      );
      listOfItems.add(
          new TaskListItem(
              "Oil case outputs (resources)",
              TaskListLabel.notStartedOrComplete(
                  oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(OilCaseOutputsController.class)
                  .getOilCaseOutputs(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
      );
    }

    if (hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCaseDetail)) {
      listOfItems.add(
          new TaskListItem("Gas case volumetric parameters",
              TaskListLabel.notStartedOrComplete(
                  gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
                  .getGasCaseVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
      );
      listOfItems.add(
          new TaskListItem(
              "Gas case outputs (resources)",
              TaskListLabel.notStartedOrComplete(
                  gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(GasCaseOutputsController.class)
                  .getGasCaseOutputs(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
      );
    }

    if (hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)) {
      listOfItems.add(
          new TaskListItem("Mixed case volumetric parameters",
              TaskListLabel.notStartedOrComplete(
                  mixedCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
                  .getMixedCaseVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
      );
      listOfItems.add(
          new TaskListItem(
              "Mixed case outputs (resources)",
              TaskListLabel.notStartedOrComplete(
                  prospectMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(ProspectMixedCaseOutputsController.class)
                  .getMixedCaseOutputs(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
      );
    }
    return listOfItems;
  }
}
