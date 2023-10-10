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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs.ProspectMixedCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs.ProspectMixedCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;


@Service
class VolumetricsTaskListSectionService implements TaskListSectionService {

  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService;
  private final OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;
  private final GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService;
  private final GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;
  private final ProspectMixedCaseOutputsSubmissionService prospectMixedCaseOutputsSubmissionService;
  private final MixedCaseVolumetricsSubmissionService mixedCaseVolumetricsSubmissionService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public VolumetricsTaskListSectionService(
      HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
      OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService,
      OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService,
      GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService,
      GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService,
      ProspectMixedCaseOutputsSubmissionService prospectMixedCaseOutputsSubmissionService,
      MixedCaseVolumetricsSubmissionService mixedCaseVolumetricsSubmissionService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.oilCaseVolumetricsSubmissionService = oilCaseVolumetricsSubmissionService;
    this.oilCaseOutputsSubmissionService = oilCaseOutputsSubmissionService;
    this.gasCaseVolumetricsSubmissionService = gasCaseVolumetricsSubmissionService;
    this.gasCaseOutputsSubmissionService = gasCaseOutputsSubmissionService;
    this.prospectMixedCaseOutputsSubmissionService = prospectMixedCaseOutputsSubmissionService;
    this.mixedCaseVolumetricsSubmissionService = mixedCaseVolumetricsSubmissionService;
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
        sectionSubmittable = gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
            && gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      }

      if (hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCaseDetail)) {
        sectionSubmittable = sectionSubmittable
            && oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
            && oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      }

      if (hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)) {
        sectionSubmittable = sectionSubmittable
            && mixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
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
                  oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(OilCaseVolumetricsController.class)
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
                  gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(GasCaseVolumetricsController.class)
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
                  mixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(MixedCaseVolumetricsController.class)
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
