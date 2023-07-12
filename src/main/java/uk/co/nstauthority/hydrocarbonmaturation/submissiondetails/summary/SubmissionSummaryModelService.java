package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;

@Service
public class SubmissionSummaryModelService {
  private final ProspectSummaryService prospectSummaryService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final DiscoverySummaryService discoverySummaryService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;
  private final PostDrillDryWellSummaryService postDrillDryWellSummaryService;
  private final SubmissionUpdateService submissionUpdateService;

  @Autowired
  SubmissionSummaryModelService(ProspectSummaryService prospectSummaryService,
                                HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                DiscoverySummaryService discoverySummaryService,
                                DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService,
                                PostDrillDryWellSummaryService postDrillDryWellSummaryService,
                                SubmissionUpdateService submissionUpdateService) {
    this.prospectSummaryService = prospectSummaryService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.discoverySummaryService = discoverySummaryService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
    this.postDrillDryWellSummaryService = postDrillDryWellSummaryService;
    this.submissionUpdateService = submissionUpdateService;
  }

  public ModelAndView createSummaryForSubmissionTypeToModelAndView(SubmissionDetail submissionDetail, String viewName) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE, RELINQUISHMENT_PROSPECT ->
          createProspectSubmissionSummaryModelAndView(submissionDetail, viewName);
      case POST_DRILL_DRY_HOLE ->
          createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_DISCOVERY, UPDATE_DISCOVERY, CREATE_DISCOVERY_FROM_PROSPECT, RELINQUISHMENT_DISCOVERY ->
          createDiscoverySubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_FIELD, CREATE_CSR, CREATE_FDP ->
          // No summary view for fields
          new ModelAndView(viewName);
    };
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
            prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("outlineForMaximumCaseSummaryView",
            prospectSummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail));

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
    addCommonAttributes(submissionDetail, modelAndView);
    return modelAndView;
  }

  public ModelAndView createPostDrillDryWellSubmissionSummaryModelAndView(SubmissionDetail submissionDetail, String viewName) {
    var modelAndView = new ModelAndView(viewName)
        .addObject("dryWellDetailsSummaryView",
            postDrillDryWellSummaryService.getDryWellDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("generalDetailsSummaryView",
            postDrillDryWellSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("permeabilitySummaryView",
            postDrillDryWellSummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("reservoirPropertiesSummaryView",
            postDrillDryWellSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("opportunityDetailsSummaryView",
            prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("outlineForMaximumCaseSummaryView",
            prospectSummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("sourceAndSealSummaryView",
            prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("trapPropertiesSummaryView",
            prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("resourceDetailSummaryView",
            prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail));

    addCommonAttributes(submissionDetail, modelAndView);
    return modelAndView;
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
            discoverySummaryService.getDrillStemTestSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("opportunityDetailsSummaryView",
            prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("outlineForMaximumCaseSummaryView",
            prospectSummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("discoveryWellDetailsSummaryView",
            discoverySummaryService.getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("sourceAndSealSummaryView",
            prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("trapPropertiesSummaryView",
            prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail));

    addCommonAttributes(submissionDetail, modelAndView);
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

  private void addCommonAttributes(SubmissionDetail submissionDetail, ModelAndView modelAndView) {
    modelAndView
        .addObject("accordionId", submissionDetail.getId());

    var submissionUpdateOpt = submissionUpdateService.getLatestUnresolvedUpdateForUpdateSubmissionDetail(submissionDetail);
    submissionUpdateOpt.ifPresent(submissionUpdate ->
        modelAndView
            .addObject("submissionUpdateReason", submissionUpdate.getReason()));
    modelAndView.addObject("isSubmissionUpdate", submissionUpdateOpt.isPresent());

    var resourceDetailSummaryView = prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail);
    modelAndView.addObject("resourceDetailSummaryView", resourceDetailSummaryView);
  }
}
