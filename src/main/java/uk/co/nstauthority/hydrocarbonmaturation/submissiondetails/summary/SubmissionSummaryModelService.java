package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Service
public class SubmissionSummaryModelService {
  private final ProspectSummaryService prospectSummaryService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final DiscoverySummaryService discoverySummaryService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;
  private final PostDrillDryWellSummaryService postDrillDryWellSummaryService;
  private final SubmissionReadyService submissionReadyService;

  SubmissionSummaryModelService(ProspectSummaryService prospectSummaryService,
                                HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                DiscoverySummaryService discoverySummaryService,
                                DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService,
                                PostDrillDryWellSummaryService postDrillDryWellSummaryService,
                                SubmissionReadyService submissionReadyService) {
    this.prospectSummaryService = prospectSummaryService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.discoverySummaryService = discoverySummaryService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
    this.postDrillDryWellSummaryService = postDrillDryWellSummaryService;
    this.submissionReadyService = submissionReadyService;
  }

  public ModelAndView createSummaryForSubmissionTypeToModelAndView(SubmissionDetail submissionDetail, String viewName) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE, RELINQUISHMENT_PROSPECT ->
          createProspectSubmissionSummaryModelAndView(submissionDetail, viewName);
      case POST_DRILL_DRY_HOLE ->
          createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_DISCOVERY, UPDATE_DISCOVERY, CREATE_DISCOVERY_FROM_PROSPECT, RELINQUISHMENT_DISCOVERY ->
          createDiscoverySubmissionSummaryModelAndView(submissionDetail, viewName);
    };
  }

  public void addCommonModelAttributes(ModelAndView modelAndView,
                                       SubmissionSummaryForm form,
                                       SubmissionDetail submissionDetail,
                                       String pageName) {
    modelAndView
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId())))
        .addObject("submittable", submissionReadyService.isSubmissionSubmittable(submissionDetail));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, pageName, submissionDetail);
  }

  public ModelAndView createProspectSubmissionSummaryModelAndView(SubmissionDetail submissionDetail, String viewName) {
    var modelAndView = new ModelAndView(viewName)
        .addObject("generalDetailsSummaryView",
            prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("sourceAndSealSummaryView",
            prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("chanceOfSuccessSummaryView",
            prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("hydrocarbonCaseSummaryView",
            prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("reservoirPropertiesSummaryView",
            prospectSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("trapPropertiesSummaryView",
            prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseVolumetricsSummaryView",
            prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseOutputsSummaryView",
            prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCaseVolumetricsSummaryView",
            prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCaseOutputsSummaryView",
            prospectSummaryService.getGasCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("mixedCaseVolumetricsSummaryView",
            prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("mixedCaseOutputsSummaryView",
            prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("opportunityDetailsSummaryView",
            prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail));

    hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail)
        .ifPresentOrElse(hydrocarbonCaseDetail ->
                modelAndView
                    .addObject("gasCase",
                        hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCaseDetail))
                    .addObject("oilCase",
                        hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCaseDetail))
                    .addObject("mixedCase",
                        hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)),
            () ->
                modelAndView
                    .addObject("gasCase", false)
                    .addObject("oilCase", false)
                    .addObject("mixedCase", false)
        );
    return modelAndView;
  }

  public ModelAndView createPostDrillDryWellSubmissionSummaryModelAndView(SubmissionDetail submissionDetail, String viewName) {

    return new ModelAndView(viewName)
        .addObject("dryWellDetailsSummaryView",
            postDrillDryWellSummaryService.getDryWellDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("generalDetailsSummaryView",
            postDrillDryWellSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("permeabilitySummaryView",
            postDrillDryWellSummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("reservoirPropertiesSummaryView",
            postDrillDryWellSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail));
  }

  public ModelAndView createDiscoverySubmissionSummaryModelAndView(SubmissionDetail submissionDetail, String viewName) {
    var modelAndView = new ModelAndView(viewName)
        .addObject("gasFluidPropertiesSummaryView",
            discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("generalFluidPropertiesSummaryView",
            discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilFluidPropertiesSummaryView",
            discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("reservoirPropertiesSummaryView",
            discoverySummaryService.getReservoirPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("permeabilitySummaryView",
            discoverySummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("generalDetailsSummaryView",
            discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseVolumetricsSummaryView",
            prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseOutputsSummaryView",
            prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCaseVolumetricsSummaryView",
            prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCaseOutputsSummaryView",
            prospectSummaryService.getGasCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("mixedCaseVolumetricsSummaryView",
            prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("mixedCaseOutputsSummaryView",
            prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("drillStemTestSummaryView",
            discoverySummaryService.getDrillStemTestSummaryViewFromSubmissionDetail(submissionDetail));

    return addHydrocarbonsDiscoveredToModelAndView(submissionDetail, modelAndView);
  }

  private ModelAndView addHydrocarbonsDiscoveredToModelAndView(SubmissionDetail submissionDetail, ModelAndView modelAndView) {
    return discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
        .map(discoveryWellDetail -> {
          modelAndView.addObject("gasOnlyDiscovery",
              discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(discoveryWellDetail));
          modelAndView.addObject("oilOnlyDiscovery",
              discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(discoveryWellDetail));
          modelAndView.addObject("mixedDiscovery",
              discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail));
          return modelAndView;
        }).orElseGet(() -> {
          modelAndView.addObject("gasOnlyDiscovery", false);
          modelAndView.addObject("oilOnlyDiscovery", false);
          modelAndView.addObject("mixedDiscovery", false);
          return modelAndView;
        });
  }
}
