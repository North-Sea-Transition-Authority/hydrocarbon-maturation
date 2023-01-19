package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
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
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  public VolumetricsTaskListSectionService(HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                           OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService,
                                           OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService,
                                           GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService,
                                           GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService,
                                           MixedCaseOutputsSubmissionService mixedCaseOutputsSubmissionService,
                                           MixedCaseVolumetricsSubmissionService mixedCaseVolumetricsSubmissionService,
                                           DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService) {
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.oilCaseVolumetricsSubmissionService = oilCaseVolumetricsSubmissionService;
    this.oilCaseOutputsSubmissionService = oilCaseOutputsSubmissionService;
    this.gasCaseVolumetricsSubmissionService = gasCaseVolumetricsSubmissionService;
    this.gasCaseOutputsSubmissionService = gasCaseOutputsSubmissionService;
    this.mixedCaseOutputsSubmissionService = mixedCaseOutputsSubmissionService;
    this.mixedCaseVolumetricsSubmissionService = mixedCaseVolumetricsSubmissionService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = getTaskListItemsByHydrocarbonCase(submissionDetail);

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
    if (submissionDetail.isDiscoverySubmission() || submissionDetail.isProspectSubmission()) {
      var discoveryWellDetail = discoveryWellDetailsPersistenceService
          .getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
          .orElseGet(DiscoveryWellDetail::new);
      var hydrocarbonCaseDetail = hydrocarbonCasePersistenceService
          .getHydrocarbonCaseDetail(submissionDetail)
          .orElseGet(HydrocarbonCaseDetail::new);
      var gasOnly = isGasOnly(hydrocarbonCaseDetail, discoveryWellDetail, submissionDetail);
      var oilOnly = isOilOnly(hydrocarbonCaseDetail, discoveryWellDetail, submissionDetail);
      var oilAndGas = isMixedOnly(hydrocarbonCaseDetail, discoveryWellDetail, submissionDetail);

      var sectionSubmittable = true;

      if (gasOnly) {
        sectionSubmittable = gasCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)
            && gasCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail);
      }

      if (oilOnly) {
        sectionSubmittable = sectionSubmittable && oilCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)
            && oilCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail);
      }

      if (oilAndGas) {
        sectionSubmittable = sectionSubmittable && mixedCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)
            && mixedCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail);
      }

      return sectionSubmittable;
    }
    return true;
  }

  private List<TaskListItem> getTaskListItemsByHydrocarbonCase(SubmissionDetail submissionDetail) {
    var discoveryWellDetail = discoveryWellDetailsPersistenceService
        .getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
        .orElseGet(DiscoveryWellDetail::new);
    var hydrocarbonCaseDetail = hydrocarbonCasePersistenceService
        .getHydrocarbonCaseDetail(submissionDetail)
        .orElseGet(HydrocarbonCaseDetail::new);
    var gasOnly = isGasOnly(hydrocarbonCaseDetail, discoveryWellDetail, submissionDetail);
    var oilOnly = isOilOnly(hydrocarbonCaseDetail, discoveryWellDetail, submissionDetail);
    var oilAndGas = isMixedOnly(hydrocarbonCaseDetail, discoveryWellDetail, submissionDetail);

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

  private boolean isGasOnly(HydrocarbonCaseDetail hydrocarbonCaseDetail,
                            DiscoveryWellDetail discoveryWellDetail,
                            SubmissionDetail submissionDetail) {
    return (submissionDetail.isProspectSubmission()
        && hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCaseDetail))
        || (submissionDetail.isDiscoverySubmission()
        && discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(discoveryWellDetail));
  }

  private boolean isOilOnly(HydrocarbonCaseDetail hydrocarbonCaseDetail,
                            DiscoveryWellDetail discoveryWellDetail,
                            SubmissionDetail submissionDetail) {
    return (submissionDetail.isProspectSubmission()
        && hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCaseDetail))
        || (submissionDetail.isDiscoverySubmission()
        && discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(discoveryWellDetail));
  }

  private boolean isMixedOnly(HydrocarbonCaseDetail hydrocarbonCaseDetail,
                              DiscoveryWellDetail discoveryWellDetail,
                              SubmissionDetail submissionDetail) {
    return (submissionDetail.isProspectSubmission()
        && hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail))
        || (submissionDetail.isDiscoverySubmission()
        && discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail));
  }
}
